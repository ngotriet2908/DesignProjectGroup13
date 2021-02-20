package com.group13.tcsprojectgrading.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Account {
    @Id
    private String id;
    private String name;
    private String login_id;
    private String short_name;
    private String sortable_name;
    private String primary_email;

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
        this.login_id = login_id;
        this.short_name = short_name;
        this.sortable_name = sortable_name;
        this.primary_email = primary_email;
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

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getSortable_name() {
        return sortable_name;
    }

    public void setSortable_name(String sortable_name) {
        this.sortable_name = sortable_name;
    }

    public String getPrimary_email() {
        return primary_email;
    }

    public void setPrimary_email(String primary_email) {
        this.primary_email = primary_email;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", login_id='" + login_id + '\'' +
                ", short_name='" + short_name + '\'' +
                ", sortable_name='" + sortable_name + '\'' +
                ", primary_email='" + primary_email + '\'' +
                '}';
    }
}
