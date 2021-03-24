package com.group13.tcsprojectgrading.services.rubric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.group13.tcsprojectgrading.models.rubric.*;
import com.group13.tcsprojectgrading.repositories.rubric.RubricHistoryRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.model.util.Elements;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class RubricService {
    private final RubricRepository repository;
    private final RubricHistoryRepository historyRepository;

    @Autowired
    public RubricService(RubricRepository repository, RubricHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric getRubricById(String id) {
        return repository.getById(id);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric saveRubric(Rubric rubric) {
        updateCriterionCount(rubric);
        updateLastModified(rubric);
        return repository.save(rubric);
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
        return repository.findAll();
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteRubric(String projectId) {
        repository.deleteById(projectId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void storeHistory(RubricHistory history) {
        this.historyRepository.save(history);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public RubricHistory getHistory(String projectId) {
        return this.historyRepository.getById(projectId);
    }

    // TODO: mark all? mark specific? put in issues? notify? if mark all - stop when first issue below is found
    public Rubric applyUpdate(JsonNode patches, Rubric rubric) throws JsonProcessingException {
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
                        // it was a title with non-empty subtree
                        // TODO it might have been a hierarchy of headers, too tired to check
                        System.out.println(element.get("children").size());
                        if (element.get("children").size() > 0) {
                            System.out.println("A section with criteria has been removed (well, not necessarily, but hey). Mark all submissions.");
                        }
                    } else {
                        // it was a criterion
                        System.out.println("A criterion has been removed. Mark all submissions.");
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
