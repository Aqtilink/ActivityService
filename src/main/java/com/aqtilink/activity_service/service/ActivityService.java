package com.aqtilink.activity_service.service;

import com.aqtilink.activity_service.model.Activity;
import com.aqtilink.activity_service.repository.ActivityRepository;
import com.aqtilink.activity_service.client.UserServiceClient;
import com.aqtilink.activity_service.messaging.NotificationPublisherActivity;
import com.aqtilink.activity_service.dto.NotificationEventDTO;
import com.aqtilink.activity_service.exception.ActivityAlreadyStartedException;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Activity create(Activity activity, Set<String> notifyFriends) {
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

        return saved;
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



    public List<Activity> getAllActivities() {
        return repo.findAll().stream()
            .peek(this::enrichWithOwnerName)
            .collect(Collectors.toList());
    }

    public void deleteActivity(UUID activityId) {
        repo.deleteById(activityId);
    }

    private void enrichWithOwnerName(Activity activity) {
        try {
            String ownerName = userServiceClient.getUserName(activity.getOwnerId());
            activity.setOwnerName(ownerName != null ? ownerName : "Unknown");
        } catch (Exception e) {
            activity.setOwnerName("Unknown");
        }
    }

    public List<Activity> getUserActivities(String userId) {
        return repo.findByOwnerId(userId).stream()
            .peek(this::enrichWithOwnerName)
            .collect(Collectors.toList());
    }

    public List<Activity> getUserJoinedActivities(String userId) {
        return repo.findByParticipantsContains(userId).stream()
            .peek(this::enrichWithOwnerName)
            .collect(Collectors.toList());
    }

    public List<Activity> getFriendsActivities(String userId) {
        List<String> friendIds = userServiceClient.getFriendIds(userId);
        if (friendIds.isEmpty()) return List.of();
        return repo.findByOwnerIdIn(friendIds).stream()
            .peek(this::enrichWithOwnerName)
            .collect(Collectors.toList());
    }
}

