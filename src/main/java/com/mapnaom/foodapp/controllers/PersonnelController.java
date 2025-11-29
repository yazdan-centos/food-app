package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.PersonnelDto;
import com.mapnaom.foodapp.dtos.SelectOption;
import com.mapnaom.foodapp.exceptions.PersonnelHasReservationsException;
import com.mapnaom.foodapp.searchForms.PersonnelSearchForm;
import com.mapnaom.foodapp.services.PersonnelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for managing Personnel.
 * <p>
 * This controller provides endpoints for creating, retrieving, updating, and deleting personnel records.
 * It delegates business logic to the {@link PersonnelService}.
 * </p>
 */
@CrossOrigin
@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
@Tag(name = "Personnel", description = "Personnel management")
public class PersonnelController {

    private final PersonnelService personnelService;

    /**
     * Creates a new personnel record.
     *
     * @param personnelDto the personnel data transfer object containing the details to create.
     * @return a {@link ResponseEntity} containing the created personnel and HTTP status 201 (Created).
     */
    @PostMapping(path={"","/"})
    @Operation(summary = "Create a new personnel record")
    public ResponseEntity<PersonnelDto> createPersonnel(@RequestBody PersonnelDto personnelDto) {
        PersonnelDto createdPersonnel = personnelService.createPersonnel(personnelDto);
        return new ResponseEntity<>(createdPersonnel, HttpStatus.CREATED);
    }

    /**
     * Retrieves a personnel record by its identifier.
     *
     * @param id the unique identifier of the personnel.
     * @return a {@link ResponseEntity} containing the personnel data and HTTP status 200 (OK).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a personnel record by ID")
    public ResponseEntity<PersonnelDto> getPersonnelById(@PathVariable Long id) {
        PersonnelDto personnelDto = personnelService.getPersonnelById(id);
        return new ResponseEntity<>(personnelDto, HttpStatus.OK);
    }

    /**
     * Retrieves all personnel records.
     *
     * @return a {@link ResponseEntity} containing a list of all personnel and HTTP status 200 (OK).
     */
    /**
     * Searches Personnel by various criteria with pagination and sorting.
     *
     * @param page   zero-based page index (default=0).
     * @param size   the size of the page to be returned (default=10).
     * @param sortBy the property to sort by (default="id").
     * @param order  sort direction, either "ASC" (ascending) or "DESC" (descending) (default="ASC").
     * @param form   the search form containing filter criteria.
     * @return a Page of PersonnelDto objects matching the given criteria.
     */
    @GetMapping
    @Operation(summary = "Search for personnel with pagination and sorting")
    public ResponseEntity<Page<PersonnelDto>> searchPersonnel(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            @RequestBody(required = false) String form
    ) {

        Page<PersonnelDto> results = personnelService.searchPersonnel(form, page, size, sortBy, order);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/select")
    @Operation(summary = "Get a list of personnel as select options")
    public ResponseEntity<List<SelectOption>> selectPersonnel(@RequestParam(required = false) String searchKeyword) {
        List<SelectOption> dishes = personnelService.selectPersonnel(searchKeyword);
        return ResponseEntity.ok(dishes);
    }



    /**
     * Updates an existing personnel record.
     *
     * @param id           the unique identifier of the personnel to update.
     * @param personnelDto the personnel data transfer object containing updated details.
     * @return a {@link ResponseEntity} containing the updated personnel and HTTP status 200 (OK).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing personnel record")
    public ResponseEntity<PersonnelDto> updatePersonnel(@PathVariable Long id, @RequestBody PersonnelDto personnelDto) {
        PersonnelDto updatedPersonnel = personnelService.updatePersonnel(id, personnelDto);
        return new ResponseEntity<>(updatedPersonnel, HttpStatus.OK);
    }

    /**
     * Deletes a personnel record by its identifier.
     *
     * @param id the unique identifier of the personnel to delete.
     * @return a {@link ResponseEntity} with HTTP status 204 (No Content) after deletion.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a personnel record by ID")
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id){
        personnelService.deletePersonnel(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/import")
    @Operation(summary = "Import personnel from an Excel file")
    public ResponseEntity<List<PersonnelDto>> importPersonnelFromExcel(@RequestParam("file") MultipartFile file) {
        List<PersonnelDto> importedPersonnel = personnelService.importFromExcel(file);
        return new ResponseEntity<>(importedPersonnel, HttpStatus.CREATED);
    }

    // create exportToExcel class
    @GetMapping("/download-all-personnel.xlsx")
    @Operation(summary = "Export personnel to an Excel file")
    public ResponseEntity<byte[]> exportPersonnelToExcel() {
        byte[] excelData = personnelService.exportToExcel();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=personnel.xlsx")
                .body(excelData);
        }
}
