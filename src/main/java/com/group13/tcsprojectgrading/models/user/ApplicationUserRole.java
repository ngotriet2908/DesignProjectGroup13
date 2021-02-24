package com.group13.tcsprojectgrading.models.user;


//import com.google.common.collect.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.group13.tcsprojectgrading.models.user.ApplicationUserPermission.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum ApplicationUserRole {
    TEACHING_ASSISTANT(new HashSet<>(Arrays.asList(PROJECT_READ, PROJECT_WRITE, COURSE_READ, RUBRICS_READ))),
    TEACHER(new HashSet<>(Arrays.asList(PROJECT_READ, PROJECT_WRITE, COURSE_READ, COURSE_WRITE, PROJECT_WRITE, RUBRICS_READ))),
    ADMIN(new HashSet<>(Arrays.asList(COURSE_READ, COURSE_WRITE)));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public String getSimpleAuthoritiesLabel() {
        return "ROLE_" + this.name();
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

}
