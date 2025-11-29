package com.mapnaom.foodapp.utils;

import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExcelUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int DEFAULT_SHEET_INDEX = 0;
    private static final int DEFAULT_HEADER_ROW_INDEX = 0;

    // ==================== IMPORT METHODS ====================

    /**
     * Process Excel file and map to list of DTOs
     */
    public <T> List<T> processExcel(MultipartFile file, Class<T> clazz, int sheetIndex, int headerRowIndex)
            throws IOException, ExcelProcessingException {

        validateFileInput(file);

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(inputStream, file.getOriginalFilename());
            Sheet sheet = getSheet(workbook, sheetIndex);
            Map<String, Integer> headerMap = validateAndMapHeaders(sheet, clazz, headerRowIndex);
            return processDataRows(sheet, clazz, headerMap, headerRowIndex + 1);
        }
    }

    public <T> List<T> processExcel(MultipartFile file, Class<T> clazz)
            throws IOException, ExcelProcessingException {
        return processExcel(file, clazz, DEFAULT_SHEET_INDEX, DEFAULT_HEADER_ROW_INDEX);
    }

    public <T> List<T> processExcel(InputStream inputStream, String filename, Class<T> clazz,
                                    int sheetIndex, int headerRowIndex)
            throws IOException, ExcelProcessingException {

        Workbook workbook = createWorkbook(inputStream, filename);
        Sheet sheet = getSheet(workbook, sheetIndex);
        Map<String, Integer> headerMap = validateAndMapHeaders(sheet, clazz, headerRowIndex);
        return processDataRows(sheet, clazz, headerMap, headerRowIndex + 1);
    }

    // ==================== EXPORT METHODS ====================

    /**
     * Generate Excel file from list of DTOs
     */
    public <T> byte[] generateExcel(List<T> data, Class<T> clazz) {
        return generateExcel(data, clazz, "Data", false, null);
    }

    public <T> byte[] generateExcel(List<T> data, Class<T> clazz, String sheetName) {
        return generateExcel(data, clazz, sheetName, false, null);
    }

    public <T> byte[] generateExcel(List<T> data, Class<T> clazz, String sheetName,
                                    boolean rightToLeft, Map<String, String> customHeaders) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<Field> fields = getAllFields(clazz);
            validateFields(fields, clazz);

            Sheet sheet = createStyledSheet(workbook, sheetName, rightToLeft);
            ExcelStyleUtil styleUtil = new ExcelStyleUtil(workbook);

            createHeaderRow(sheet, fields, styleUtil, customHeaders);
            createDataRows(sheet, data, fields, styleUtil, rightToLeft);
            autoSizeColumns(sheet, fields.size());

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel file: " + e.getMessage(), e);
        }
    }

    // ==================== VALIDATION METHODS ====================

    private void validateFileInput(MultipartFile file) throws ExcelProcessingException {
        if (file == null || file.isEmpty()) {
            throw new ExcelProcessingException("Excel file is empty or null");
        }
    }

    private void validateFields(List<Field> fields, Class<?> clazz) {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("No fields found in class: " + clazz.getName());
        }
    }

    private <T> Map<String, Integer> validateAndMapHeaders(Sheet sheet, Class<T> clazz, int headerRowIndex)
            throws ExcelProcessingException {

        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow == null) {
            throw new ExcelProcessingException("Header row not found at index " + headerRowIndex);
        }

        Set<String> dtoFieldNames = getAllFieldNames(clazz);
        Map<String, Integer> headerMap = new HashMap<>();
        List<String> missingFields = new ArrayList<>();
        List<String> unmappedHeaders = new ArrayList<>();

        // Map headers to field names
        for (Cell cell : headerRow) {
            String headerValue = getCellValueAsString(cell).trim();
            if (headerValue.isEmpty()) continue;

            String normalizedHeader = normalizeFieldName(headerValue);
            boolean matched = mapHeaderToField(headerMap, dtoFieldNames, normalizedHeader, cell.getColumnIndex());

            if (!matched) {
                unmappedHeaders.add(headerValue);
            }
        }

        // Check for required fields
        checkRequiredFields(clazz, headerMap, missingFields);

        if (!missingFields.isEmpty()) {
            throw new ExcelProcessingException(
                    "Required fields missing in Excel: " + String.join(", ", missingFields)
            );
        }

        logUnmappedHeaders(unmappedHeaders);
        return headerMap;
    }

    private boolean mapHeaderToField(Map<String, Integer> headerMap, Set<String> dtoFieldNames,
                                     String normalizedHeader, int columnIndex) {
        for (String fieldName : dtoFieldNames) {
            if (normalizeFieldName(fieldName).equalsIgnoreCase(normalizedHeader)) {
                headerMap.put(fieldName, columnIndex);
                return true;
            }
        }
        return false;
    }

    private <T> void checkRequiredFields(Class<T> clazz, Map<String, Integer> headerMap,
                                         List<String> missingFields) {
        for (Field field : getAllFields(clazz)) {
            if (isRequiredField(field) && !headerMap.containsKey(field.getName())) {
                missingFields.add(field.getName());
            }
        }
    }

    private void logUnmappedHeaders(List<String> unmappedHeaders) {
        if (!unmappedHeaders.isEmpty()) {
            System.out.printf("Warning: Unmapped Excel headers: %s%n",
                    String.join(", ", unmappedHeaders));
        }
    }

    // ==================== DATA PROCESSING ====================

    private <T> List<T> processDataRows(Sheet sheet, Class<T> clazz,
                                        Map<String, Integer> headerMap, int startRowIndex)
            throws ExcelProcessingException {

        List<T> result = new ArrayList<>();
        List<ExcelError> errors = new ArrayList<>();

        for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isEmptyRow(row)) continue;

            try {
                T dto = mapRowToDTO(row, clazz, headerMap);
                result.add(dto);
            } catch (Exception e) {
                errors.add(new ExcelError(i + 1, e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new ExcelProcessingException("Errors processing rows", errors);
        }

        return result;
    }

    private <T> T mapRowToDTO(Row row, Class<T> clazz, Map<String, Integer> headerMap)
            throws ReflectiveOperationException {

        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            Cell cell = row.getCell(entry.getValue());
            if (cell != null) {
                setFieldValue(instance, entry.getKey(), cell);
            }
        }

        return instance;
    }

    private <T> void setFieldValue(T instance, String fieldName, Cell cell)
            throws ReflectiveOperationException {

        Field field = getFieldByName(instance.getClass(), fieldName);
        if (field == null) return;

        field.setAccessible(true);
        Object value = getCellValue(cell, field.getType());

        try {
            field.set(instance, value);
        } catch (IllegalArgumentException e) {
            // Try setter method as fallback
            invokeSetter(instance, fieldName, value, field.getType());
        }
    }

    private <T> void invokeSetter(T instance, String fieldName, Object value, Class<?> fieldType)
            throws ReflectiveOperationException {
        String setterName = "set" + capitalize(fieldName);
        Method setter = instance.getClass().getMethod(setterName, fieldType);
        setter.invoke(instance, value);
    }

    // ==================== CELL VALUE EXTRACTION ====================

    private Object getCellValue(Cell cell, Class<?> targetType) {
        if (cell == null) return null;

        if (targetType == String.class) return getCellValueAsString(cell);
        if (targetType == Integer.class || targetType == int.class) return getCellValueAsInteger(cell);
        if (targetType == Long.class || targetType == long.class) return getCellValueAsLong(cell);
        if (targetType == Double.class || targetType == double.class) return getCellValueAsDouble(cell);
        if (targetType == Float.class || targetType == float.class) return getCellValueAsFloat(cell);
        if (targetType == Boolean.class || targetType == boolean.class) return getCellValueAsBoolean(cell);
        if (targetType == Date.class) return getCellValueAsDate(cell);
        if (targetType == LocalDate.class) return getCellValueAsLocalDate(cell);
        if (targetType == LocalDateTime.class) return getCellValueAsLocalDateTime(cell);
        if (targetType.isEnum()) return getCellValueAsEnum(cell, targetType);

        return getCellValueAsString(cell);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> formatNumericCell(cell);
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> evaluateFormula(cell);
            default -> "";
        };
    }

    private String formatNumericCell(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return new SimpleDateFormat(DATE_FORMAT).format(cell.getDateCellValue());
        }
        double numericValue = cell.getNumericCellValue();
        return numericValue == (long) numericValue
                ? String.valueOf((long) numericValue)
                : String.valueOf(numericValue);
    }

    private String evaluateFormula(Cell cell) {
        try {
            return cell.getRichStringCellValue().getString();
        } catch (Exception e) {
            return "";
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> parseInteger(cell.getStringCellValue());
            default -> null;
        };
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> parseLong(cell.getStringCellValue());
            default -> null;
        };
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> parseDouble(cell.getStringCellValue());
            default -> null;
        };
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float getCellValueAsFloat(Cell cell) {
        Double value = getCellValueAsDouble(cell);
        return value != null ? value.floatValue() : null;
    }

    private Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING -> parseBooleanString(cell.getStringCellValue());
            case NUMERIC -> cell.getNumericCellValue() != 0;
            default -> null;
        };
    }

    private Boolean parseBooleanString(String value) {
        String normalized = value.trim().toLowerCase();
        return "true".equals(normalized) || "yes".equals(normalized) ||
                "1".equals(normalized) || "بله".equals(normalized);
    }

    private Date getCellValueAsDate(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : null;
            case STRING -> parseDate(cell.getStringCellValue());
            default -> null;
        };
    }

    private Date parseDate(String value) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getCellValueAsLocalDate(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate()
                    : null;
            case STRING -> parseLocalDate(cell.getStringCellValue());
            default -> null;
        };
    }

    private LocalDate parseLocalDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime getCellValueAsLocalDateTime(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue()
                    : null;
            case STRING -> parseLocalDateTime(cell.getStringCellValue());
            default -> null;
        };
    }

    private LocalDateTime parseLocalDateTime(String value) {
        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Object getCellValueAsEnum(Cell cell, Class<?> enumType) {
        String value = getCellValueAsString(cell);
        if (value == null || value.trim().isEmpty()) return null;

        try {
            return Enum.valueOf((Class<Enum>) enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ==================== EXCEL CREATION ====================

    private Sheet createStyledSheet(Workbook workbook, String sheetName, boolean rightToLeft) {
        Sheet sheet = workbook.createSheet(sheetName);
        if (rightToLeft) {
            sheet.setRightToLeft(true);
        }
        return sheet;
    }

    private void createHeaderRow(Sheet sheet, List<Field> fields, ExcelStyleUtil styleUtil,
                                 Map<String, String> customHeaders) {
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < fields.size(); i++) {
            Cell cell = headerRow.createCell(i);
            String fieldName = fields.get(i).getName();
            String headerName = getHeaderName(fieldName, customHeaders);
            cell.setCellValue(headerName);
            cell.setCellStyle(styleUtil.getHeaderStyle());
        }
    }

    private String getHeaderName(String fieldName, Map<String, String> customHeaders) {
        if (customHeaders != null && customHeaders.containsKey(fieldName)) {
            return customHeaders.get(fieldName);
        }
        return formatFieldName(fieldName);
    }

    private <T> void createDataRows(Sheet sheet, List<T> data, List<Field> fields,
                                    ExcelStyleUtil styleUtil, boolean rightToLeft) {
        int rowNum = 1;

        for (T dto : data) {
            Row row = sheet.createRow(rowNum++);

            for (int i = 0; i < fields.size(); i++) {
                Cell cell = row.createCell(i);
                Object value = getFieldValue(dto, fields.get(i));
                setCellValueWithAutoStyle(cell, value, styleUtil, rightToLeft);
            }
        }
    }

    private <T> Object getFieldValue(T instance, Field field) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return getFieldValueViaGetter(instance, field);
        }
    }

    private <T> Object getFieldValueViaGetter(T instance, Field field) {
        try {
            String getterName = "get" + capitalize(field.getName());
            Method getter = instance.getClass().getMethod(getterName);
            return getter.invoke(instance);
        } catch (Exception e) {
            return null;
        }
    }

    private void setCellValueWithAutoStyle(Cell cell, Object value, ExcelStyleUtil styleUtil,
                                           boolean rightToLeft) {
        if (value == null) {
            cell.setCellValue("");
            cell.setCellStyle(rightToLeft ? styleUtil.getDataStyle() : styleUtil.getDataStyleLeft());
            return;
        }

        CellStyle style = determineStyle(value, styleUtil, rightToLeft);
        setCellValue(cell, value);
        cell.setCellStyle(style);
    }

    private CellStyle determineStyle(Object value, ExcelStyleUtil styleUtil, boolean rightToLeft) {
        if (value instanceof Integer || value instanceof Long) {
            return styleUtil.getPriceStyle();
        } else if (value instanceof Double || value instanceof Float) {
            return styleUtil.getDecimalStyle(2);
        } else if (value instanceof Date || value instanceof LocalDate || value instanceof LocalDateTime) {
            return styleUtil.getDateStyle(DATE_FORMAT);
        } else if (value instanceof Boolean) {
            return styleUtil.getBooleanStyle();
        }
        return rightToLeft ? styleUtil.getDataStyle() : styleUtil.getDataStyleLeft();
    }

    private void setCellValue(Cell cell, Object value) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue(value.toString());
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== UTILITY METHODS ====================

    private Workbook createWorkbook(InputStream inputStream, String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        if (filename.endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (filename.endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException(
                    "Invalid file format. Only .xls and .xlsx are supported. Received: " + filename);
        }
    }

    private Sheet getSheet(Workbook workbook, int sheetIndex) throws ExcelProcessingException {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) {
            throw new ExcelProcessingException("Sheet at index " + sheetIndex + " not found");
        }
        return sheet;
    }

    private Set<String> getAllFieldNames(Class<?> clazz) {
        return getAllFields(clazz).stream()
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    private Field getFieldByName(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        return null;
    }

    private boolean isRequiredField(Field field) {
        return field.getAnnotation(Required.class) != null ||
                field.getAnnotation(NotNull.class) != null;
    }

    private String normalizeFieldName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        for (Cell cell : row) {
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String formatFieldName(String fieldName) {
        String result = fieldName.replaceAll("([A-Z])", " $1").trim();
        return capitalize(result);
    }

    // ==================== INNER CLASSES ====================

    public static class ExcelProcessingException extends Exception {
        private final List<ExcelError> errors;

        public ExcelProcessingException(String message) {
            super(message);
            this.errors = new ArrayList<>();
        }

        public ExcelProcessingException(String message, List<ExcelError> errors) {
            super(message);
            this.errors = errors;
        }

        public List<ExcelError> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }

    public static class ExcelError {
        private final int rowNumber;
        private final String message;

        public ExcelError(int rowNumber, String message) {
            this.rowNumber = rowNumber;
            this.message = message;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Row " + rowNumber + ": " + message;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Required {}
}