package com.group13.tcsprojectgrading.models.permissions;


import javax.persistence.*;
import java.util.Collection;

@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<ProjectRole> projectRoles;

    public Privilege(String name) {
        this.name = name;
    }

    public Privilege() {

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

    public Collection<ProjectRole> getProjectRoles() {
        return projectRoles;
    }

    public void setProjectRoles(Collection<ProjectRole> projectRoles) {
        this.projectRoles = projectRoles;
    }
}