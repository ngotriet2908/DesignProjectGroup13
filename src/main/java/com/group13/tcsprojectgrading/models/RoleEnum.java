package com.group13.tcsprojectgrading.models;

public enum RoleEnum {
    TEACHER("TEACHER_ROLE"),
    TA("TA_ROLE"),
    STUDENT("STUDENT_ROLE");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static RoleEnum getRoleFromEnrolment(String enrolment) {
        if (enrolment.equals("TeacherEnrollment")) {
            return TEACHER;
        } else if (enrolment.equals("TaEnrollment")) {
            return TA;
        } else {
            return STUDENT;
        }
    }
}
