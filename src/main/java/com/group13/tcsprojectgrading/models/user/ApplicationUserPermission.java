package com.group13.tcsprojectgrading.models.user;

public enum ApplicationUserPermission {
    PROJECT_READ("project:read"),
    PROJECT_WRITE("project:write"),
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write"),
    RUBRICS_READ("rubrics:read"),
    RUBRICS_WRITE("rubrics:write");
    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
