package com.group13.tcsprojectgrading.services.projects;


import com.group13.tcsprojectgrading.models.project.Attachment;
import com.group13.tcsprojectgrading.repositories.project.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {
    private final AttachmentRepository repository;

    @Autowired
    public AttachmentService(AttachmentRepository repository) {
        this.repository = repository;
    }

    public Attachment addNewAttachment(Attachment attachment) {
        return repository.save(attachment);
    }
}
