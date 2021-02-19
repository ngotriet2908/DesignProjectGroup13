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

    @OneToMany(mappedBy = "course")
    private Set<Participant> participants = new HashSet<>();

    public Account(String id, String name, Set<Participant> participants) {
        this.id = id;
        this.name = name;
        this.participants = participants;
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

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
