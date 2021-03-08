package com.group13.tcsprojectgrading.canvas.api;

public class CanvasEndpoints {
    public static final String HOST = "utwente-dev.instructure.com";
    public static final String SCHEME = "https";

    public static final String BASE_URL = SCHEME + "://" + HOST;

    public static final String PROFILE_URL = "/api/v1/users/self/profile";

    public static final String SELF_COURSE_URL = "/api/v1/courses";

    public static final String COURSE_URL = "/api/v1/courses/{courseId}";

    public static final String COURSE_USERS_PATH = "/api/v1/courses/{courseId}/users";

    public static final String COURSE_SINGLE_USER = "/api/v1/courses/{course_id}/users/{id}";

    public static final String COURSE_GROUPS_PATH = "/api/v1/courses/{courseId}/groups";

    public static final String COURSE_GROUP_CATEGORY_PATH = "/api/v1/courses/{courseId}/group_categories";

    public static final String COURSE_GROUP_CATEGORY_GROUP_PATH = "/api/v1/group_categories/{group_category_id}/groups";

    public static final String COURSE_GROUP_MEMBERSHIP_PATH = "/api/v1/groups/{group_id}/memberships";

    public static final String COURSE_ASSIGNMENT_PATH = "/api/v1/courses/{courseId}/assignments";
    public static final String COURSE_SINGLE_ASSIGNMENT_PATH = "/api/v1/courses/{courseId}/assignments/{projectId}";

    public static final String COURSE_ASSIGNMENT_GROUPS_PATH = "/api/v1/courses/{courseId}/assignment_groups";

    public static final String SUBMISSIONS_PATH = "/api/v1/courses/{courseId}/assignments/{assignmentId}/submissions";
    public static final String SUBMISSION_SUMMARY_PATH = "/api/v1/courses/{courseId}/assignments/{assignmentId}/submission_summary";
}
