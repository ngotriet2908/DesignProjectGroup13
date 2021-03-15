package com.group13.tcsprojectgrading.models;

public enum PrivilegeEnum {
    MANAGE_GRADERS_OPEN("ManageGraders_open"),
    RUBRIC_READ("Rubric_read"),
    RUBRIC_WRITE("Rubric_write"),
    GRADING_WRITE_ALL("Grading_write_all"),
    GRADING_WRITE_SINGLE("Grading_write_assigned")
    ;

    private final String name;

    PrivilegeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
