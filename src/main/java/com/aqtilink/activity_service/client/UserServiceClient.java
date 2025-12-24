package com.aqtilink.activity_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aqtilink.activity_service.dto.FriendDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${user-service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public List<UUID> getFriendIds(UUID userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId + "/friends";

        ResponseEntity<List<FriendDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FriendDTO>>() {}
        );

        List<FriendDTO> friends = response.getBody();
        if (friends == null) return List.of();

        return friends.stream().map(FriendDTO::getId).collect(Collectors.toList());
    }
    public List<String> getFriendEmails(UUID userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId + "/friends";

        ResponseEntity<List<FriendDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FriendDTO>>() {}
        );

        List<FriendDTO> friends = response.getBody();
        if (friends == null) return List.of();

    return friends.stream().map(FriendDTO::getEmail).toList();
}
}
