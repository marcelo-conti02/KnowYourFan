package com.furia.know_your_fan.controller;

import com.furia.know_your_fan.entity.FanProfile;
import com.furia.know_your_fan.entity.User;
import com.furia.know_your_fan.service.FanProfileService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/fan-profiles")
@RequiredArgsConstructor
public class FanProfileController {

    private final FanProfileService fanProfileService;

    @PostMapping
    public ResponseEntity<FanProfile> createFanProfile(@RequestBody FanProfile fanProfile) {
        FanProfile savedProfile = fanProfileService.save(fanProfile);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<FanProfile> getFanProfileByUserId(@PathVariable Long userId) {
        FanProfile fanProfile = fanProfileService.findByUserId(userId);
        if (fanProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fanProfile);
    }

    @GetMapping
    public ResponseEntity<List<FanProfile>> getAllFanProfiles() {
        List<FanProfile> fanProfiles = fanProfileService.findAll();
        return ResponseEntity.ok(fanProfiles);
    }
    
}
