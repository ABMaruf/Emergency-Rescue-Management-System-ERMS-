package com.erms.repository;

import com.erms.entity.Volunteer;
import com.erms.entity.User;
import com.erms.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByUserId(Long userId);

    Optional<Volunteer> findByUser(User user);

    Optional<Volunteer> findByUserUsername(String username);

    List<Volunteer> findByAvailabilityStatus(AvailabilityStatus status);

    long countByAvailabilityStatus(AvailabilityStatus status);

    @Query("SELECT v FROM Volunteer v WHERE v.availabilityStatus = 'AVAILABLE' ORDER BY v.rating DESC")
    List<Volunteer> findAvailableOrderByRating();
}
