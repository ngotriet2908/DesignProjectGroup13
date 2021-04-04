package com.group13.tcsprojectgrading.models.grading;

import javax.persistence.*;

@Entity
public class IssueStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String name;

    public IssueStatus() {
    }

    public IssueStatus(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IssueStatus(String name) {
        this.name = name;
    }

    public IssueStatus(IssueStatusEnum status) {
        this.name = status.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
