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
import java.util.*;

/**
 * Service handlers operations relating to rubric
 */
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

    /**
     * get a rubric with project id
     * @param id rubric id
     * @return a rubric entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric getRubricById(Long id) {
        return getRubricFromLinker(rubricLinkerRepository.findById(new RubricLinker.Pk(new Project(id))).orElse(null));
    }

    /**
     * obtain a lock on a rubric with project id
     * @param id rubric id
     * @return a rubric entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric getRubricAndLock(Long id) {
        return getRubricFromLinker(rubricLinkerRepository.findRubricLinkerById(new RubricLinker.Pk(new Project(id))).orElse(null));
    }

    /**
     * extract rubric from rubric linker
     * @param linker rubric linker entity
     * @return a rubric entity
     */
    private Rubric getRubricFromLinker(RubricLinker linker) {
        try {
            // TODO, get rid of object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            if (linker != null) {
                System.out.println("Rubric version: " + linker.getVersion());
                Rubric rubric = objectMapper.readValue(linker.getRubric(), Rubric.class);
                rubric.setVersion(linker.getVersion());
                return rubric;
            }
        } catch (JsonProcessingException e) {
            return null;
        }
        return null;
    }

    /**
     * extract rubric history from rubric history linker
     * @param linker rubric history linker entity
     * @return a rubric history entity
     */
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

    /**
     * update rubric
     * @param rubric rubric entity
     * @return updated rubric entity
     * @throws JsonProcessingException json parsing exception
     */
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
        System.out.println("Rubric version: " + linker.getVersion());
        ObjectMapper mapper = new ObjectMapper();
        linker.setRubric(mapper.writeValueAsString(rubric));

        return getRubricFromLinker(this.rubricLinkerRepository.save(linker));
    }

    /**
     * add new rubric if not exist
     * @param rubric rubric entity
     * @return created rubric entity
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     * add new empty rubric
     * @param projectId canvas project id
     * @return updated rubric entity
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric addNewRubric(Long projectId) throws JsonProcessingException {
        return this.addNewRubric(new Rubric(projectId));
    }

    /**
     * import rubric to database
     * @param rubric rubric entity
     * @return updated rubric
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public String importRubric(Rubric rubric) throws JsonProcessingException {
        RubricLinker linker = rubricLinkerRepository.findRubricLinkerById(new RubricLinker.Pk(new Project(rubric.getId()))).orElse(null);
        if (linker == null) return null;

        //randomise ID of elements
        Stack<Element> stack = new Stack<>();
        stack.addAll(rubric.getChildren());
        while(stack.size() > 0) {
            Element element = stack.pop();
            if (element.content.type.equals(RubricContent.BLOCK_TYPE)) {
                stack.addAll(element.children);
            }
            element.getContent().setId(UUID.randomUUID().toString());
        }
        linker.setRubric(Json.getObjectWriter().writeValueAsString(rubric));
        return rubricLinkerRepository.save(linker).getRubric();
    }

    /**
     * update criterion count in a rubric
     * @param rubric rubric entity
     */
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

    /**
     * update last modified in a rubric
     * @param rubric rubric entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void updateLastModified(Rubric rubric) {
        rubric.setLastModified(new Date());
    }

    /**
     * get all rubrics
     * @return list of rubrics
     */
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

    /**
     * remove rubric
     * @param projectId canvas project id
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteRubric(Long projectId) {
        rubricLinkerRepository.deleteById(new RubricLinker.Pk(new Project(projectId)));
    }

    /**
     * store rubric history
     * @param history rubric history entity
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void storeHistory(RubricHistory history) throws JsonProcessingException {

        RubricHistoryLinker linker = rubricHistoryLinkerRepository.findById(history.getId()).orElse(null);
        if (linker == null) {
            linker = new RubricHistoryLinker(history.getId());
        };

        linker.setRubricHistory(Json.getObjectWriter().writeValueAsString(history));
        rubricHistoryLinkerRepository.save(linker);
    }

    /**
     * get rubric history in project
     * @param projectId canvas project id
     * @return rubric history entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public RubricHistory getHistory(Long projectId) {
        RubricHistoryLinker linker = rubricHistoryLinkerRepository.findById(projectId).orElse(null);
        return getRubricHistoryFromLinker(linker);
    }

    // TODO: mark all? mark specific? put in issues? notify? if mark all - stop when first issue below is found

    /**
     * Apply patch to rubric and update
     * @param patches update patches
     * @param rubric rubric entity
     * @return updated rubric
     * @throws JsonProcessingException json parsing exception
     * @throws ResponseStatusException response exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric applyUpdate(JsonNode patches, Rubric rubric) throws JsonProcessingException, ResponseStatusException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rubricJson = objectMapper.convertValue(rubric, JsonNode.class);

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
                                // for each assessment in the project check if the criterion is in it
                                System.out.println("removing grades of criterion: " + criterion.get("content").get("id").asText());
                                gradeRepository.deleteAllByCriterionId(criterion.get("content").get("id").asText());
                            }
                        } catch (Exception e) {
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


    /**
     * find element in rubric using path
     * @param rubric rubric entity
     * @param path path string
     * @return rubric element
     */
    public JsonNode findInRubric(JsonNode rubric, String[] path) {
        JsonNode currentArray = null;
        JsonNode currentElement = rubric;

        for (int i = 1; i < path.length; i++) {
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

    /**
     * find all criteria in an element
     * @param element rubric element
     * @return list of rubric element
     */
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

    private enum Operation {
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

    private enum Type {
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
