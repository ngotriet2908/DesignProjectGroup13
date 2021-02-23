package com.group13.tcsprojectgrading.repository.project;

import com.group13.tcsprojectgrading.model.project.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
