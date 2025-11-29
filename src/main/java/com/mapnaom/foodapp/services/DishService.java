package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.DishDto;
import com.mapnaom.foodapp.dtos.DishExcelImportDto;
import com.mapnaom.foodapp.exceptions.DishHasAssociatedDailyMealsException;
import com.mapnaom.foodapp.mappers.DishMapper;
import com.mapnaom.foodapp.models.Dish;
import com.mapnaom.foodapp.dtos.SelectOption;
import com.mapnaom.foodapp.repositories.DailyMealDishRepository;
import com.mapnaom.foodapp.repositories.DishRepository;
import com.mapnaom.foodapp.searchForms.DishSearchForm;
import com.mapnaom.foodapp.specifications.DishSpecification;
import com.mapnaom.foodapp.utils.ExcelUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final ExcelUtil excelUtil;
    private final DailyMealDishRepository dailyMealDishRepository;

    public DishDto createDish(@NotNull DishDto dishDto) {
        // Check if a dish with the same name and price already exists
        if (dishRepository.existsDishByNameAndPrice(dishDto.getName(), dishDto.getPrice())) {
            throw new IllegalArgumentException(
                    "یک غذا با نام '%s' و قیمت '%d' قبلاً وجود دارد.".formatted(dishDto.getName(), dishDto.getPrice())
            );
        }

        Dish dish = dishMapper.toEntity(dishDto);
        Dish savedDish = dishRepository.save(dish);
        return dishMapper.toDto(savedDish);
    }

    public DishDto getDishById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Dish ID for lookup cannot be null.");
        }
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: %d".formatted(id)));
        return dishMapper.toDto(dish);
    }

    public Page<DishDto> searchDishes(DishSearchForm form, int page, int size, String sortBy, String order) {
        // Build Pageable with sort
        Sort sort = order.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Execute the query using the specification + pageable
        Page<Dish> dishPage = dishRepository.findAll(
                DishSpecification.getSpecification(form),
                pageRequest
        );

        // Convert entities -> DTOs
        return dishPage.map(dishMapper::toDto);
    }
    public List<SelectOption> selectOptionsDishes(DishSearchForm form) {
        List<Dish> dishList = dishRepository.findAll(DishSpecification.getSpecification(form));
        return dishList.stream()
                .map(dish -> new SelectOption(dish.getId(),
                        "%s - %d ریال".formatted(dish.getName(), dish.getPrice())))
                .collect(Collectors.toList());
    }


    @Transactional

    public DishDto updateDish(Long id, DishDto dishDto) {
        if (id == null) {
            throw new IllegalArgumentException("Dish ID for update cannot be null.");
        }
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("غذایی با شناسه %d یافت نشد.".formatted(id)));

        // Use the new name and price for the check, falling back to the existing values if not provided in the DTO
        String newName = dishDto.getName() != null ? dishDto.getName() : dish.getName();
        Integer newPrice = dishDto.getPrice() != null ? dishDto.getPrice() : dish.getPrice();

        // Check if another dish with the same name and price already exists
        if (dishRepository.existsDishByNameAndPriceAndIdNot(newName, newPrice, id)) {
            throw new IllegalArgumentException(
                    "یک غذا با نام '%s' و قیمت '%d' قبلاً وجود دارد.".formatted(newName, newPrice)
            );
        }

        // Apply the partial updates from the DTO to the entity
        dishMapper.partialUpdate(dishDto, dish);
        Dish updatedDish = dishRepository.save(dish);
        return dishMapper.toDto(updatedDish);
    }

    public void deleteDish(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Dish ID for deletion cannot be null.");
        }
        Dish dish = dishRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("غذایی با شناسه %d یافت نشد.".formatted(id)));
        if (dailyMealDishRepository.existsAllByDishId(id)) {
            String dishName = dish.getName();
            Integer price = dish.getPrice();
            throw new DishHasAssociatedDailyMealsException(dishName, price);
        }
        dishRepository.deleteById(id);
    }

    /**
     * Imports dishes from an Excel file
     *
     * @param file The Excel file containing dish data
     * @return List of imported dishes
     * @throws IllegalArgumentException if the file format is invalid
     */
    public List<DishDto> importDishesFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("فایل نمی‌تواند خالی باشد.");
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) &&
                !"application/vnd.ms-excel".equals(contentType)) {
            throw new IllegalArgumentException("فقط فایل‌های اکسل (.xls یا .xlsx) پشتیبانی می‌شوند.");
        }

        try {
            // Process Excel file and map to DishDto objects
            List<DishExcelImportDto> dishDtos = excelUtil.processExcel(file, DishExcelImportDto.class);

            // Validate and save each dish
            return dishDtos.stream()
                    .map(this::validateAndSaveDish)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("خطا در خواندن فایل اکسل: %s".formatted(e.getMessage()), e);
        } catch (ExcelUtil.ExcelProcessingException e) {
            throw new IllegalArgumentException("خطا در پردازش فایل اکسل: %s".formatted(e.getMessage()));
        }
    }

    /**
     * Validates and saves a single dish
     */
    private DishDto validateAndSaveDish(DishExcelImportDto dishDto) {
        // Validate required fields
        if (dishDto.getName() == null || dishDto.getName().isEmpty()) {
            throw new IllegalArgumentException("نام غذا نمی‌تواند خالی باشد.");
        }
        if (dishDto.getPrice() == null) {
            throw new IllegalArgumentException("قیمت غذا نمی‌تواند خالی باشد.");
        }

        // Check for duplicate dish name and price combination
        if (dishRepository.existsDishByNameAndPrice(dishDto.getName(), dishDto.getPrice())) {
            throw new IllegalArgumentException(
                    String.format("غذایی با نام '%s' و قیمت '%d' قبلاً وجود دارد.", dishDto.getName(), dishDto.getPrice())
            );
        }

        // Save the dish
        Dish dish = dishRepository.save(new Dish(dishDto.getName(), dishDto.getPrice()));
        return new DishDto(dish.getId(), dish.getName(), dish.getPrice());
    }

    public byte[] exportDishesToExcel(DishSearchForm form, String sortBy, String order) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sort sort = order.equalsIgnoreCase("DESC")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            List<Dish> dishList = dishRepository.findAll(DishSpecification.getSpecification(form), sort);

            if (dishList.size() > 10000) {
                throw new IllegalArgumentException("امکان گرفتن خروجی بیش از 10,000 غذا به صورت همزمان وجود ندارد.");
            }

            Sheet sheet = workbook.createSheet("Dishes");
            // Set sheet to right-to-left
            sheet.setRightToLeft(true);

            // --- Define Cell Styles ---
            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex()); // Example color
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Add all borders to header style
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);


            // Data Cell Style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.RIGHT); // Align data to the right for RTL
            // Add all borders to data style
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);


            // Price Cell Style (Currency)
            CellStyle priceStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            priceStyle.setDataFormat(format.getFormat("#,##0")); // Example: format as number with thousands separator
            priceStyle.setAlignment(HorizontalAlignment.RIGHT); // Align numbers to the right
            // Add all borders to price style
            priceStyle.setBorderBottom(BorderStyle.THIN);
            priceStyle.setBorderTop(BorderStyle.THIN);
            priceStyle.setBorderLeft(BorderStyle.THIN);
            priceStyle.setBorderRight(BorderStyle.THIN);

            // --- Apply Styles to Header Row ---
            Row headerRow = sheet.createRow(0);
            Cell cellId = headerRow.createCell(0);
            cellId.setCellValue("ID");
            cellId.setCellStyle(headerStyle);

            Cell cellName = headerRow.createCell(1);
            cellName.setCellValue("Name");
            cellName.setCellStyle(headerStyle);

            Cell cellPriceHeader = headerRow.createCell(2);
            cellPriceHeader.setCellValue("Price");
            cellPriceHeader.setCellStyle(headerStyle);

            int rowIdx = 1;
            for (Dish dish : dishList) {
                Row row = sheet.createRow(rowIdx++);

                Cell idCell = row.createCell(0);
                idCell.setCellValue(dish.getId() != null ? dish.getId() : 0);
                idCell.setCellStyle(dataStyle); // Apply data style

                Cell nameCell = row.createCell(1);
                nameCell.setCellValue(dish.getName() != null ? dish.getName() : "");
                nameCell.setCellStyle(dataStyle); // Apply data style

                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(dish.getPrice() != null ? dish.getPrice() : 0);
                priceCell.setCellStyle(priceStyle); // Apply price style
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("خطا در صدور اطلاعات غذاها به اکسل: %s".formatted(e.getMessage()), e);
        }
    }


}
