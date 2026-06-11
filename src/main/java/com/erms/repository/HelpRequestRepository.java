package com.erms.repository;

import com.erms.entity.HelpRequest;
import com.erms.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {

    long countByStatus(RequestStatus status);

    List<HelpRequest> findBySubmittedByIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT r FROM HelpRequest r WHERE r.status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY r.createdAt DESC")
    List<HelpRequest> findAllActive();

    List<HelpRequest> findAllByOrderByCreatedAtDesc();
}
