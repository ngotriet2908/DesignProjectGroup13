package com.group13.tcsprojectgrading.services.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.AssessmentContainerRepository;
import com.group13.tcsprojectgrading.services.AssessmentLinkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

//    private final AssessmentRepository repository;
    private final AssessmentLinkerService service;
    private final AssessmentContainerRepository containerRepository;

    @Autowired
    public AssessmentService(AssessmentLinkerService service, AssessmentContainerRepository containerRepository) {
//        this.repository = repository;
        this.service = service;
        this.containerRepository = containerRepository;
    }

    private Assessment getAssessmentFromContainer(AssessmentContainer container) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (container != null) {
                return objectMapper.readValue(container.getAssessment(), Assessment.class);
            }
        } catch (JsonProcessingException e) {
            return null;
        }
        return null;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment getAssessmentBySubmissionAndParticipant(Submission submission, Participant participant) {
        List<AssessmentLinker> linkers = service.findAssessmentLinkerForSubmissionAndParticipant(submission, participant);
        if (linkers.size() != 1) {
            System.out.println("linker size:" + linkers.size());
            return null;
        }

        return getAssessmentFromContainer(containerRepository.findById(linkers.get(0).getAssessmentId()).orElse(null));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Assessment> getAssessmentBySubmission(Submission submission) {
        List<AssessmentLinker> linkers = service.findAssessmentLinkersForSubmission(submission);
        List<Assessment> assessments = new ArrayList<>();
        for(AssessmentLinker linker: linkers) {
            Assessment assessment = getAssessmentFromContainer(containerRepository.findById(linker.getAssessmentId()).orElse(null));
            if (assessment == null) continue;
            if (!assessments.contains(assessment)) {
                assessments.add(assessment);
            }
        }

        return assessments;
    }

    private String convertAssessmentToString(Assessment assessment) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(assessment);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment getAssessmentById(String id) {
        return getAssessmentFromContainer(containerRepository.findById(UUID.fromString(id)).orElse(null));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment saveAssessment(Assessment assessment) {
        try {
            AssessmentContainer container = containerRepository.findById(assessment.getId()).orElse(null);
            if (container == null) return null;
            container.setAssessment(convertAssessmentToString(assessment));
            return getAssessmentFromContainer(containerRepository.save(container));
        } catch (Exception e) {
            return null;
        }

    }


    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment saveAssessment(AssessmentLinker assessmentLinker) {
        try {
            Assessment assessment = new Assessment(assessmentLinker.getAssessmentId());
            AssessmentContainer container = new AssessmentContainer(assessment.getId(), convertAssessmentToString(assessment));
            return getAssessmentFromContainer(containerRepository.save(container));
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment addNewAssignment(Assessment assessment) {
        try {
            AssessmentContainer container = new AssessmentContainer(assessment.getId(), convertAssessmentToString(assessment));
            return getAssessmentFromContainer(containerRepository.save(container));
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAssessment(AssessmentLinker assessmentLinker) {
        containerRepository.deleteById(assessmentLinker.getAssessmentId());
//        repository.delete(findAssessment(assessmentLinker));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAssessment(Assessment assessment) {
        containerRepository.deleteById(assessment.getId());
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment findAssessment(String id) {
        AssessmentContainer container = containerRepository.findById(UUID.fromString(id)).orElse(null);
        if (container == null) return null;
        return getAssessmentFromContainer(container);

    }


    public int calculateFinalGrade(Rubric rubric, Assessment assessment) {
        List<Element> criteria = rubric.fetchAllCriteria();
        int total = 0;

        for (Element criterion: criteria) {
            CriterionGrade grades = assessment.getGrades().get(criterion.getContent().getId());
            Grade grade = grades.getHistory().get(grades.getActive());
            total += grade.getGrade() * criterion.getContent().getGrade().getWeight();
        }

        return total;
    }

//    public double computeProgress(Assessment assessment) {
//        Rubric rubric = getRubric(assessment);
//        int graded = getGradedCount(assessment);
//        int total = rubric.getCriterionCount();
//        return graded/(double) total;
//    }
}