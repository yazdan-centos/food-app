
package com.mapnaom.foodapp.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;

public class ExcelStyleUtil {
    private final Workbook workbook;

    public ExcelStyleUtil(Workbook workbook) {
        this.workbook = workbook;
    }

    public CellStyle getHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public CellStyle getDataStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    public CellStyle getDataStyleLeft() {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    public CellStyle getPriceStyle() {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    public CellStyle getDecimalStyle(int decimals) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        StringBuilder pattern = new StringBuilder("#,##0");
        if (decimals > 0) {
            pattern.append(".");
            for (int i = 0; i < decimals; i++) pattern.append("0");
        }
        style.setDataFormat(format.getFormat(pattern.toString()));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    public CellStyle getDateStyle(String dateFormat) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat(dateFormat));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    public CellStyle getBooleanStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
