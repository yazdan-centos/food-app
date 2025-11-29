package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.PersonnelDto;
import com.mapnaom.foodapp.dtos.SelectOption;
import com.mapnaom.foodapp.exceptions.PersonnelAlreadyExistsException;
import com.mapnaom.foodapp.exceptions.PersonnelHasReservationsException;
import com.mapnaom.foodapp.exceptions.ResourceNotFoundException;
import com.mapnaom.foodapp.mappers.PersonnelMapper;
import com.mapnaom.foodapp.models.Personnel;
import com.mapnaom.foodapp.repositories.PersonnelRepository;
import com.mapnaom.foodapp.repositories.ReservationRepository;
import com.mapnaom.foodapp.specifications.PersonnelSpecification;
import com.mapnaom.foodapp.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final ExcelUtil excelUtil;
    private final ReservationRepository reservationRepository;


    /**
     * Searches for Personnel based on the provided criteria with pagination and sorting.
     *
     * @param string the search string containing filter criteria.
     * @param page   zero-based page index.
     * @param size   the size of the page to be returned.
     * @param sortBy the property to sort by.
     * @param order  sort direction, "ASC" for ascending and "DESC" for descending.
     * @return a Page of PersonnelDto objects matching the given criteria.
     */
    public Page<PersonnelDto> searchPersonnel(String string, int page, int size, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Personnel> personnelPage = personnelRepository.findAll(PersonnelSpecification.withFilterByFullName(string), pageRequest);
        return personnelPage.map(personnelMapper::toDto);
    }

    public List<SelectOption> selectPersonnel(String searchKeyword) {
        return personnelRepository.findAll(PersonnelSpecification.withFilterByFullName(searchKeyword))
                .stream()
                .map(personnel -> new SelectOption(
                        personnel.getId(),
                        "%s %s".formatted(personnel.getFirstName(), personnel.getLastName())
                ))
                .collect(Collectors.toList());
    }

    public PersonnelDto getPersonnelById(Long id) {
        final var optional = personnelRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("پرسنل با شناسه %d یافت نشد.".formatted(id), "id", id);
            } else {
            return personnelMapper.toDto(optional.get());
        }
    }

    public PersonnelDto createPersonnel(PersonnelDto personnelDto) {
        if (personnelRepository.existsPersonnelByUsername(personnelDto.getUsername()))
            throw new PersonnelAlreadyExistsException("پرسنل با نام کاربری %s وجود دارد".formatted(personnelDto.getUsername()));
        if (personnelRepository.existsByPersCode(personnelDto.getPersCode()))
            throw new PersonnelAlreadyExistsException("پرسنل با کد پرسنلی %s وجود دارد".formatted(personnelDto.getPersCode()));
        Personnel personnel = personnelMapper.toEntity(personnelDto);
        Personnel savedPersonnel = personnelRepository.save(personnel);
        return personnelMapper.toDto(savedPersonnel);
    }

    @Transactional
    public PersonnelDto updatePersonnel(Long id, PersonnelDto personnelDto) {
        // Find the existing personnel
        Personnel existingPersonnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: %d".formatted(id)));

        if (personnelRepository.existsPersonnelByUsernameAndIdIsNot(personnelDto.getUsername(), id)) {
            throw new PersonnelAlreadyExistsException("پرسنل با نام کاربری %s وجود دارد".formatted(personnelDto.getUsername()));
        }
        if (personnelRepository.existsPersonnelByPersCodeAndIdIsNot(personnelDto.getPersCode(), id)) {
            throw new PersonnelAlreadyExistsException("پرسنل با کد پرسنلی %s وجود دارد".formatted(personnelDto.getPersCode()));
        }

        // Use the mapper's partialUpdate method to update only the provided fields
        Personnel updatedPersonnel = personnelMapper.partialUpdate(personnelDto, existingPersonnel);

        // Save the updated entity
        Personnel savedPersonnel = personnelRepository.save(updatedPersonnel);

        // Convert back to DTO and return
        return personnelMapper.toDto(savedPersonnel);
    }

    public void deletePersonnel(Long id) {
        Personnel findedPersonnel = personnelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: %d".formatted(id)));
        if (reservationRepository.existsAllByPersonnel_Id(id)) {
            throw new PersonnelHasReservationsException(
                    findedPersonnel.getPersCode(),
                    String.format("%s %s", findedPersonnel.getFirstName(), findedPersonnel.getLastName()));
        }
        personnelRepository.deleteById(id);
    }

    @Transactional
    public List<PersonnelDto> importFromExcel(MultipartFile file) {
        List<PersonnelDto> successfulImports = new ArrayList<>();
        List<PersonnelDto> failedImports = new ArrayList<>();

        try {
            // Process Excel file and map to DTOs
            List<PersonnelDto> personnelDtos = excelUtil.processExcel(file, PersonnelDto.class);

            // Process each DTO
            for (PersonnelDto dto : personnelDtos) {
                try {
                    Personnel personnel = personnelMapper.toEntity(dto);
                    Personnel savedPersonnel = personnelRepository.save(personnel);
                    successfulImports.add(personnelMapper.toDto(savedPersonnel));
                } catch (DataIntegrityViolationException e) {
                    String errorMessage;
                    if (e.getMessage().contains("username")) {
                        errorMessage = String.format("نام کاربری %s از قبل وجود دارد", dto.getUsername());
                    } else {
                        errorMessage = String.format("ترکیب تکراری کد پرسنلی %s، نام %s و نام خانوادگی %s وجود دارد",
                                dto.getPersCode(), dto.getFirstName(), dto.getLastName());
                    }
                    log.error("وارد کردن پرسنل با شکست مواجه شد: {} - دلیل: {}", dto, errorMessage);
                    failedImports.add(dto);
                } catch (Exception e) {
                    log.error("خطای غیرمنتظره هنگام وارد کردن پرسنل: {}", dto, e);
                    failedImports.add(dto);
                }
            }

            // Log import results
            log.info("وارد کردن فایل اکسل به پایان رسید. واردهای موفق: {}, واردهای ناموفق: {}",
                    Optional.of(successfulImports.size()), Optional.of(failedImports.size()));

            return successfulImports;

        } catch (IOException e) {
            String errorMessage = "خطایی هنگام خواندن فایل اکسل رخ داد: %s".formatted(e.getMessage());
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        } catch (ExcelUtil.ExcelProcessingException e) {
            String errorMessage = "هنگام پردازش فایل اکسل خطایی رخ داد: %s".formatted(e.getMessage());
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
    public byte[] exportToExcel() {
        List<Personnel> personnelList = personnelRepository.findAll(Sort.by("id").ascending());
        List<PersonnelDto> personnelDtos = personnelList.stream()
                .map(personnelMapper::toDto)
                .collect(Collectors.toList());
        return excelUtil.generateExcel(personnelDtos, PersonnelDto.class);
    }


}
