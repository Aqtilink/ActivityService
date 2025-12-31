package com.aqtilink.activity_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aqtilink.activity_service.dto.FriendDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Client for communicating with User Service.
 * Includes service-to-service API key authentication.
 */
@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final String serviceApiKey;

    public UserServiceClient(RestTemplate restTemplate, 
                            @Value("${user-service.url}") String userServiceUrl,
                            @Value("${service.api-key}") String serviceApiKey) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.serviceApiKey = serviceApiKey;
    }

    public List<String> getFriendIds(String userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId + "/friends";
        HttpHeaders headers = createServiceHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<FriendDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<FriendDTO>>() {}
        );

        List<FriendDTO> friends = response.getBody();
        if (friends == null) return List.of();

        return friends.stream().map(FriendDTO::getId).collect(Collectors.toList());
    }
    
    public List<String> getFriendEmails(String userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId + "/friends";
        HttpHeaders headers = createServiceHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<FriendDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<FriendDTO>>() {}
        );

        List<FriendDTO> friends = response.getBody();
        if (friends == null) return List.of();

        return friends.stream().map(FriendDTO::getEmail).toList();
    }

    /**
     * Creates HTTP headers with service API key for inter-service communication.
     */
    private HttpHeaders createServiceHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-API-Key", serviceApiKey);
        return headers;
    }
}
