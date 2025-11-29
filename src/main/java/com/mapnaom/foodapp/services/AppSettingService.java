package com.mapnaom.foodapp.services;


import com.mapnaom.foodapp.models.AppSetting;
import com.mapnaom.foodapp.models.PriceShares;
import com.mapnaom.foodapp.models.ReservationTime;
import com.mapnaom.foodapp.repositories.AppSettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppSettingService {

    private final AppSettingRepository appSettingRepository;

    public AppSettingService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    /**
     * Retrieves the singleton AppSetting instance.
     * Caches the result to prevent multiple database lookups.
     */
    @Cacheable(value = "appSettings", key = "#root.methodName")
    @Transactional(readOnly = true)
    public AppSetting getAppSetting() {
        return appSettingRepository.findById(AppSetting.SINGLETON_ID)
                .orElseThrow(() -> new IllegalStateException("AppSetting has not been initialized."));
    }

    /**
     * Updates the AppSetting and evicts the cache.
     */
    @CacheEvict(value = "appSettings", allEntries = true)
    @Transactional
    public AppSetting save(AppSetting appSetting) {
        // Enforce the singleton ID before saving.
        appSetting.setId(AppSetting.SINGLETON_ID);
        return appSettingRepository.save(appSetting);
    }

    /**
     * Method to initialize the configuration if it doesn't exist.
     * Use @PostConstruct to run this on application startup.
     */
    @PostConstruct
    @Transactional
    public void initIfEmpty() {
        if (appSettingRepository.findById(AppSetting.SINGLETON_ID).isEmpty()) {
            AppSetting defaultSettings = new AppSetting(
                    AppSetting.SINGLETON_ID,
                    true, // foodPricesActive
                    new PriceShares(40,60),
                    new ReservationTime(10,30),
                    "شرکت بهره برداری و تعمیرات نیروگاه مپنا",
                    "بلوار میرداماد - خیابان مصدق ( نفت شمالی) - خیابان یکم",
                    "09129357731",
                    null // version handled by JPA
            );
            appSettingRepository.save(defaultSettings);
        }
    }
}