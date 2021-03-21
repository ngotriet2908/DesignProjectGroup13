package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.repositories.SubmissionRepository;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionService {
    private final SubmissionRepository repository;
    private final AssessmentService assessmentService;

    @Autowired
    public SubmissionService(SubmissionRepository repository, AssessmentService assessmentService) {
        this.repository = repository;
        this.assessmentService = assessmentService;
    }

    public Submission addNewSubmission(Project project, String userId, String groupId,
                                       String date, String name, String comments, String attachments) {
        Submission currentSubmission = repository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
                project, userId, groupId, date);

        if (currentSubmission != null) {
            return null;
        } else {
            return repository.save(new Submission(date, userId, groupId, project, name, comments, attachments));
        }
    }

    public Submission saveFlags(Submission submission) {
        Submission currentSubmission = repository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
                submission.getProject(),
                submission.getUserId(),
                submission.getGroupId(),
                submission.getDate()
        );
        currentSubmission.setFlags(submission.getFlags());
        return repository.save(currentSubmission);
    }

    public Submission saveGrader(Submission submission) {
        Submission currentSubmission = repository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
                submission.getProject(),
                submission.getUserId(),
                submission.getGroupId(),
                submission.getDate()
        );
        currentSubmission.setGrader(submission.getGrader());
        return repository.save(currentSubmission);
    }

    public List<Submission> findSubmissionWithProject(Project project) {
        return repository.findSubmissionsByProject(project);
    }

    public Submission findSubmissionById(String id) {
//        return repository.findById(new SubmissionId(user_id, project.getProjectCompositeKey())).orElse(null);
        return repository.getOne(UUID.fromString(id));
    }

    public List<Submission> findSubmissionsForGrader(Project project, String graderId) {
        return repository.findSubmissionsByProjectAndGrader_UserId(project, graderId);
    }

    public List<Submission> findSubmissionsByFlags(Flag flag) {
        return repository.findSubmissionsByFlags(flag);
    }

    public List<Submission> findSubmissionsForGraderAll(String graderId) {
        return repository.findSubmissionsByGrader_UserId(graderId);
    }
}