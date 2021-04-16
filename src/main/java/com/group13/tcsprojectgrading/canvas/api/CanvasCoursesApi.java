package com.group13.tcsprojectgrading.canvas.api;

import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasEndpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * This class consists of methods that are used to retrieve data from canvas course apis
 */
@Component
public class CanvasCoursesApi {
    private final CanvasApi canvasApi;

    @Autowired
    public CanvasCoursesApi(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    /**
     * Get Canvas courses that user participates in
     * @return list of courses in Json strings
     */
    public List<String> getUserCourseList() {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.SELF_COURSE_URL)
                    .build().toUri();

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get a Canvas course if user have access to it
     * @param courseId canvas course id
     * @return canvas course Json string
     */
    public String getUserCourse(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_URL)
                    .queryParam("include[]", "image_url")
                    .build(courseId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas assignments/projects in a course
     * @param courseId canvas course id
     * @return a list of canvas assignments/projects Json string
     */
    public List<String> getCourseProjects(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_ASSIGNMENT_PATH)
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas participants in a course (with enrollments aka roles)
     * @param courseId canvas course id
     * @return a list of Canvas participants Json string
     */
    public List<String> getCourseParticipants(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_USERS_PATH)
                    .queryParam("include[]", "enrollments")
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas participants in a course (with enrollments aka roles, avatar url and email)
     * @param courseId canvas course id
     * @return a list of Canvas participants Json string
     */
    public List<String> getCourseParticipantsWithAvatars(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_USERS_PATH)
                    .queryParam("include[]", "enrollments", "avatar_url", "email")
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get a participant in a Canvas course
     * @param courseId canvas course id
     * @param userId canvas user id
     * @return participant json string
     */
    public String getCourseUser(Long courseId, Long userId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_SINGLE_USER)
                    .build(courseId, userId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas graders in a course (with enrollments aka roles)
     * @param courseId canvas course id
     * @return a list of Canvas graders Json string
     */
    public List<String> getCourseGraders(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_USERS_PATH)
                    .queryParam("include[]", "enrollments")
                    .queryParam("enrollment_type[]", "teacher","ta")
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas students in a course (with enrollments aka roles)
     * @param courseId canvas course id
     * @return a list of Canvas students Json string
     */
    public List<String> getCourseStudents(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_USERS_PATH)
                    .queryParam("include[]", "enrollments")
                    .queryParam("enrollment_type[]", "student")
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas group categories in a course
     * @param courseId canvas course id
     * @return a list of group categories Json string
     */
    public List<String> getCourseGroupCategories(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_GROUP_CATEGORY_PATH)
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas groups in a group category in a course
     * @param groupCatId canvas group category id
     * @return a list of groups Json string
     */
    public List<String> getCourseGroupCategoryGroup(Long groupCatId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_GROUP_CATEGORY_GROUP_PATH)
                    .queryParam("include[]", "users")
                    .build(groupCatId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas groups in a course
     * @param courseId canvas group category id
     * @return a list of groups Json string
     */
    public List<String> getCourseGroups(Long courseId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_GROUPS_PATH)
                    .queryParam("include[]", "users")
                    .build(courseId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas group memberships in a group
     * @param groupId canvas group id
     * @return a list of members Json string
     */
    public List<String> getGroupMemberships(Long groupId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_GROUP_MEMBERSHIP_PATH)
                    .build(groupId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas group users in a group
     * @param groupId canvas group id
     * @return a list of members Json string
     */
    public List<String> getGroupUsers(Long groupId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.GROUP_USERS_PATH)
                    .build(groupId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas groups that user are in
     * @param accountId canvas account id
     * @return a list of groups Json string
     */
    public List<String> getGroupsFromAccount(Long accountId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_GROUPS_BY_ACCOUNT_PATH)
                    .build(accountId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas submissions in a project in a course
     * @param courseId canvas course id
     * @param assignmentId canvas assignment id
     * @return a list of submissions Json string
     */
    public List<String> getSubmissions(Long courseId, Long assignmentId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.SUBMISSIONS_PATH)
                    .queryParam("include[]", "group","submission_history","submission_comments")
                    .queryParam("grouped", true)
                    .build(courseId, assignmentId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas submissions in a project in a course with additional info (group, submission_history, user, submission comments)
     * @param courseId canvas course id
     * @param assignmentId canvas assignment id
     * @return a list of submissions Json string
     */
    public List<String> getSubmissionsInfo(Long courseId, Long assignmentId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.SUBMISSIONS_PATH)
                    .queryParam("include[]", "group", "submission_history","user","submission_comments")
//                    .queryParam("grouped", true)
                    .build(courseId, assignmentId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas submission in a project in a course with submission comments
     * @param courseId canvas course id
     * @param projectId canvas assignment id
     * @param submitterId canvas user submitter id
     * @return submission Json string
     */
    public String getSubmission(Long courseId, Long projectId , Long submitterId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.SUBMISSION_PATH)
                    .queryParam("include[]", "submission_comments")
                    .build(courseId, projectId, submitterId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Get Canvas assignment in a course
     * @param courseId canvas course id
     * @param projectId canvas assignment id
     * @return Canvas project Json string
     */
    public String getCourseProject(Long courseId, Long projectId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_SINGLE_ASSIGNMENT_PATH)
                    .build(courseId, projectId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Upload grades to canvas to a project
     * @param courseId canvas course id
     * @param assignmentId canvas assignment id
     * @param queryParams list of grades in Canvas api format
     * @return Canvas project Json string
     */
    public String uploadGrades(Long courseId, Long assignmentId, List<String> queryParams) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.UPLOAD_GRADES_PATH)
                    .build(courseId, assignmentId);

            MultiValueMap<String, String> grades = new LinkedMultiValueMap();
            queryParams.forEach(queryParam -> {
                grades.add(queryParam.split("=")[0], queryParam.split("=")[1]);
            });

            return this.canvasApi.postRequest(uri, HttpMethod.POST, authorizedClient, grades);
        }
    }

}