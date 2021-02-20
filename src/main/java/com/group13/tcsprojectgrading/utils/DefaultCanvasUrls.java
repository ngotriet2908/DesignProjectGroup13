package com.group13.tcsprojectgrading.utils;

public class DefaultCanvasUrls {

    public static final String HOST = "utwente-dev.instructure.com";
    public static final String SCHEME = "https";


    public static final String BASE_URL = "https://utwente-dev.instructure.com";

    public static final String USER_INFO_URL = BASE_URL + "/api/v1/users/self";
    public static final String PROFILE_URL = BASE_URL + "/api/v1/users/self/profile";

    public static final String SELF_COURSE_URL = BASE_URL + "/api/v1/courses";

    public static final String COURSE_USERS_PATH = "/api/v1/courses/{course_id}/users";
    public static final String COURSE_USERS_URL = BASE_URL + COURSE_USERS_PATH;

    public static final String TEACHER_ROLE = "TeacherEnrollment";
    public static final String STUDENT_ROLE = "StudentEnrollment";
    public static final String TA_ROLE = "TaEnrollment";
}
