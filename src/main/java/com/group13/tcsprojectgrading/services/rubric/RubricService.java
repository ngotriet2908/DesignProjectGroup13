package com.group13.tcsprojectgrading.services.rubric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonPatch;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.rubric.RubricHistoryLinker;
import com.group13.tcsprojectgrading.models.rubric.RubricLinker;
import com.group13.tcsprojectgrading.models.rubric.*;
import com.group13.tcsprojectgrading.repositories.grading.GradeRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricHistoryLinkerRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricLinkerRepository;
import com.group13.tcsprojectgrading.services.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

@Service
public class RubricService {
    private final RubricLinkerRepository rubricLinkerRepository;
    private final RubricHistoryLinkerRepository rubricHistoryLinkerRepository;
    private final GradeRepository gradeRepository;

    @Autowired
    public RubricService(RubricLinkerRepository repository, RubricHistoryLinkerRepository rubricHistoryLinkerRepository, GradeRepository gradeRepository) {
        this.rubricLinkerRepository = repository;
        this.rubricHistoryLinkerRepository = rubricHistoryLinkerRepository;
        this.gradeRepository = gradeRepository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric getRubricById(Long id) {
        return getRubricFromLinker(rubricLinkerRepository.findById(new RubricLinker.Pk(new Project(id))).orElse(null));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric getRubricAndLock(Long id) {
        return getRubricFromLinker(rubricLinkerRepository.findRubricLinkerById(new RubricLinker.Pk(new Project(id))).orElse(null));
    }

    private Rubric getRubricFromLinker(RubricLinker linker) {
        try {
            // TODO, get rid of object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            if (linker != null) {
//                System.out.println("Rubric version: " + linker.getVersion());
                Rubric rubric = objectMapper.readValue(linker.getRubric(), Rubric.class);
                rubric.setVersion(linker.getVersion());
                return rubric;
            }
        } catch (JsonProcessingException e) {
            return null;
        }
        return null;
    }

    private RubricHistory getRubricHistoryFromLinker(RubricHistoryLinker linker) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (linker != null) {
                return objectMapper.readValue(linker.getRubricHistory(), RubricHistory.class);
            }
        } catch (JsonProcessingException e) {
            return null;
        }
        return null;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric saveRubric(Rubric rubric) throws JsonProcessingException {
        updateCriterionCount(rubric);
        updateLastModified(rubric);
        RubricLinker linker = this.rubricLinkerRepository.findById(new RubricLinker.Pk(new Project(rubric.getId()))).orElse(null);
        if (linker == null) {
            return null;
        }

        // TODO, get rid of object mapper
//        linker.setRubric(Json.getObjectWriter().writeValueAsString(rubric));
//        System.out.println("Rubric version: " + linker.getVersion());
        ObjectMapper mapper = new ObjectMapper();
        linker.setRubric(mapper.writeValueAsString(rubric));

        return getRubricFromLinker(this.rubricLinkerRepository.save(linker));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric addNewRubric(Rubric rubric) throws JsonProcessingException {
        updateCriterionCount(rubric);
        updateLastModified(rubric);
        RubricLinker linker = this.rubricLinkerRepository.findById(new RubricLinker.Pk(new Project(rubric.getId()))).orElse(null);

        if (linker != null) {
            return null;
        }

        linker = new RubricLinker(rubric.getId(), Json.getObjectWriter().writeValueAsString(rubric));
        return getRubricFromLinker(this.rubricLinkerRepository.save(linker));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric addNewRubric(Long projectId) throws JsonProcessingException {
        return this.addNewRubric(new Rubric(projectId));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public String importRubric(Rubric rubric) throws JsonProcessingException {
        RubricLinker linker = rubricLinkerRepository.findRubricLinkerById(new RubricLinker.Pk(new Project(rubric.getId()))).orElse(null);
        if (linker == null) return null;

        // TODO, get rid of object mapper
//        linker.setRubric(Json.getObjectWriter().writeValueAsString(rubric));
//        if (linker.getVersion() > 0) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "cannot import after v0"
//            );
//        }

        ObjectMapper mapper = new ObjectMapper();
        linker.setRubric(mapper.writeValueAsString(rubric));
        return rubricLinkerRepository.save(linker).getRubric();
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void updateCriterionCount(Rubric rubric) {
        int total = 0;
        if (rubric.getChildren() != null) {
            for (Element child : rubric.getChildren()) {
                total += child.countCriteria();
            }
        }

        rubric.setCriterionCount(total);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void updateLastModified(Rubric rubric) {
        rubric.setLastModified(new Date());
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Rubric> getAllRubrics() {
        List<Rubric> rubrics = new ArrayList<>();
        List<RubricLinker> linkers = rubricLinkerRepository.findAll();
        for(RubricLinker linker: linkers) {
            Rubric rubric = getRubricFromLinker(linker);
            if (rubric != null) {
                rubrics.add(rubric);
            }
        }
        return rubrics;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteRubric(Long projectId) {
        rubricLinkerRepository.deleteById(new RubricLinker.Pk(new Project(projectId)));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void storeHistory(RubricHistory history) throws JsonProcessingException {

        RubricHistoryLinker linker = rubricHistoryLinkerRepository.findById(history.getId()).orElse(null);
        if (linker == null) {
            linker = new RubricHistoryLinker(history.getId());
        };

        linker.setRubricHistory(Json.getObjectWriter().writeValueAsString(history));
        rubricHistoryLinkerRepository.save(linker);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public RubricHistory getHistory(Long projectId) {
        RubricHistoryLinker linker = rubricHistoryLinkerRepository.findById(projectId).orElse(null);
        return getRubricHistoryFromLinker(linker);
    }

    // TODO: mark all? mark specific? put in issues? notify? if mark all - stop when first issue below is found
    // TODO: maybe add exceptions if the path is not in the rubric
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric applyUpdate(JsonNode patches, Rubric rubric) throws JsonProcessingException, ResponseStatusException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rubricJson = objectMapper.convertValue(rubric, JsonNode.class);
        System.out.println(rubricJson);

        if (patches.isArray()) {
            ArrayNode arrayNode = (ArrayNode) patches;

            for(int i = 0; i < arrayNode.size(); i++) {
                JsonNode update = arrayNode.get(i);
                System.out.println(update);

                String operation = update.get("op").asText();

                if (operation.equals(Operation.ADD.toString())) {
                    // either criterion or section
                    String type = update.get("value").get("content").get("type").asText();

                    // mark all submissions if a new criterion is added
                    // i.e. add 'issue' to those submissions
                    if (type.equals(Type.CRITERION.toString())) {
                        System.out.println("A new criterion has been added. Mark all submissions.");
                    } else {
                        System.out.println("A new section has been created. Well, skip.");
                    }
                } else if (operation.equals(Operation.REPLACE.toString())) {
                    // get path elements
                    String[] path = update.get("path").asText().split("/");

                    // if title was changed, skip
                    if (path.length > 0 && path[path.length - 1].equals("title")) {
                        // mark all submissions that are affected by this change
                        System.out.println("We don't really care about changes in name, do we? Skip.");
                    } else if (path.length > 0 && path[path.length - 1].equals("grade")) {
                        // mark all submissions that are affected by this change
                        System.out.println("Grading for a criterion has been changed. Mark all submissions.");
                    } else if (path.length > 0 && path[path.length - 1].equals("text")) {
                        // mark all submissions that are affected by this change
                        System.out.println("Description of a criterion has been changed. Mark all submissions.");
                    } else {
                        // the whole rubric was purged
                        System.out.println("Rubric has been cleared. Mark all submissions.");
                    }
                } else if (operation.equals(Operation.REMOVE.toString())) {
                    // get path elements, last part should be an index in the children array
                    String[] path = update.get("path").asText().split("/");

                    JsonNode element = findInRubric(rubricJson, path);

                    if (element.get("content").get("type").asText().equals(Type.SECTION.toString())) {
                        // the code below is very inefficient

                        // recursively get all criteria
                        List<JsonNode> criteria = this.findAllCriteria(element);

                        if (criteria.isEmpty()) {
                            // no criterion was removed, so skip
                            continue;
                        }

                        System.out.println("A section with criteria has been removed. Mark all submissions.");

                        // for each criterion
                        try {
                            for (JsonNode criterion: criteria) {
                                System.out.println(criterion);
                                // for each assessment in the project check if the criterion is in it
                                System.out.println("removing grades of criterion: " + criterion.get("content").get("id").asText());
                                gradeRepository.deleteAllByCriterionId(criterion.get("content").get("id").asText());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "deletion failed");
                        }
                    } else {
                        // it was a criterion
                        try {
                            gradeRepository.deleteAllByCriterionId(element.get("content").get("id").asText());
                            System.out.println("removing grades of criterion: " + element.get("content").get("id").asText());
                            System.out.println("A criterion has been removed. Mark all submissions.");
                        } catch (Exception e) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "deletion failed");
                        }
                    }
                }

                // apply patch
                ArrayNode updateArray = objectMapper.createArrayNode();
                updateArray.add(update);
                JsonPatch.applyInPlace(updateArray, rubricJson);
            }
        }

        return objectMapper.treeToValue(rubricJson, Rubric.class);
    }


    public JsonNode findInRubric(JsonNode rubric, String[] path) {
        JsonNode currentArray = null;
        JsonNode currentElement = rubric;

        for (int i = 1; i < path.length; i++) {
            System.out.println(path[i]);
            if (path[i].equals("children")) {
                // children
                currentArray = currentElement.get("children");
            } else {
                // index
                currentElement = currentArray.get(Integer.parseInt(path[i]));
            }
        }

        return currentElement;
    }

    public List<Element> findAllCriteria(Element element) {
        List<Element> criteria = new ArrayList<>();
        Stack<Element> fringe = new Stack<>();
        fringe.push(element);

        while (!fringe.isEmpty()) {
            Element currentElement = fringe.pop();

            if (currentElement.getContent().getType().equals(Type.SECTION.toString())) {
                for (Element e: currentElement.getChildren()) {
                    fringe.push(e);
                }
            } else {
                criteria.add(currentElement);
            }
        }

        return criteria;
    }

    public List<JsonNode> findAllCriteria(JsonNode element) {
        List<JsonNode> criteria = new ArrayList<>();
        Stack<JsonNode> fringe = new Stack<>();
        fringe.push(element);

        while (!fringe.isEmpty()) {
            JsonNode currentElement = fringe.pop();
            if (currentElement.get("content").get("type").asText().equals(Type.SECTION.toString())) {
                for (JsonNode e: currentElement.get("children")) {
                    fringe.push(e);
                }
            } else {
                criteria.add(currentElement);
            }
        }

        return criteria;
    }

    public enum Operation {
        REPLACE("replace"),
        ADD("add"),
        REMOVE("remove");

        private final String operation;

        Operation(final String operation) {
            this.operation = operation;
        }

        @Override
        public String toString() {
            return this.operation;
        }
    }

    public enum Type {
        CRITERION("1"),
        SECTION("0");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }
    }
}
