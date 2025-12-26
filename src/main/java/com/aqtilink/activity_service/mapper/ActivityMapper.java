package com.aqtilink.activity_service.mapper;

import com.aqtilink.activity_service.client.UserServiceClient;
import com.aqtilink.activity_service.dto.ActivityDTO;
import com.aqtilink.activity_service.dto.FriendDTO;
import com.aqtilink.activity_service.model.Activity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityMapper {

    private final UserServiceClient userServiceClient;

    public ActivityMapper(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

public ActivityDTO toDto(Activity activity) {
    ActivityDTO dto = new ActivityDTO();
    dto.setId(activity.getId());
    dto.setTitle(activity.getTitle());
    dto.setLocation(activity.getLocation());
    dto.setStartTime(activity.getStartTime());
    dto.setOwnerId(activity.getOwnerId());
    dto.setSportType(activity.getSportType().toString());

    FriendDTO owner = userServiceClient.getUser(activity.getOwnerId());
    dto.setOwnerName(owner.getFirstName() + " " + owner.getLastName());

    if (activity.getParticipants() != null && !activity.getParticipants().isEmpty()) {
        List<FriendDTO> friends = userServiceClient.getFriends(activity.getOwnerId());
        List<FriendDTO> participants = friends.stream()
            .filter(f -> activity.getParticipants().contains(f.getId()))
                .toList();
        
        // Add owner to participants list if they're in the participants set
        if (activity.getParticipants().contains(activity.getOwnerId())) {
            List<FriendDTO> allParticipants = new java.util.ArrayList<>(participants);
            // Check if owner is not already in the list
            if (participants.stream().noneMatch(p -> p.getId().equals(activity.getOwnerId()))) {
                allParticipants.add(owner);
            }
            dto.setParticipants(allParticipants);
        } else {
            dto.setParticipants(participants);
        }
    }

    return dto;
}


}

