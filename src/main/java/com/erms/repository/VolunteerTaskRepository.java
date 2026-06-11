package com.erms.repository;

import com.erms.entity.VolunteerTask;
import com.erms.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerTaskRepository extends JpaRepository<VolunteerTask, Long> {

    List<VolunteerTask> findByVolunteerIdAndTaskStatus(Long volunteerId, TaskStatus taskStatus);
}
