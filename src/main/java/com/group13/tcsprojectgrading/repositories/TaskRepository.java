package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.models.TaskId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, TaskId> {
}
