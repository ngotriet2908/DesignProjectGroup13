package com.group13.tcsprojectgrading.service.project;


import com.group13.tcsprojectgrading.model.project.Attachment;
import com.group13.tcsprojectgrading.repository.project.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {

    private AttachmentRepository repository;

    @Autowired
    public AttachmentService(AttachmentRepository repository) {
        this.repository = repository;
    }

    public Attachment addNewAttachment(Attachment attachment) {
        return repository.save(attachment);
    }
}
