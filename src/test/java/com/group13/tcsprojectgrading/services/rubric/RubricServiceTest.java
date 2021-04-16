package com.group13.tcsprojectgrading.services.rubric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.group13.tcsprojectgrading.models.rubric.RubricGrade;
import com.group13.tcsprojectgrading.repositories.grading.GradeRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricHistoryLinkerRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricLinkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.Operation;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(MockitoExtension.class)
public class RubricServiceTest {
    @Mock
    private RubricLinkerRepository repository;
    @Mock
    private RubricHistoryLinkerRepository historyRepository;
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private RubricService rubricService;

    Long rubricId;
    Rubric rubric;
    ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        rubricId = 111L;
        rubric = new Rubric(rubricId);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void criterionCountUpdated() {
        rubricService.updateCriterionCount(rubric);
        assertThat(rubric.getCriterionCount()).isZero();

        Element criterion1 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
        Element criterion2 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));

        rubric.setChildren(Arrays.asList(criterion1, criterion2));
        rubricService.updateCriterionCount(rubric);
        assertThat(rubric.getCriterionCount()).isEqualTo(2);

        rubric.setChildren(Collections.singletonList(criterion1));
        rubricService.updateCriterionCount(rubric);
        assertThat(rubric.getCriterionCount()).isEqualTo(1);
    }

    @Test
    public void findChildInRubric() {
        Element child1 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
        Element grandChild = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
        Element child2 = new Element(new RubricContent("test", RubricContent.BLOCK_TYPE, "test"), Collections.singletonList(grandChild));
        rubric.setChildren(Arrays.asList(child1, child2));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rubricJson = objectMapper.convertValue(rubric, JsonNode.class);

        String[] path = new String[] {"rubric", "children", "0"};
        assertThat(rubricService.findInRubric(rubricJson, path)).isEqualTo(objectMapper.convertValue(child1, JsonNode.class));

        path = new String[] {"rubric", "children", "1", "children", "0"};
        assertThat(rubricService.findInRubric(rubricJson, path)).isEqualTo(objectMapper.convertValue(grandChild, JsonNode.class));
    }

    @Test
    public void applyUpdateOk() throws JsonProcessingException {
        //Doesn't cover all possible patches so could be expanded
        Element child1 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
        Element grandChild = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
        Element child2 = new Element(new RubricContent("test", RubricContent.BLOCK_TYPE, "test"), Collections.singletonList(grandChild));
        rubric.setChildren(Arrays.asList(child1, child2));

        ArrayNode patches = objectMapper.createArrayNode();

        ObjectNode addCriterion = objectMapper.createObjectNode();
        ObjectNode valueNodeCriterion = objectMapper.createObjectNode();
        ObjectNode contentNodeCriterion = objectMapper.createObjectNode();
        contentNodeCriterion.put("type", RubricService.Type.CRITERION.toString());
        contentNodeCriterion.put("id", "added");
        valueNodeCriterion.set("content", contentNodeCriterion);
        addCriterion.put("op", RubricService.Operation.ADD.toString());
        addCriterion.put("path","/children/1/children/0");
        addCriterion.set("value", valueNodeCriterion);

        ObjectNode replaceCriterion = objectMapper.createObjectNode();
        replaceCriterion.put("op", RubricService.Operation.REPLACE.toString());
        replaceCriterion.put("path","/children/0/content/grade");
        replaceCriterion.set("value", null);

//        ObjectNode removeNonExistentCriterion = objectMapper.createObjectNode();
//        removeNonExistentCriterion.put("op", RubricService.Operation.REMOVE.toString());
//        removeNonExistentCriterion.put("path", "/children/2");
//        removeNonExistentCriterion.set("value", valueNodeCriterion);

        ObjectNode removeExistentCriterion = objectMapper.createObjectNode();
        removeExistentCriterion.put("op", RubricService.Operation.REMOVE.toString());
        removeExistentCriterion.put("path", "/children/0");


        //TODO the index of the children changes once something is removed, this could cause problems
        ObjectNode valueNodeSection = objectMapper.createObjectNode();
        ObjectNode contentNodeSection = objectMapper.createObjectNode();
        ObjectNode removeExistentSection = objectMapper.createObjectNode();
        contentNodeSection.put("type", RubricService.Type.SECTION.toString());
        valueNodeSection.set("content", contentNodeSection);
        removeExistentSection.put("op", RubricService.Operation.REMOVE.toString());
        removeExistentSection.put("path", "/children/0");

        patches.add(addCriterion);
        patches.add(replaceCriterion);
//        patches.add(removeNonExistentCriterion);
        patches.add(removeExistentCriterion);
        patches.add(removeExistentSection);

        assertThatNoException().isThrownBy(() -> rubricService.applyUpdate(patches, rubric));

    }
}
