package com.aqtilink.activity_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.aqtilink.activity_service.dto.FriendDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${user-service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getToken() != null) {
            headers.setBearerAuth(auth.getToken().getTokenValue());
        }

        return headers;
    }


    public List<String> getFriendIds(String userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId + "/friends";
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());

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
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());


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
    public List<FriendDTO> getFriends(String userId) {
        String url = userServiceUrl + "/api/v1/users/" + userId + "/friends";
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());

        ResponseEntity<List<FriendDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        List<FriendDTO> friends = response.getBody();
        if (friends == null) return List.of();

        return friends;
    }

    public FriendDTO getUser(String userId) {
    String url = userServiceUrl + "/api/v1/users/" + userId;
    return restTemplate.getForObject(url, FriendDTO.class);
    }

    public List<FriendDTO> getUsersByIds(Set<String> ids) {
        // assuming user-service exposes /users/summary?ids=...
        String url = userServiceUrl + "/api/v1/users/summary?ids=" +
                    ids.stream().collect(Collectors.joining(","));
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<List<FriendDTO>> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<FriendDTO>>() {}
        );
        return response.getBody() != null ? response.getBody() : List.of();
    }


}
