package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.dtos.*;
import com.mapnaom.foodapp.mappers.PersonnelReservationMapper;
import com.mapnaom.foodapp.mappers.ReservationMapper;
import com.mapnaom.foodapp.models.Reservation;
import com.mapnaom.foodapp.repositories.ReservationRepository;
import com.mapnaom.foodapp.searchForms.ReservationSearchForm;
import com.mapnaom.foodapp.services.ReservationService;
import com.mapnaom.foodapp.specifications.ReservationSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservations", description = "Reservation management")
public class ReservationController {
    private final PersonnelReservationMapper personnelReservationMapper;

    private final ReservationService    reservationService;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;


    @GetMapping
    @Operation(summary = "Search for reservations with pagination and sorting")
    public ResponseEntity<?> searchReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            ReservationSearchForm form
    ) {
        try {
            Page<ReservationDto> results = reservationService.searchReservations(form, page, size, sortBy, order);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }

    @PutMapping("/upsert")
    public ResponseEntity<ReservationDto> upsertReservation(@RequestBody ReservationDto reservationDto) {
        ReservationDto result = reservationService.upsertReservation(reservationDto);

        if (reservationDto.getId() != null) {
            return ResponseEntity.ok(result); // 200 OK for update
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(result); // 201 Created for new
        }
    }

    @PutMapping("/batch-upsert")
    public ResponseEntity<List<ReservationDto>> batchUpsertReservations(@RequestBody List<ReservationDto> reservationDtos) {
        List<ReservationDto> results = reservationService.upsertReservations(reservationDtos);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/deliver/{id}")
    @Operation(summary = "Deliver a reservation")
    public ResponseEntity<ReservationDto> deliverReservation(@PathVariable Long id) {
        return ResponseEntity.ok(
                reservationService.deliverReservation(id));
    }
    @PostMapping("/undeliver/{id}")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(
                reservationService.cancelReservation(id));
    }



    @GetMapping("/byDate")
    @Operation(summary = "Get all reservations grouped by date")
    public ResponseEntity<Map<LocalDate, List<PersonnelReservationDto>>> getGrouped() {
        Map<LocalDate, List<PersonnelReservationDto>> grouped =
                reservationService.getAllReservationsGroupedByDate();
        return ResponseEntity.ok(grouped);
    }




    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation by ID")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        ReservationDto reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/byPersonnel/{personnelId}")
    @Operation(summary = "Get reservations by personnel ID with pagination")
    public ResponseEntity<Page<PersonnelReservationDto>> getReservationsByPersonnel(
            @PathVariable Long personnelId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Reservation> spec = ReservationSpecification.byPersonnel(personnelId);

        Page<Reservation> reservations = reservationRepository.findAll(spec, pageable);
        Page<PersonnelReservationDto> reservationDtos = reservations.map(personnelReservationMapper::toDto);

        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/byJYear-JMonth-username")
    @Operation(summary = "Get reservations by Jalali year, month, and username")
    public ResponseEntity<List<DailyPersonnelReservationListDto>> getReservationsByJalaliYearMonthAndUsername(
            @RequestParam("jYear") Integer jYear,
            @RequestParam("jMonth") Integer jMonth,
            @RequestParam("username") String username)
    {
        return   ResponseEntity.ok(reservationService.reservationsByJYearJMonthAndUsername(jYear, jMonth, username));
    }


    @GetMapping("/select")
    @Operation(summary = "Get a list of all reservations")
    public ResponseEntity<?> selectReservations(@ModelAttribute ReservationSearchForm form) {
        List<ReservationDto> list = reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }




    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return reservationService.delete(id);
    }

    @PostMapping("/clear")
    @Operation(summary = "Clear the dish from a reservation")
    public ResponseEntity<ReservationDto> clear(@RequestBody ClearRequest req) {
        return ResponseEntity.ok(
                reservationService.clearDish(req.getPersonnelId(), req.getDailyMealId()));
    }

    @Data
    public static class ClearRequest {
        private Long personnelId;
        private Long dailyMealId;
    }

    @GetMapping("/serveList-byDate")
    @Operation(summary = "Get reservations by date for serving list")
    public ResponseEntity<?> getReservationsByDate(
            @RequestParam LocalDate date
    ) {
        List<DailyPersonnelReservationListDto> reservations = reservationService
                .getReservationsByDate(date);
        return ResponseEntity.ok(reservations);
    }
}
