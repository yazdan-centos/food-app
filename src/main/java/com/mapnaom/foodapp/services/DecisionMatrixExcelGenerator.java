package com.mapnaom.foodapp.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class DecisionMatrixExcelGenerator {

    public void generateExcel(String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Sheet 1: Strategic Weights
        createWeightsSheet(workbook);

        // Sheet 2: Project Evaluation
        createEvaluationSheet(workbook);

        // Sheet 3: Decision Matrix
        createDecisionMatrixSheet(workbook);

        // Sheet 4: Scoring Guide
        createScoringGuideSheet(workbook);

        // Sheet 5: Multiple Projects Comparison
        createMultiProjectSheet(workbook);

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private void createWeightsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("وزن‌های استراتژیک");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("معیار");
        headerRow.createCell(1).setCellValue("وزن (از ۱۰۰)");
        headerRow.createCell(2).setCellValue("درصد");

        // Data rows
        String[] criteria = {"اهمیت استراتژیک", "منحصربه‌فردی فرآیند",
                "حاکمیت داده و امنیت", "فوریت نیاز"};
        int[] weights = {40, 25, 20, 15};

        for (int i = 0; i < criteria.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(criteria[i]);
            row.createCell(1).setCellValue(weights[i]);

            Cell percentCell = row.createCell(2);
            percentCell.setCellFormula("B" + (i + 2) + "/100");
        }

        // Sum row
        Row sumRow = sheet.createRow(5);
        sumRow.createCell(0).setCellValue("جمع کل");
        sumRow.createCell(1).setCellFormula("SUM(B2:B5)");
        sumRow.createCell(2).setCellFormula("SUM(C2:C5)");

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createEvaluationSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("ارزیابی پروژه");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("معیار");
        headerRow.createCell(1).setCellValue("وزن");
        headerRow.createCell(2).setCellValue("امتیاز (۱-۵)");
        headerRow.createCell(3).setCellValue("امتیاز وزنی");
        headerRow.createCell(4).setCellValue("توضیحات");

        // Data rows
        String[] criteria = {"اهمیت استراتژیک", "منحصربه‌فردی فرآیند",
                "حاکمیت داده و امنیت", "فوریت نیاز"};
        double[] weights = {0.40, 0.25, 0.20, 0.15};
        int[] scores = {4, 3, 5, 2};

        for (int i = 0; i < criteria.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(criteria[i]);
            row.createCell(1).setCellValue(weights[i]);
            row.createCell(2).setCellValue(scores[i]);

            Cell weightedCell = row.createCell(3);
            weightedCell.setCellFormula("B" + (i + 2) + "*C" + (i + 2));
        }

        // Final score row
        Row finalScoreRow = sheet.createRow(6);
        finalScoreRow.createCell(0).setCellValue("امتیاز نهایی");
        finalScoreRow.createCell(3).setCellFormula("SUM(D2:D5)");

        // Recommended approach row
        Row approachRow = sheet.createRow(7);
        approachRow.createCell(0).setCellValue("رویکرد توصیه‌شده");
        Cell approachCell = approachRow.createCell(3);
        approachCell.setCellFormula(
                "IF(D7>=4,\"🔧 تولید داخلی\",IF(D7>=3,\"🔄 ترکیبی\",IF(D7>=2,\"🛒 خرید + سفارشی‌سازی\",\"☁️ برون‌سپاری کامل\")))"
        );

        // Auto-size columns
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDecisionMatrixSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("ماتریس تصمیم‌گیری");

        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("محدوده امتیاز");
        headerRow.createCell(1).setCellValue("رویکرد توصیه‌شده");
        headerRow.createCell(2).setCellValue("توضیح مختصر");

        // Data
        Object[][] data = {
                {"4.0 - 5.0", "🔧 تولید داخلی", "حیاتی، منحصربه‌فرد، حساس"},
                {"3.0 - 3.9", "🔄 ترکیبی", "مهم اما امکان استفاده از محصولات موجود"},
                {"2.0 - 2.9", "🛒 خرید + سفارشی‌سازی", "استاندارد با نیازهای خاص محدود"},
                {"1.0 - 1.9", "☁️ برون‌سپاری کامل", "غیرحیاتی، کاملاً استاندارد"}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                row.createCell(j).setCellValue(data[i][j].toString());
            }
        }

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createScoringGuideSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("راهنمای امتیازدهی");

        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("معیار");
        headerRow.createCell(1).setCellValue("امتیاز");
        headerRow.createCell(2).setCellValue("توضیح");

        // Data - Strategic Importance
        String[][] strategicData = {
                {"اهمیت استراتژیک", "1", "هیچ تأثیر مستقیمی بر ماموریت اصلی"},
                {"اهمیت استراتژیک", "2", "نقش پشتیبانی عمومی"},
                {"اهمیت استراتژیک", "3", "بخشی از زنجیره ارزش"},
                {"اهمیت استراتژیک", "4", "تأثیر مستقیم بر اهداف کلیدی"},
                {"اهمیت استراتژیک", "5", "در هسته اصلی ماموریت"}
        };

        // Add all scoring data
        int rowNum = 1;
        for (String[] data : strategicData) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < data.length; i++) {
                row.createCell(i).setCellValue(data[i]);
            }
        }

        // Continue with other criteria...
        // (Similar pattern for uniqueness, data governance, urgency)

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createMultiProjectSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("مقایسه چند پروژه");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"نام پروژه", "معیار۱ (۱-۵)", "معیار۲ (۱-۵)",
                "معیار۳ (۱-۵)", "معیار۴ (۱-۵)", "امتیاز نهایی",
                "رویکرد توصیه‌شده", "اولویت"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Sample projects
        Object[][] projects = {
                {"پروژه A", 4, 3, 5, 2},
                {"پروژه B", 5, 4, 5, 3},
                {"پروژه C", 2, 2, 3, 1}
        };

        for (int i = 0; i < projects.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(projects[i][0].toString());

            for (int j = 1; j < 5; j++) {
                row.createCell(j).setCellValue((Integer) projects[i][j]);
            }

            // Final score formula
            Cell scoreCell = row.createCell(5);
            scoreCell.setCellFormula("B" + (i + 2) + "*0.4+C" + (i + 2) +
                    "*0.25+D" + (i + 2) + "*0.2+E" + (i + 2) + "*0.15");

            // Approach formula
            Cell approachCell = row.createCell(6);
            approachCell.setCellFormula(
                    "IF(F" + (i + 2) + ">=4,\"🔧 تولید داخلی\",IF(F" + (i + 2) +
                            ">=3,\"🔄 ترکیبی\",IF(F" + (i + 2) + ">=2,\"🛒 خرید + سفارشی‌سازی\",\"☁️ برون‌سپاری کامل\")))"
            );

            // Priority ranking
            Cell priorityCell = row.createCell(7);
            priorityCell.setCellFormula("RANK(F" + (i + 2) + ",$F$2:$F$4,0)");
        }

        // Auto-size columns
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
