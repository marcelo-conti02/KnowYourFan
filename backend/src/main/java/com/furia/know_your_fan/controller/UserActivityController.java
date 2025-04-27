package com.furia.know_your_fan.controller;

import com.furia.know_your_fan.entity.UserActivity;
import com.furia.know_your_fan.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;

    @PostMapping
    public ResponseEntity<UserActivity> createUserActivity(@RequestBody UserActivity activity) {
        UserActivity savedActivity = userActivityService.save(activity);
        return ResponseEntity.ok(savedActivity);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserActivity>> getActivitiesByUserId(@PathVariable Long userId) {
        List<UserActivity> activities = userActivityService.findByUserId(userId);
        return ResponseEntity.ok(activities);
    }
}
