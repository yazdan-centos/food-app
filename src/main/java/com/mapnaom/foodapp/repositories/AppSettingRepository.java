package com.mapnaom.foodapp.repositories;



import com.mapnaom.foodapp.models.AppSetting;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    // Custom method to find the singleton instance
    @Query("select a from AppSetting a where a.id = :id")
    @NotNull
    Optional<AppSetting> findById(@Param("id") Long id);

    @Query("select a from AppSetting a")
    AppSetting findFirst(@Param("singletonId") Long singletonId);
}
