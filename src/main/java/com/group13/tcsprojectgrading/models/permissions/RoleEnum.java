package com.group13.tcsprojectgrading.models.permissions;

public enum RoleEnum {
    TEACHER("TEACHER_ROLE"),
    TA("TA_ROLE"),
    TA_GRADING("TA_GRADING_ROLE"),
    STUDENT("STUDENT_ROLE"),
    NULL("null");

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

    public static RoleEnum fromName(String name) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.name.equals(name)) {
                return roleEnum;
            }
        }
        return RoleEnum.NULL;
    }

    public static RoleEnum getRoleFromEnrolment(String enrolment) {
        switch (enrolment) {
            case "TeacherEnrollment":
                return TEACHER;
            case "TaEnrollment":
                return TA;
            case "TaGradingEnrollment":
                return TA_GRADING;
            default:
                return STUDENT;
        }
    }
}
