package com.mapnaom.foodapp.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelFileReader {

    /**
     * Reads data from an Excel file and returns it as a list of lists of strings.
     * Each inner list represents a row, and each string in the inner list represents a cell value.
     *
     * @param filePath The path to the Excel file.
     * @return A list of lists of strings representing the data in the Excel file.
     * @throws IOException If an error occurs while reading the file.
     */
    public List<List<String>> readExcelFile(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();

        try (FileInputStream excelFile = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(excelFile)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet

            for (Row currentRow : sheet) {
                Iterator<Cell> cellIterator = currentRow.iterator();

                List<String> rowData = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    rowData.add(getCellValueAsString(currentCell));
                }
                data.add(rowData);
            }
        }
        return data;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> ""; // Handle other cell types or return null as needed
        };
    }
}
