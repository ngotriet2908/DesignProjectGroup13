package com.group13.tcsprojectgrading.model.user;


import com.google.common.collect.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.group13.tcsprojectgrading.model.user.ApplicationUserPermission.*;

import java.util.Set;
import java.util.stream.Collectors;

public enum ApplicationUserRole {
    TEACHING_ASSISTANT(Sets.newHashSet(PROJECT_READ, PROJECT_WRITE, COURSE_READ, RUBRICS_READ)),
    TEACHER(Sets.newHashSet(PROJECT_READ, PROJECT_WRITE, COURSE_READ, COURSE_WRITE, PROJECT_WRITE, RUBRICS_READ)),
    ADMIN(Sets.newHashSet(COURSE_READ, COURSE_WRITE));

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
