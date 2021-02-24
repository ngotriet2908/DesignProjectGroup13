package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.project.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
