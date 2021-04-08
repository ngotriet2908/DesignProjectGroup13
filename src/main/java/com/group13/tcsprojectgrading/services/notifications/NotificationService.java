package com.group13.tcsprojectgrading.services.notifications;

import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

@Service
public class NotificationService {
    private final EmailSender emailSender;

    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendNotification(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("project.grader.utwente@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.getMailSender().send(message);
    }

    public void sendIssueNotification(String to, String authorName, String projectName) {
        this.sendNotification(to,
                authorName + " has mentioned you in a new issue of the " + projectName + " project",
                "You can find the issue in the " + projectName + " project"
                );
    }

    public void sendIssueNotification(String to, String projectName) {
        this.sendNotification(to,
                "New issue in " + projectName + " project",
                "You can find the issue in the " + projectName + " project"
        );
    }

    public void sendResolvedNotification(String to, String issueSubject, String projectName) {
        this.sendNotification(to,
                "The issue " + issueSubject + " in project " + projectName + " has been resolved",
                "The issue created by you in the " + projectName + " project has been resolved."
        );
    }
}

