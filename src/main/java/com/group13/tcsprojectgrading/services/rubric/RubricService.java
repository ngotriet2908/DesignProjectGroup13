package com.group13.tcsprojectgrading.services.rubric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.zjsonpatch.JsonPatch;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.group13.tcsprojectgrading.models.RubricHistoryLinker;
import com.group13.tcsprojectgrading.models.RubricLinker;
import com.group13.tcsprojectgrading.models.rubric.*;
import com.group13.tcsprojectgrading.repositories.RubricHistoryLinkerRepository;
import com.group13.tcsprojectgrading.repositories.RubricLinkerRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.model.util.Elements;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RubricService {
    private final RubricLinkerRepository rubricLinkerRepository;
    private final RubricHistoryLinkerRepository rubricHistoryLinkerRepository;

    @Autowired
    public RubricService(RubricLinkerRepository repository, RubricHistoryLinkerRepository rubricHistoryLinkerRepository) {
        this.rubricLinkerRepository = repository;
        this.rubricHistoryLinkerRepository = rubricHistoryLinkerRepository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric getRubricById(String id) {
        return getRubricFromLinker(rubricLinkerRepository.findById(id).orElse(null));
    }

    private Rubric getRubricFromLinker(RubricLinker linker) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (linker != null) {
                return objectMapper.readValue(linker.getRubric(), Rubric.class);
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
        RubricLinker linker = rubricLinkerRepository.findById(rubric.getId()).orElse(null);
        if (linker == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        linker.setRubric(mapper.writeValueAsString(rubric));

        return getRubricFromLinker(rubricLinkerRepository.save(linker));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Rubric addNewRubric(Rubric rubric) throws JsonProcessingException {
        updateCriterionCount(rubric);
        updateLastModified(rubric);
        RubricLinker linker = rubricLinkerRepository.findById(rubric.getId()).orElse(null);
        if (linker != null) return null;
        ObjectMapper mapper = new ObjectMapper();
        linker = new RubricLinker(rubric.getId(), mapper.writeValueAsString(rubric));
        return getRubricFromLinker(rubricLinkerRepository.save(linker));
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
    public void deleteRubric(String projectId) {
        rubricLinkerRepository.deleteById(projectId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void storeHistory(RubricHistory history) throws JsonProcessingException {

        RubricHistoryLinker linker = rubricHistoryLinkerRepository.findById(history.getId()).orElse(null);
        if (linker == null) {
//            throw new NullPointerException("null linker")
            linker = new RubricHistoryLinker(history.getId());
        };
        ObjectMapper mapper = new ObjectMapper();
        linker.setRubricHistory(mapper.writeValueAsString(history));
        rubricHistoryLinkerRepository.save(linker);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public RubricHistory getHistory(String projectId) {
        RubricHistoryLinker linker = rubricHistoryLinkerRepository.findById(projectId).orElse(null);
        return getRubricHistoryFromLinker(linker);
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

                        // recursively get all criteria



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
