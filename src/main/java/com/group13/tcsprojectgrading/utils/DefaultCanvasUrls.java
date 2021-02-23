package com.group13.tcsprojectgrading.utils;

public class DefaultCanvasUrls {

    public static final String HOST = "utwente-dev.instructure.com";
    public static final String SCHEME = "https";

    public static final String SINGLE_GROUP_MARK = "-";
    public static final String SINGLE_GROUP_NAME_PREFIX = "Single Group of ";

    public static final String BASE_URL = SCHEME + "://" + HOST;

    public static final String PROFILE_URL = BASE_URL + "/api/v1/users/self/profile";

    public static final String SELF_COURSE_URL = BASE_URL + "/api/v1/courses";

    public static final String COURSE_USERS_PATH = "/api/v1/courses/{course_id}/users";

    public static final String COURSE_GROUPS_PATH = "/api/v1/courses/{course_id}/groups";

    public static final String COURSE_GROUP_CATEGORY_PATH = "/api/v1/courses/{course_id}/group_categories";

    public static final String COURSE_GROUP_MEMBERSHIP_PATH = "/api/v1/groups/{group_id}/memberships";

    public static final String COURSE_ASSIGNMENT_PATH = "/api/v1/courses/{course_id}/assignments";

    public static final String COURSE_ASSIGNMENT_GROUPS_PATH = "/api/v1/courses/{course_id}/assignment_groups";

    public static final String SUBMISSIONS_PATH = "/api/v1/courses/{course_id}/assignments/{assignment_id}/submissions";


    public static final String TEACHER_ROLE = "TeacherEnrollment";
    public static final String STUDENT_ROLE = "StudentEnrollment";
    public static final String TA_ROLE = "TaEnrollment";
}
