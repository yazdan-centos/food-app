package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.services.DecisionMatrixExcelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/decision-matrix")
@RequiredArgsConstructor
public class DecisionMatrixExcelController {

    private final DecisionMatrixExcelGenerator excelGenerator;

    /**
     * Generate the decision matrix Excel file and return it as a downloadable attachment.
     * The file is created in a temporary location and deleted after being read.
     *
     * Example:
     * GET /api/decision-matrix/excel
     * GET /api/decision-matrix/excel?filename=my-report.xlsx
     */
    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam(name = "filename", required = false) String filename) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("decision-matrix-", ".xlsx");
            excelGenerator.generateExcel(tempFile.toString());

            byte[] fileBytes = Files.readAllBytes(tempFile);

            String finalName = (filename != null && !filename.isBlank())
                    ? sanitizeFilename(filename)
                    : defaultFilename();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename(finalName).build());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate Excel file", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // If deletion fails, we silently ignore.
                }
            }
        }
    }

    /**
     * Generate the decision matrix Excel file and persist it to a specific path on the server.
     *
     * Example:
     * POST /api/decision-matrix/excel?path=/tmp/decision-matrix.xlsx
     */
    @PostMapping("/excel")
    public ResponseEntity<String> createExcelOnServer(@RequestParam("path") String filePath) {
        try {
            excelGenerator.generateExcel(filePath);
            return ResponseEntity.ok("Excel generated successfully at: " + filePath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate Excel file at path", e);
        }
    }

    private String defaultFilename() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return "decision-matrix-" + timestamp + ".xlsx";
    }

    /**
     * Basic filename sanitization to avoid header injection or invalid names.
     */
    private String sanitizeFilename(String filename) {
        String cleaned = filename.replaceAll("[\\r\\n]", "").trim();
        if (!cleaned.toLowerCase().endsWith(".xlsx")) {
            cleaned = cleaned + ".xlsx";
        }
        return cleaned;
    }
}
