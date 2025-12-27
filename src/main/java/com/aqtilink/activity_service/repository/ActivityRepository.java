package com.aqtilink.activity_service.repository;

import com.aqtilink.activity_service.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByOwnerId(String ownerId);

    List<Activity> findByParticipantsContains(String userId);

    List<Activity> findByOwnerIdIn(List<String> ownerIds);

    @Transactional
    @Modifying
    void deleteByOwnerId(String ownerId);
}
