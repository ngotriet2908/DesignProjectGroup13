package com.group13.tcsprojectgrading.canvas.api;

import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasEndpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


@Component
public class CanvasCoursesApi {
    private final CanvasApi canvasApi;

    @Autowired
    public CanvasCoursesApi(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

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

    public String getSubmissionsSummary(Long courseId, Long assignmentId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.SUBMISSION_SUMMARY_PATH)
                    .build(courseId, assignmentId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

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

    public String getAssignmentGroups(Long courseId, Long assignmentId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.COURSE_ASSIGNMENT_GROUPS_PATH)
                    .queryParam("include[]", "assignments")
                    .queryParam("assignment_ids[]", assignmentId)
                    .build(courseId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

}