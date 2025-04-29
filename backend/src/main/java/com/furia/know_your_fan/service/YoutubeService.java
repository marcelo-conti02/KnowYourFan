package com.furia.know_your_fan.service;

import com.furia.know_your_fan.entity.FanProfile;
import com.furia.know_your_fan.repository.FanProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class YoutubeService {

    private final FanProfileRepository fanProfileRepository;

    @Value("${youtube.client-id}")
    private String clientId;

    @Value("${youtube.client-secret}")
    private String clientSecret;

    @Value("${youtube.redirect-uri}")
    private String redirectUri;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String CHANNELS_URL = "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true";
    private static final String SUBSCRIPTIONS_URL = "https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&mine=true&maxResults=50";
    private static final String ACTIVITIES_URL = "https://www.googleapis.com/youtube/v3/activities?part=snippet,contentDetails&mine=true&maxResults=50";

    public void connectYoutubeAccount(Long fanProfileId, String authCode) throws Exception {
        FanProfile fanProfile = fanProfileRepository.findById(fanProfileId)
                .orElseThrow(() -> new RuntimeException("FanProfile not found"));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("code", authCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree(response.getBody());
            String accessToken = body.path("access_token").asText();
            String refreshToken = body.path("refresh_token").asText();
            int expiresIn = body.path("expires_in").asInt();

            fanProfile.setYoutubeAccessToken(accessToken);
            fanProfile.setYoutubeRefreshToken(refreshToken);
            fanProfile.setYoutubeTokenExpiry(Instant.now().plusSeconds(expiresIn));

            updateYoutubeData(fanProfile, accessToken);

            fanProfileRepository.save(fanProfile);
        } else {
            throw new RuntimeException("Failed to fetch YouTube access token");
        }
    }

    private void updateYoutubeData(FanProfile fanProfile, String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> channelResponse = restTemplate.exchange(CHANNELS_URL, HttpMethod.GET, entity, String.class);
        JsonNode channels = new ObjectMapper().readTree(channelResponse.getBody());
        JsonNode snippet = channels.path("items").get(0).path("snippet");

        fanProfile.setYoutubeUsername(snippet.path("title").asText());
        fanProfile.setYoutubeChannelId(channels.path("items").get(0).path("id").asText());
    }
}
