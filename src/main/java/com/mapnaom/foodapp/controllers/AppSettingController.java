package com.mapnaom.foodapp.controllers;

import com.mapnaom.foodapp.models.AppSetting;
import com.mapnaom.foodapp.services.AppSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/settings")
public class AppSettingController {

    private final AppSettingService appSettingService;

    public AppSettingController(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    @GetMapping
    public ResponseEntity<AppSetting> getSettings() {
        return ResponseEntity.ok(appSettingService.getAppSetting());
    }

    @PutMapping
    public ResponseEntity<AppSetting> updateSettings(@RequestBody AppSetting appSetting) {
        return  ResponseEntity.ok(appSettingService.save(appSetting));
    }
}