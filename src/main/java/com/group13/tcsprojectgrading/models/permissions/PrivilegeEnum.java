package com.group13.tcsprojectgrading.models.permissions;

public enum PrivilegeEnum {
    PROJECT_READ("Project_read"),

    MANAGE_GRADERS_OPEN("ManageGraders_open"),
    MANAGE_GRADERS_EDIT("ManageGraders_edit"),
    MANAGE_GRADERS_SELF_EDIT("ManageGraders_self-edit"),

    RUBRIC_READ("Rubric_read"),
    RUBRIC_WRITE("Rubric_write"),
    RUBRIC_DOWNLOAD("Rubric_download"),

    STATISTIC_READ("Statistic_read"),
    STATISTIC_WRITE("Statistic_write"),

    ADMIN_TOOLBAR_VIEW("AdminToolbar_view"),
    TODO_LIST_VIEW("TodoList_view"),

    STUDENT_PERSONAL_VIEW("studentView_view"),

    GRADING_WRITE_ALL("Grading_write_all"),
    GRADING_WRITE_SINGLE("Grading_write_assigned"),
    GRADING_READ_ALL("Grading_read_all"),
    GRADING_READ_SINGLE("Grading_read_single"),

    SUBMISSIONS_SYNC("Submissions_sync"),
    SUBMISSIONS_READ("Submissions_read"),

    SUBMISSION_READ_SINGLE("Submission_read_single"),
    SUBMISSION_READ_ALL("Submission_read_all"),

    SUBMISSION_EDIT_SINGLE("Submission_edit_single"),
    SUBMISSION_EDIT_ALL("Submission_edit_all"),

//    ASSESSMENTS_EDIT_SINGLE("Assessment_edit_assigned"),
//    ASSESSMENTS_EDIT_ALL("Assessment_edit_all"),

    FEEDBACK_SEND("Feedback_send"),
    FEEDBACK_OPEN("Feedback_open"),

    FLAG_CREATE("Flag_create"),
    FLAG_DELETE("Flag_delete"),
    FLAG_ASSIGN("Flag_assign"),

    NULL("NULL")
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

    public static PrivilegeEnum fromName(String name) {
        for (PrivilegeEnum privilegeEnum : PrivilegeEnum.values()) {
            if (privilegeEnum.name.equals(name)) {
                return privilegeEnum;
            }
        }
        return PrivilegeEnum.NULL;
    }

    public static void main(String[] args) {
        PrivilegeEnum privilegeEnum = PrivilegeEnum.fromName("ManageGraders_open");
        System.out.println(privilegeEnum.name);
    }
}
