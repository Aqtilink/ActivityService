package com.aqtilink.activity_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private String location;

    private String gpxPath;

    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(
        name = "activity_participants",
        joinColumns = @JoinColumn(name = "activity_id")
    )
    @Column(name = "user_id")
    private Set<String> participants = new HashSet<>();

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Activity() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
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
    public String getGpxPath() {
        return gpxPath;
    }
    public void setGpxPath(String gpxPath) {
        this.gpxPath = gpxPath;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Set<String> getParticipants() {
        return participants;
    }
    public void setParticipants(Set<String> participants) {
        this.participants = participants;
    }
}