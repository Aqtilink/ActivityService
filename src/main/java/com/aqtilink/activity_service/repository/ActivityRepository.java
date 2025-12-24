package com.aqtilink.activity_service.repository;

import com.aqtilink.activity_service.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByOwnerId(UUID ownerId);

    List<Activity> findByParticipantsContains(UUID userId);

    List<Activity> findByOwnerIdIn(List<UUID> ownerIds);
}
