package com.furia.know_your_fan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furia.know_your_fan.entity.FanProfile;
import com.furia.know_your_fan.entity.YoutubeActivity;
import com.furia.know_your_fan.entity.YoutubeSubscription;
import com.furia.know_your_fan.repository.FanProfileRepository;
import com.furia.know_your_fan.repository.YoutubeActivityRepository;
import com.furia.know_your_fan.repository.YoutubeSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class YoutubeService {

    private final FanProfileRepository fanProfileRepository;
    private final YoutubeSubscriptionRepository youtubeSubscriptionRepository;
    private final YoutubeActivityRepository youtubeActivityRepository;

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

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
        formConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN,
                MediaType.APPLICATION_FORM_URLENCODED));

        restTemplate.setMessageConverters(Arrays.asList(
                formConverter,
                stringConverter,
                new MappingJackson2HttpMessageConverter()));

        return restTemplate;
    }

    public void connectYoutubeAccount(Long fanProfileId, String authCode) throws Exception {
        FanProfile fanProfile = fanProfileRepository.findById(fanProfileId)
                .orElseThrow(() -> new RuntimeException("FanProfile not found"));

        RestTemplate restTemplate = createRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
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
            updateYoutubeSubscriptions(fanProfile, accessToken);
            updateYoutubeActivities(fanProfile, accessToken);

            fanProfileRepository.save(fanProfile);
        } else {
            throw new RuntimeException("YouTube connection failed: " + response.getStatusCode() + " - " + response.getBody());
        }
    }

    private void updateYoutubeData(FanProfile fanProfile, String accessToken) throws Exception {
        RestTemplate restTemplate = createRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> channelResponse = restTemplate.exchange(CHANNELS_URL, HttpMethod.GET, entity,
                String.class);
        JsonNode channels = new ObjectMapper().readTree(channelResponse.getBody());
        JsonNode snippet = channels.path("items").get(0).path("snippet");

        fanProfile.setYoutubeUsername(snippet.path("title").asText());
        fanProfile.setYoutubeChannelId(channels.path("items").get(0).path("id").asText());
    }

    private void updateYoutubeSubscriptions(FanProfile fanProfile, String accessToken) throws Exception {
        RestTemplate restTemplate = createRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(SUBSCRIPTIONS_URL, HttpMethod.GET, entity,
                String.class);
        JsonNode items = new ObjectMapper().readTree(response.getBody()).path("items");

        youtubeSubscriptionRepository.deleteByFanProfile(fanProfile);

        List<String> esportsKeywords = Arrays.asList("furia", "loud", "mibr", "pain");

        for (JsonNode item : items) {
            JsonNode snippet = item.path("snippet");
            String title = snippet.path("title").asText().toLowerCase();

            // filters esports-related channels
            if (esportsKeywords.stream().anyMatch(title::contains)) {
                YoutubeSubscription subscription = new YoutubeSubscription();
                subscription.setFanProfile(fanProfile);
                subscription.setChannelId(snippet.path("resourceId").path("channelId").asText());
                subscription.setChannelTitle(snippet.path("title").asText());
                subscription.setChannelThumbnailUrl(snippet.path("thumbnails").path("default").path("url").asText());

                youtubeSubscriptionRepository.save(subscription);
            }
        }
    }

    private void updateYoutubeActivities(FanProfile fanProfile, String accessToken) throws Exception {
        RestTemplate restTemplate = createRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(ACTIVITIES_URL, HttpMethod.GET, entity, String.class);
        JsonNode items = new ObjectMapper().readTree(response.getBody()).path("items");

        youtubeActivityRepository.deleteByFanProfile(fanProfile);

        List<String> esportsKeywords = Arrays.asList("furia", "loud", "mibr", "pain");

        for (JsonNode item : items) {
            JsonNode snippet = item.path("snippet");
            String title = snippet.path("title").asText().toLowerCase();
            String description = snippet.path("description").asText().toLowerCase();

            // filters activities related to esports teams
            if (esportsKeywords.stream()
                    .anyMatch(keyword -> title.contains(keyword) || description.contains(keyword))) {
                YoutubeActivity activity = new YoutubeActivity();
                activity.setFanProfile(fanProfile);
                activity.setType(snippet.path("type").asText());
                activity.setTitle(snippet.path("title").asText());
                activity.setDescription(snippet.path("description").asText());
                activity.setPublishedAt(Instant.parse(snippet.path("publishedAt").asText()));

                JsonNode contentDetails = item.path("contentDetails");
                JsonNode upload = contentDetails.path("upload");
                JsonNode like = contentDetails.path("like");
                if (!upload.isMissingNode()) {
                    activity.setVideoId(upload.path("videoId").asText());
                } else if (!like.isMissingNode()) {
                    activity.setVideoId(like.path("videoId").asText());
                }

                youtubeActivityRepository.save(activity);
            }
        }
    }
}