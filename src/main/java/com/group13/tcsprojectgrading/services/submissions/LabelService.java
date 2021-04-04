//package com.group13.tcsprojectgrading.services.submissions;
//
//import com.group13.tcsprojectgrading.models.submissions.Label;
//import com.group13.tcsprojectgrading.models.project.Project;
//import com.group13.tcsprojectgrading.repositories.submissions.LabelRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.List;
//
//@Service
//public class LabelService {
//
//    private final LabelRepository repository;
//
//    @Autowired
//    public LabelService(LabelRepository repository) {
//        this.repository = repository;
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public Label saveNewLabel(Label label) {
//        Label currentLabel = findLabelWithNameAndProject(label.getName(), label.getProject());
//        if (currentLabel != null) {
//            label.setId(currentLabel.getId());
//        }
//        return this.repository.save(label);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public Label findLabelWithNameAndProject(String name, Project project) {
//        return repository.findByNameAndProject(name, project);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public Label findLabelWithId(Long id) {
//        return repository.findById(id).orElse(null);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Label> findLabelsWithProject(Project project) {
//        return repository.findByProject(project);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public void deleteLabel(Label label) {
//        repository.delete(label);
//    }
//}
