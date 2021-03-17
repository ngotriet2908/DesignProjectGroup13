package com.group13.tcsprojectgrading.models;

public enum PrivilegeEnum {
    MANAGE_GRADERS_OPEN("ManageGraders_open"),

    RUBRIC_READ("Rubric_read"),
    RUBRIC_WRITE("Rubric_write"),

    STATISTIC_READ("Statistic_read"),
    STATISTIC_WRITE("Statistic_write"),

    ADMIN_TOOLBAR_VIEW("AdminToolbar_view"),
    TODO_LIST_VIEW("TodoList_view"),

    STUDENT_PERSONAL_VIEW("studentView_view"),

    GRADING_WRITE_ALL("Grading_write_all"),
    GRADING_WRITE_SINGLE("Grading_write_assigned"),
    GRADING_READ("Grading_read")

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
