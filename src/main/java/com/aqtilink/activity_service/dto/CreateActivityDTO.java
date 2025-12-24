package com.aqtilink.activity_service.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import com.aqtilink.activity_service.model.SportType;


public class CreateActivityDTO {
    private UUID ownerId;
    private String title;
    private SportType sportType;
    private LocalDateTime startTime;
    private String location;
    private Set<UUID> notifyFriends;

    // getters & setters
    public UUID getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public SportType getSportType() {
        return sportType;
    }
    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Set<UUID> getNotifyFriends() {
        return notifyFriends;
    }
}
