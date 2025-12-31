package com.aqtilink.activity_service.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import com.aqtilink.activity_service.model.SportType;


public class CreateActivityDTO {
    private String ownerId;
    private String title;
    private SportType sportType;
    private LocalDateTime startTime;
    private String location;
    private Set<String> notifyFriends;

    // getters & setters
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
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
    public Set<String> getNotifyFriends() {
        return notifyFriends;
    }
}
