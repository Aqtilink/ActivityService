package com.aqtilink.activity_service.controller;

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
    public Activity create(@RequestBody Activity activity) {
        return service.create(activity, activity.getParticipants());
    }
    /*
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Activity create(
            @RequestParam UUID ownerId,
            @RequestParam String title,
            @RequestParam String sportType,
            @RequestParam String startTime,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) MultipartFile gpxFile,
            @RequestParam(required = false) Set<UUID> participants
    ) throws IOException {

        Activity activity = new Activity();
        activity.setOwnerId(ownerId);
        activity.setTitle(title);
        activity.setSportType(Enum.valueOf(SportType.class, sportType.toUpperCase()));
        activity.setStartTime(LocalDateTime.parse(startTime));
        activity.setLocation(location);
        activity.setParticipants(participants);

        if (gpxFile != null && !gpxFile.isEmpty()) {
            // za zdaj shranimo datoteko lokalno, lahko tudi byte[] ali path
            String gpxPath = "/tmp/gpx_" + UUID.randomUUID() + ".gpx";
            gpxFile.transferTo(new File(gpxPath));
            activity.setGpxPath(gpxPath);
        }

        return service.create(activity);
    }
    */

    @PostMapping("/{activityId}/join/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void joinActivity(@PathVariable UUID activityId,@PathVariable UUID userId) {
        service.joinActivity(activityId, userId);
    }

    @GetMapping("/user/{userId}")
    public List<Activity> getUserActivities(@PathVariable UUID userId) {
        return service.getUserActivities(userId);
    }

    @GetMapping("/friends-feed/{userId}")
    public List<Activity> getFriendsFeed(@PathVariable UUID userId) {
    return service.getFriendsActivities(userId);
    }

    @GetMapping("/joined/{userId}")
    public List<Activity> getJoinedActivities(@PathVariable UUID userId) {
        return service.getUserJoinedActivities(userId);
    }
    
    @GetMapping("/all")
    public List<Activity> getAllActivities() {
        return service.getAllActivities();
    }
    @DeleteMapping("{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable UUID activityId) {
        service.deleteActivity(activityId);
    }
    
    
}
