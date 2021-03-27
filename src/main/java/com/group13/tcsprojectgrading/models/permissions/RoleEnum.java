package com.group13.tcsprojectgrading.models.permissions;

public enum RoleEnum {
    TEACHER("TEACHER_ROLE"),
    TA("TA_ROLE"),
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
        if (enrolment.equals("TeacherEnrollment")) {
            return TEACHER;
        } else if (enrolment.equals("TaEnrollment")) {
            return TA;
        } else {
            return STUDENT;
        }
    }
}
