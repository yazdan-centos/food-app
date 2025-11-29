package com.mapnaom.foodapp.services;

import com.mapnaom.foodapp.dtos.*;
import com.mapnaom.foodapp.enums.ReservationStatus;
import com.mapnaom.foodapp.exceptions.ResourceNotFoundException;
import com.mapnaom.foodapp.mappers.*;
import com.mapnaom.foodapp.models.*;
import com.mapnaom.foodapp.repositories.*;
import com.mapnaom.foodapp.searchForms.ReservationSearchForm;
import com.mapnaom.foodapp.specifications.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationListMapper reservationListMapper;
    private final PersonnelReservationMapper personnelReservationMapper;
    private final PersonnelRepository personnelRepository;
    private final DailyMealDishRepository dailyMealDishRepository;
    private final DailyPersonnelReservationListMapper dailyPersonnelReservationListMapper;
    private final DailyMealRepository dailyMealRepository;
    private final ReservationMapper reservationMapper;
    private final AppSettingRepository appSettingRepository;

    public Page<ReservationDto> searchReservations(ReservationSearchForm form, int page, int size, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Reservation> reservationPage = reservationRepository.findAll(
                ReservationSpecification.withFilter(form),
                pageRequest
        );

        return reservationPage.map(reservationMapper::toDto);
    }

    public List<ReservationListDto> reservationList() {
        List<Reservation> reservationList = reservationRepository.findAll();
        return reservationList.stream().map(reservationListMapper::toDto).toList();
    }
    public ReservationDto upsertReservation(ReservationDto reservationDto) {
        log.info("Upserting reservation: {}", reservationDto);

        try {
            Reservation reservation;

            // Check if this is an update (id exists) or create operation
            if (reservationDto.getId() != null && reservationDto.getId() != 0) {
                // Update existing reservation
                reservation = reservationRepository.findById(reservationDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Reservation not found with id: %d".formatted(reservationDto.getId())));

                // Update only the fields that should be modifiable
                updateReservationFields(reservation, reservationDto);

            } else {
                // Create new reservation
                reservation = createNewReservation(reservationDto);
            }

            // Save and return the updated/created reservation
            Reservation savedReservation = reservationRepository.save(reservation);
            log.info("Successfully upserted reservation with id: {}", savedReservation.getId());

            return reservationMapper.toDto(savedReservation);

        } catch (Exception e) {
            log.error("Error upserting reservation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upsert reservation: %s".formatted(e.getMessage()), e);
        }
    }

    private Reservation createNewReservation(ReservationDto reservationDto) {
        // Fetch related entities
        Personnel personnel = personnelRepository.findPersonnelByUsername(reservationDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Personnel not found with username: %s".formatted(reservationDto.getUsername())));

        DailyMeal dailyMeal = dailyMealRepository.findById(reservationDto.getDailyMealId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DailyMeal not found with id: %d".formatted(reservationDto.getDailyMealId())));

        DailyMealDish dailyMealDish = dailyMealDishRepository.findById(reservationDto.getDailyMealDishId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DailyMealDish not found with id: %d".formatted(reservationDto.getDailyMealDishId())));

        // Validate that the dish belongs to the meal
        if (!dailyMealDish.getDailyMeal().getId().equals(dailyMeal.getId())) {
            throw new IllegalArgumentException(
                    "DailyMealDish does not belong to the specified DailyMeal");
        }

        // Create the reservation first
        Reservation reservation = Reservation.builder()
                .personnel(personnel)
                .dailyMeal(dailyMeal)
                .dailyMealDish(dailyMealDish)
                .reservationStatus(reservationDto.getReservationStatus() != null
                        ? reservationDto.getReservationStatus()
                        : ReservationStatus.ACTIVE)
                .build();

        // Create and associate the CostShare
        CostShare costShare = createCostShareForReservation(reservation, reservationDto.getCostShares());
        reservation.setCostShare(costShare);

        // Build new reservation
        return reservation;
    }

    private void updateReservationFields(Reservation reservation, ReservationDto reservationDto) {
        // Update personnel if username changed
        if (reservationDto.getUsername() != null &&
                !reservation.getPersonnel().getUsername().equals(reservationDto.getUsername())) {
            Personnel newPersonnel = personnelRepository.findPersonnelByUsername(reservationDto.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Personnel not found with username: %s".formatted(reservationDto.getUsername())));
            reservation.setPersonnel(newPersonnel);
        }

        // Update daily meal if changed
        if (reservationDto.getDailyMealId() != null &&
                !reservation.getDailyMeal().getId().equals(reservationDto.getDailyMealId())) {
            DailyMeal newDailyMeal = dailyMealRepository.findById(reservationDto.getDailyMealId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DailyMeal not found with id: %d".formatted(reservationDto.getDailyMealId())));
            reservation.setDailyMeal(newDailyMeal);
        }

        // Update daily meal dish if changed
        if (reservationDto.getDailyMealDishId() != null &&
                !reservation.getDailyMealDish().getId().equals(reservationDto.getDailyMealDishId())) {
            DailyMealDish newDailyMealDish = dailyMealDishRepository.findById(reservationDto.getDailyMealDishId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DailyMealDish not found with id: %d".formatted(reservationDto.getDailyMealDishId())));

            // Validate that the dish belongs to the meal
            if (!newDailyMealDish.getDailyMeal().getId().equals(reservation.getDailyMeal().getId())) {
                throw new IllegalArgumentException(
                        "DailyMealDish does not belong to the specified DailyMeal");
            }

            reservation.setDailyMealDish(newDailyMealDish);
        }

        // Update cost shares if provided
        if (reservationDto.getCostShares() != null) {
            CostShare costShare = reservation.getCostShare();
            if (costShare == null) {
                costShare = createCostShareForReservation(reservation, reservationDto.getCostShares());
                reservation.setCostShare(costShare);
            } else {
                // You can add logic here to update portions if needed
            }
        }

        // Update reservation status if provided
        if (reservationDto.getReservationStatus() != null) {
            reservation.setReservationStatus(reservationDto.getReservationStatus());
        }
    }
    private CostShare createCostShareForReservation(Reservation reservation, BigDecimal employeePortion) {
        // You can implement more detailed logic here based on AppSetting if needed
        // For now, we'll use the provided DTO value.

        BigDecimal dishPrice = BigDecimal.valueOf(reservation.getDailyMealDish().getDish().getPrice());

        BigDecimal employeeShare = employeePortion != null ? employeePortion : BigDecimal.ZERO;
        BigDecimal employerShare = dishPrice.subtract(employeeShare);

        return CostShare.builder()
                .reservation(reservation)
                .quantity(1)
                .totalCost(dishPrice)
                .employeePortion(employeeShare)
                .employerPortion(employerShare)
                // You can calculate this percentage if you have the rules
                // .employeeSharePercentage(...)
                .build();
    }


    // Alternative batch upsert method for multiple reservations
    @Transactional
    public List<ReservationDto> upsertReservations(List<ReservationDto> reservationDtos) {
        log.info("Batch upserting {} reservations", reservationDtos.size());

        return reservationDtos.stream()
                .map(this::upsertReservation)
                .collect(Collectors.toList());
    }

    // Additional helper method to check if a reservation already exists
    public boolean reservationExists(String username, Long dailyMealId, Long dailyMealDishId) {
        return reservationRepository.existsByPersonnelUsernameAndDailyMealIdAndDailyMealDishId(
                username, dailyMealId, dailyMealDishId);
    }

    // Method to prevent duplicate reservations for the same meal
    public ReservationDto upsertReservationWithDuplicateCheck(ReservationDto reservationDto) {
        log.info("Upserting reservation with duplicate check: {}", reservationDto);

        // Check if a reservation already exists for this personnel, meal, and dish combination
        List<Reservation> existingReservations = reservationRepository
                .findByPersonnelUsernameAndDailyMealIdAndReservationStatus(
                        reservationDto.getUsername(),
                        reservationDto.getDailyMealId(),
                        ReservationStatus.ACTIVE);

        if (!existingReservations.isEmpty() && reservationDto.getId() == null) {
            // If creating new reservation and active reservation already exists
            throw new IllegalStateException(
                    "Active reservation already exists for this personnel and meal");
        }

        return upsertReservation(reservationDto);
    }
    public List<DailyPersonnelReservationListDto> reservationsByJYearJMonthAndUsername(
            Integer jYear,
            Integer jMonth,
            String username
    ) {
        Specification<Reservation> spec = Specification
                .where(ReservationSpecification.byJalaliYear(jYear))
                .and(ReservationSpecification.byJalaliMonth(jMonth))
                .and(ReservationSpecification.byUsername(username));
        return reservationRepository.findAll(spec).stream()
                .map(dailyPersonnelReservationListMapper::toDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public Map<LocalDate, List<PersonnelReservationDto>> getAllReservationsGroupedByDate() {
        return reservationRepository.findAll().stream()
                .map(personnelReservationMapper::toDto)
                .collect(Collectors.groupingBy(
                        dto -> dto.getDailyMealDish()
                                .getDailyMeal()
                                .getDate()
                ));
    }


    public ReservationDto getReservationById(Long id) {
        return reservationRepository.findById(id).map(reservationMapper::toDto).orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    public ReservationDto clearDish(Long personnelId, Long dailyMealId) {
        Reservation reservation = reservationRepository.findByPersonnelIdAndDailyMeal_Id(personnelId, dailyMealId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservation.setDailyMealDish(null);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    public List<DailyPersonnelReservationListDto> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDailyMeal_Date(date)
                .stream()
                .map(dailyPersonnelReservationListMapper::toDto)
                .toList();
    }

    public ResponseEntity<Void> delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        reservationRepository.delete(reservation);
        return ResponseEntity.noContent().build();

    }

    @Transactional
    public ReservationDto deliverReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: %d".formatted(id)));

        // Use the deliver() method from the entity
        reservation.deliver();

        return reservationMapper.toDto(reservationRepository.save(reservation));
    }
    @Transactional
    public ReservationDto cancelReservation(Long id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: %d".formatted(id)));

        reservation.active();

        return reservationMapper.toDto(reservationRepository.save(reservation));

    }
}