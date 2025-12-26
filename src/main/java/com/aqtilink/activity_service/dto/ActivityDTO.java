package com.aqtilink.activity_service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ActivityDTO {

    private UUID id;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private String sportType;

    private String ownerId;
    private String ownerName;

    private List<FriendDTO> participants;

    public ActivityDTO() {}

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public List<FriendDTO> getParticipants() { return participants; }
    public void setParticipants(List<FriendDTO> participants) { this.participants = participants; }
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
}
