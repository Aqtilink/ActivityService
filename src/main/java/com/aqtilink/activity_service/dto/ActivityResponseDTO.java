package com.aqtilink.activity_service.dto;

import com.aqtilink.activity_service.model.SportType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ActivityResponseDTO {
    private UUID id;
    private String ownerId;
    private String title;
    private SportType sportType;
    private LocalDateTime startTime;
    private String location;
    private String gpxPath;
    private LocalDateTime createdAt;
    private UserSummaryDTO owner;
    private List<UserSummaryDTO> participants;

    //getters and setters
    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}
    public String getOwnerId() {return ownerId;}
    public void setOwnerId(String ownerId) {this.ownerId = ownerId;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public SportType getSportType() {return sportType;}
    public void setSportType(SportType sportType) {this.sportType = sportType;}
    public LocalDateTime getStartTime() {return startTime;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public String getGpxPath() {return gpxPath;}
    public void setGpxPath(String gpxPath) {this.gpxPath = gpxPath;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public UserSummaryDTO getOwner() {return owner;}
    public void setOwner(UserSummaryDTO owner) {this.owner = owner;}
    public List<UserSummaryDTO> getParticipants() {return participants;}
    public void setParticipants(List<UserSummaryDTO> participants) {this.participants = participants;}
}
