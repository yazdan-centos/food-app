package com.mapnaom.foodapp.config;


import com.mapnaom.foodapp.models.AppSetting;
import com.mapnaom.foodapp.models.PriceShares;
import com.mapnaom.foodapp.models.ReservationTime;
import com.mapnaom.foodapp.repositories.AppSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppSettingRepository appSettingRepository;

    public DataInitializer(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    @Override
    public void run(String... args) {
        if (appSettingRepository.findById(AppSetting.SINGLETON_ID).isEmpty()) {
            AppSetting initialSettings = new AppSetting();
            initialSettings.setCompanyName("MAPNA O&M");
            initialSettings.setAddress("Tehran, Iran");
            initialSettings.setPhone("555-0987");
            initialSettings.setFoodPricesActive(true);

            PriceShares initialPriceShares = new PriceShares(50, 50);
            initialSettings.setPriceShares(initialPriceShares);

            ReservationTime initialReservationTime = new ReservationTime(12, 0);
            initialSettings.setReservationTime(initialReservationTime);
            appSettingRepository.save(initialSettings);
        }
    }
}