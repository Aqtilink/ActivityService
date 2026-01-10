package com.aqtilink.activity_service.controller;

import com.aqtilink.activity_service.dto.ActivityResponseDTO;
import com.aqtilink.activity_service.model.Activity;
import com.aqtilink.activity_service.service.ActivityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Controller for managing activities with endpoint mappings

@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activities", description = "Activity management endpoints")
public class ActivityController {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }
    
    @PostMapping("/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new activity", description = "Creates a new activity with the provided details and initial participants")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Activity created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ActivityResponseDTO create(@RequestBody Activity activity) {
        return service.create(activity, activity.getParticipants());
    }

    @PostMapping("/{activityId}/join/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Join an activity", description = "Adds a user as a participant to an existing activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully joined activity"),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void joinActivity(
        @Parameter(description = "ID of the activity to join") @PathVariable UUID activityId,
        @Parameter(description = "ID of the user joining") @PathVariable String userId
    ) {
        service.joinActivity(activityId, userId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's activities", description = "Retrieves all activities created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activities"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<ActivityResponseDTO> getUserActivities(
        @Parameter(description = "ID of the user") @PathVariable String userId
    ) {
        return service.getUserActivities(userId);
    }

    @GetMapping("/friends-feed/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get friends' activities feed", description = "Retrieves activities created by user's friends")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved friends' activities"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<ActivityResponseDTO> getFriendsFeed(
        @Parameter(description = "ID of the user") @PathVariable String userId
    ) {
        return service.getFriendsActivities(userId);
    }

    @GetMapping("/joined/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get joined activities", description = "Retrieves all activities that a user has joined as a participant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved joined activities"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<ActivityResponseDTO> getJoinedActivities(
        @Parameter(description = "ID of the user") @PathVariable String userId
    ) {
        return service.getUserJoinedActivities(userId);
    }
    
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all activities", description = "Retrieves all activities in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all activities")
    })
    public List<ActivityResponseDTO> getAllActivities() {
        return service.getAllActivities();
    }
    @DeleteMapping("{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an activity", description = "Deletes a specific activity by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Activity deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void deleteActivity(
        @Parameter(description = "ID of the activity to delete") @PathVariable UUID activityId
    ) {
        service.deleteActivity(activityId);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete all activities by user", description = "Deletes all activities created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Activities deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteActivitiesByUser(
        @Parameter(description = "ID of the user whose activities to delete") @PathVariable String userId
    ) {
        service.deleteActivitiesOwnedByUser(userId);
    }
    
    @DeleteMapping("/participants/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove user from all activities", description = "Removes a user as a participant from all activities they've joined")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User removed from activities successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void removeUserFromActivities(
        @Parameter(description = "ID of the user to remove from activities") @PathVariable String userId
    ) {
        service.removeUserFromAllActivities(userId);
    }
    
}
