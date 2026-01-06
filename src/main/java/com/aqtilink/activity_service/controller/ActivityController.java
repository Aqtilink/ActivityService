package com.aqtilink.activity_service.controller;

import com.aqtilink.activity_service.dto.ActivityResponseDTO;
import com.aqtilink.activity_service.model.Activity;
import com.aqtilink.activity_service.service.ActivityService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;




@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }
    
    @PostMapping("/json")
    @ResponseStatus(HttpStatus.CREATED) 
    public ActivityResponseDTO create(@RequestBody Activity activity) {
        return service.create(activity, activity.getParticipants());
    }

    @PostMapping("/{activityId}/join/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void joinActivity(@PathVariable UUID activityId, @PathVariable String userId) {
        service.joinActivity(activityId, userId);
    }

    @GetMapping("/user/{userId}")
    public List<ActivityResponseDTO> getUserActivities(@PathVariable String userId) {
        return service.getUserActivities(userId);
    }

    @GetMapping("/friends-feed/{userId}")
    public List<ActivityResponseDTO> getFriendsFeed(@PathVariable String userId) {
        return service.getFriendsActivities(userId);
    }

    @GetMapping("/joined/{userId}")
    public List<ActivityResponseDTO> getJoinedActivities(@PathVariable String userId) {
        return service.getUserJoinedActivities(userId);
    }
    
    @GetMapping("/all")
    public List<ActivityResponseDTO> getAllActivities() {
        return service.getAllActivities();
    }
    @DeleteMapping("{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable UUID activityId) {
        service.deleteActivity(activityId);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivitiesByUser(@PathVariable String userId) {
        service.deleteActivitiesOwnedByUser(userId);
    }
    
    @DeleteMapping("/participants/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserFromActivities(@PathVariable String userId) {
        service.removeUserFromAllActivities(userId);
    }
    
}
