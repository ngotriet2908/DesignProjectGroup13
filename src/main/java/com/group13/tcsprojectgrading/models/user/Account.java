package com.group13.tcsprojectgrading.models.user;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Account {
    @Id
    private String id;
    private String name;
    private String loginId;
    private String shortName;
    private String sortableName;
    private String primaryEmail;

    @OneToMany(mappedBy = "course")
    private Set<Participant> participants = new HashSet<>();

    public Account(String id, String name, Set<Participant> participants) {
        this.id = id;
        this.name = name;
        this.participants = participants;
    }

    public Account(String id, String name, String login_id, String short_name, String sortable_name, String primary_email) {
        this.id = id;
        this.name = name;
        this.loginId = login_id;
        this.shortName = short_name;
        this.sortableName = sortable_name;
        this.primaryEmail = primary_email;
    }

    public Account(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Account() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public void addToParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSortableName() {
        return sortableName;
    }

    public void setSortableName(String sortableName) {
        this.sortableName = sortableName;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", loginId='" + loginId + '\'' +
                ", shortName='" + shortName + '\'' +
                ", sortableName='" + sortableName + '\'' +
                ", primaryEmail='" + primaryEmail + '\'' +
                '}';
    }
}
