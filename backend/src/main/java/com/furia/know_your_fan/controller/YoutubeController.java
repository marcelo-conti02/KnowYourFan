package com.furia.know_your_fan.controller;

import com.furia.know_your_fan.entity.YoutubeActivity;
import com.furia.know_your_fan.entity.YoutubeSubscription;
import com.furia.know_your_fan.entity.FanProfile;
import com.furia.know_your_fan.repository.FanProfileRepository;
import com.furia.know_your_fan.repository.YoutubeActivityRepository;
import com.furia.know_your_fan.repository.YoutubeSubscriptionRepository;
import com.furia.know_your_fan.service.YoutubeService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;
    private final YoutubeSubscriptionRepository subscriptionRepository;
    private final YoutubeActivityRepository activityRepository;
    private final FanProfileRepository fanProfileRepository;

    @Value("${youtube.client-id}")
    private String clientId;

    @Value("${youtube.redirect-uri}")
    private String redirectUri;

    // search for subscriptions on e-sports channels
    @GetMapping("/{fanProfileId}/subscriptions")
    public ResponseEntity<List<YoutubeSubscription>> getEsportsSubscriptions(
            @PathVariable Long fanProfileId) {

        FanProfile fan = fanProfileRepository.findById(fanProfileId)
                .orElseThrow(() -> new RuntimeException("FanProfile not found"));
        List<YoutubeSubscription> subs = subscriptionRepository.findByFanProfile(fan);
        return ResponseEntity.ok(subs);
    }

    // search for activities e-sports related
    @GetMapping("/{fanProfileId}/activities")
    public ResponseEntity<List<YoutubeActivity>> getEsportsActivities(
            @PathVariable Long fanProfileId) {

        FanProfile fan = fanProfileRepository.findById(fanProfileId)
                .orElseThrow(() -> new RuntimeException("FanProfile not found"));
        List<YoutubeActivity> activities = activityRepository.findByFanProfile(fan);
        return ResponseEntity.ok(activities);
    }

    // generate auth URL
    @GetMapping("/auth-url")
    public ResponseEntity<String> getAuthUrl(@RequestParam Long fanProfileId) {
        String scope = "https://www.googleapis.com/auth/youtube.readonly";
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=" + scope
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + fanProfileId;

        return ResponseEntity.ok(url);
    }

    // connects with youtube account
    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> handleOAuth2Callback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        try {
            Long fanProfileId = Long.parseLong(state);
            youtubeService.connectYoutubeAccount(fanProfileId, code);
            return ResponseEntity.ok("YouTube account linked successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to process OAuth callback.");
        }
    }
}
