package org.app.eventservice.repository;

import org.app.eventservice.entity.DeletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DeletionStatusRepository extends JpaRepository<DeletionStatus, String> {

    @Query("SELECT ds FROM DeletionStatus ds WHERE ds.expiryTime < :now")
    List<DeletionStatus> findExpiredStatuses(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM DeletionStatus ds WHERE ds.expiryTime < :now")
    void deleteExpiredStatuses(LocalDateTime now);
}

