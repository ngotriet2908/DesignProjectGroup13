package com.group13.tcsprojectgrading.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
public class Attachment {

    @Id
    private Long id;
    private String uuid;
    private String displayName;
    private String filename;
    private String contentType;
    private String url;
    private Long size;

    @JsonIgnoreProperties({"attachments"})
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "course_group_id"),
            @JoinColumn(name = "project_id")
    })
    private Submission submission;

    public Attachment(Long id, String uuid, String displayName, String filename, String contentType, String url, Long size, Submission submission) {
        this.id = id;
        this.uuid = uuid;
        this.displayName = displayName;
        this.filename = filename;
        this.contentType = contentType;
        this.url = url;
        this.size = size;
        this.submission = submission;
    }

    public Attachment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", url='" + url + '\'' +
                ", size=" + size +
                ", submission=" + submission +
                '}';
    }
}
