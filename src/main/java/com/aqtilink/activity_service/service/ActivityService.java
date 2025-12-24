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
    public Activity create(Activity activity, Set<UUID> notifyFriends) {
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

    public void joinActivity(UUID activityId, UUID userId) {
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



    public List<Activity> getUserActivities(UUID userId) {
        return repo.findByOwnerId(userId);
    }

    public List<Activity> getUserJoinedActivities(UUID userId) {
        return repo.findByParticipantsContains(userId);
    }

    public List<Activity> getFriendsActivities(UUID userId) {
        // pridobi ID-je prijateljev iz user-service
        List<UUID> friendIds = userServiceClient.getFriendIds(userId);
        if (friendIds.isEmpty()) return List.of();
        return repo.findByOwnerIdIn(friendIds);
    }

    public List<Activity> getAllActivities() {
        return repo.findAll();
    }
    public void deleteActivity(UUID activityId) {
        repo.deleteById(activityId);
    }

}
