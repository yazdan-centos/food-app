package com.mapnaom.foodapp.repositories;

import com.mapnaom.foodapp.models.DailyMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMealRepository extends JpaRepository<DailyMeal, Long>, JpaSpecificationExecutor<DailyMeal> {

    @Query("select d from DailyMeal d where d.date = :date")
    Optional<DailyMeal> findDailyMealByDate(@Param("date") LocalDate date);


    @Query("select d from DailyMeal d where d.jalaliYear = :jalaliYear and d.jalaliMonth = :jalaliMonth")
    List<DailyMeal> findByJalaliYearAndJalaliMonth(@Param("jalaliYear") int jalaliYear, @Param("jalaliMonth") int jalaliMonth);



    @Query("select (count(d) > 0) from DailyMeal d where d.date = :finalDate and d.id <> :id")
    boolean existsByDateAndIdNot(@Param("finalDate") LocalDate finalDate, @Param("id") Long id);

    @Query("select (count(d) > 0) from DailyMeal d")
    boolean isNotEmpty();

    @Query("select d from DailyMeal d where d.date = :date")
    List<DailyMeal> findByDate(@Param("date") LocalDate date);

    @Query("select (count(d) > 0) from DailyMeal d where d.date = :date")
    boolean existsByDate(@Param("date") LocalDate date);
}