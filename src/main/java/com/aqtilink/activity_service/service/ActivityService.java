package com.aqtilink.activity_service.service;

import com.aqtilink.activity_service.client.UserServiceClient;
import com.aqtilink.activity_service.dto.ActivityResponseDTO;
import com.aqtilink.activity_service.dto.NotificationEventDTO;
import com.aqtilink.activity_service.dto.UserDTO;
import com.aqtilink.activity_service.dto.UserSummaryDTO;
import com.aqtilink.activity_service.exception.ActivityAlreadyStartedException;
import com.aqtilink.activity_service.messaging.NotificationPublisherActivity;
import com.aqtilink.activity_service.model.Activity;
import com.aqtilink.activity_service.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

//class that handles business logic for activities

@Service
public class ActivityService {

    private final ActivityRepository repo;
    private final UserServiceClient userServiceClient;
    private final NotificationPublisherActivity notificationPublisherActivity;

    public ActivityService(ActivityRepository repo, UserServiceClient userServiceClient, NotificationPublisherActivity notificationPublisherActivity) {
        this.repo = repo;
        this.userServiceClient = userServiceClient;
        this.notificationPublisherActivity = notificationPublisherActivity;
    }
    public ActivityResponseDTO create(Activity activity, Set<String> notifyFriends) {
        activity.getParticipants().add(activity.getOwnerId());
        Activity saved = repo.save(activity);

        if (notifyFriends != null) {
            List<String> friendEmails = userServiceClient.getFriendEmails(activity.getOwnerId());

            for (String email : friendEmails) {
                NotificationEventDTO event = new NotificationEventDTO();
                event.setEmail(email);
                event.setSubject("Your friend created a new activity!");
                event.setMessage("Your friend has created a new activity: " + activity.getTitle() + ". Join them now!");
                notificationPublisherActivity.publish(event);
            }
        }

        return mapToResponse(List.of(saved)).get(0);
    }

    public void joinActivity(UUID activityId, String userId) {
        Activity activity = repo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if(activity.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ActivityAlreadyStartedException();
        }

        if (activity.getParticipants().contains(userId)) {
            return;
        }

        activity.getParticipants().add(userId);
        repo.save(activity);
    }



    public List<ActivityResponseDTO> getAllActivities() {
        return mapToResponse(repo.findAll());
    }

    public void deleteActivity(UUID activityId) {
        repo.deleteById(activityId);
    }

    public List<ActivityResponseDTO> getUserActivities(String userId) {
        return mapToResponse(repo.findByOwnerId(userId));
    }

    public List<ActivityResponseDTO> getUserJoinedActivities(String userId) {
        return mapToResponse(repo.findByParticipantsContains(userId));
    }

    public List<ActivityResponseDTO> getFriendsActivities(String userId) {
        List<String> friendIds = userServiceClient.getFriendIds(userId);
        if (friendIds.isEmpty()) return List.of();
        return mapToResponse(repo.findByOwnerIdIn(friendIds));
    }

    public void removeUserFromAllActivities(String userId) {
        List<Activity> activities = repo.findByParticipantsContains(userId);
        for (Activity activity : activities) {
            activity.getParticipants().remove(userId);
            repo.save(activity);
        }
    }

    public void deleteActivitiesOwnedByUser(String userId) {
        List<Activity> owned = repo.findByOwnerId(userId);
        if (!owned.isEmpty()) {
            repo.deleteAll(owned);
        }
    }

    private List<ActivityResponseDTO> mapToResponse(List<Activity> activities) {
        if (activities.isEmpty()) {
            return List.of();
        }

        Set<String> userIds = new HashSet<>();
        for (Activity activity : activities) {
            if (activity.getOwnerId() != null) {
                userIds.add(activity.getOwnerId());
            }
            if (activity.getParticipants() != null) {
                userIds.addAll(activity.getParticipants());
            }
        }

        Map<String, UserSummaryDTO> users = fetchUserSummaries(userIds);

        return activities.stream()
                .map(activity -> toDto(activity, users))
                .collect(Collectors.toList());
    }

    private Map<String, UserSummaryDTO> fetchUserSummaries(Set<String> userIds) {
        Map<String, UserSummaryDTO> map = new HashMap<>();

        List<UserDTO> fetched = userServiceClient.getUserSummaries(userIds);
        for (UserDTO user : fetched) {
            map.put(user.getClerkId(), new UserSummaryDTO(
                    user.getClerkId(),
                    user.getFirstName(),
                    user.getLastName()
            ));
        }

        for (String id : userIds) {
            if (map.containsKey(id)) {
                continue;
            }
            try {
                String fullName = userServiceClient.getUserName(id);
                String first = fullName;
                String last = "";
                int idx = fullName.indexOf(' ');
                if (idx > 0) {
                    first = fullName.substring(0, idx);
                    last = fullName.substring(idx + 1);
                }
                map.put(id, new UserSummaryDTO(id, first, last));
            } catch (Exception e) {
                map.put(id, new UserSummaryDTO(id, id, ""));
            }
        }

        return map;
    }

    private ActivityResponseDTO toDto(Activity activity, Map<String, UserSummaryDTO> users) {
        ActivityResponseDTO dto = new ActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setOwnerId(activity.getOwnerId());
        dto.setTitle(activity.getTitle());
        dto.setSportType(activity.getSportType());
        dto.setStartTime(activity.getStartTime());
        dto.setLocation(activity.getLocation());
        dto.setGpxPath(activity.getGpxPath());
        dto.setCreatedAt(activity.getCreatedAt());

        dto.setOwner(users.getOrDefault(
                activity.getOwnerId(),
                new UserSummaryDTO(activity.getOwnerId(), "", "")
        ));

        Set<String> participantIds = activity.getParticipants() != null
            ? activity.getParticipants()
            : Set.of();

        List<UserSummaryDTO> participantDtos = participantIds.stream()
            .map(id -> users.getOrDefault(id, new UserSummaryDTO(id, "", "")))
            .collect(Collectors.toList());

        dto.setParticipants(participantDtos);
        return dto;
    }
}

