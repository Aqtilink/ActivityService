package com.aqtilink.activity_service.service;

import com.aqtilink.activity_service.model.Activity;
import com.aqtilink.activity_service.repository.ActivityRepository;
import com.aqtilink.activity_service.client.UserServiceClient;
import com.aqtilink.activity_service.messaging.NotificationPublisherActivity;
import com.aqtilink.activity_service.dto.ActivityDTO;
import com.aqtilink.activity_service.dto.NotificationEventDTO;
import com.aqtilink.activity_service.exception.ActivityAlreadyStartedException;
import com.aqtilink.activity_service.mapper.ActivityMapper;



import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ActivityService {

    private final ActivityRepository repo;
    private final UserServiceClient userServiceClient;
    private final NotificationPublisherActivity notificationPublisherActivity;
    private final ActivityMapper activityMapper;

    public ActivityService(ActivityRepository repo, UserServiceClient userServiceClient, NotificationPublisherActivity notificationPublisherActivity, ActivityMapper activityMapper) {
        this.repo = repo;
        this.userServiceClient = userServiceClient;
        this.notificationPublisherActivity = notificationPublisherActivity;
        this.activityMapper = activityMapper;
    }
    public Activity create(Activity activity) {
        if(activity.getParticipants() == null) {
            activity.setParticipants(new HashSet<>());
        }
        activity.getParticipants().add(activity.getOwnerId());
        Activity saved = repo.save(activity);

        List<String> friendEmails = userServiceClient.getFriendEmails(activity.getOwnerId());

        for (String email : friendEmails) {
            NotificationEventDTO event = new NotificationEventDTO();
            event.setEmail(email);
            event.setSubject("Your friend created a new activity!");
            event.setMessage("Your friend has created a new activity: " + activity.getTitle() + ". Join them now!");
            notificationPublisherActivity.publish(event);
        }
    
        return saved;
    }

    public void joinActivity(UUID activityId, String userId) {
        Activity activity = repo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if(activity.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ActivityAlreadyStartedException();
        }

        if(activity.getParticipants() == null) {
            activity.setParticipants(new HashSet<>());
        }

        if (activity.getParticipants().contains(userId)) {
            return;
        }

        activity.getParticipants().add(userId);
        repo.save(activity);
    }



    public List<ActivityDTO> getUserActivities(String userId) {
        List<Activity> activities = repo.findByOwnerId(userId);
        return activities.stream()
                        .map(activityMapper::toDto)
                        .toList();
    }

    public List<ActivityDTO> getUserJoinedActivities(String userId) {
        List<Activity> activities = repo.findByParticipantsContains(userId);
        return activities.stream()
                .map(activityMapper::toDto)
                .toList();
    }

    public List<ActivityDTO> getFriendsActivities(String userId) {
        List<Activity> activities = repo.findByOwnerIdIn(userServiceClient.getFriendIds(userId));
        return activities.stream()
                        .map(activityMapper::toDto)
                        .toList();
    }

    public List<ActivityDTO> getFeed(String userId) {
        // Get user's own activities
        List<Activity> ownActivities = repo.findByOwnerId(userId);
        
        // Get friends' activities
        List<Activity> friendsActivities = repo.findByOwnerIdIn(userServiceClient.getFriendIds(userId));
        
        // Get activities user has joined (excluding their own)
        List<Activity> joinedActivities = repo.findByParticipantsContains(userId);
        
        // Combine all activities and remove duplicates by ID
        return Stream.of(ownActivities, friendsActivities, joinedActivities)
                .flatMap(List::stream)
                .distinct()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Activity> getAllActivities() {
        return repo.findAll();
    }
    public void deleteActivity(UUID activityId) {
        repo.deleteById(activityId);
    }

    public void deleteUserActivities(String ownerId) {
        // Remove user from all activities they participated in
        List<Activity> participatedActivities = repo.findByParticipantsContains(ownerId);
        for (Activity activity : participatedActivities) {
            activity.getParticipants().remove(ownerId);
            repo.save(activity);
        }
        
        // Delete all activities owned by the user
        repo.deleteByOwnerId(ownerId);
    }

}
