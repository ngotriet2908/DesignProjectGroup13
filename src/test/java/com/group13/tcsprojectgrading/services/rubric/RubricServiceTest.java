//package com.group13.tcsprojectgrading.services.rubric;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.group13.tcsprojectgrading.models.rubric.Element;
//import com.group13.tcsprojectgrading.models.rubric.Rubric;
//import com.group13.tcsprojectgrading.models.rubric.RubricContent;
//import com.group13.tcsprojectgrading.models.rubric.RubricGrade;
//import com.group13.tcsprojectgrading.repositories.rubric.RubricHistoryRepository;
//import com.group13.tcsprojectgrading.repositories.rubric.RubricRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//public class RubricServiceTest {
//    @Mock
//    private RubricRepository repository;
//
//    @Mock
//    private RubricHistoryRepository historyRepository;
//
//    @InjectMocks
//    private RubricService rubricService;
//
//    String rubricId;
//    Rubric rubric;
//
//    @BeforeEach
//    void init() {
//        rubricId = "111";
//        rubric = new Rubric(rubricId);
//    }
//
//    @Test
//    void criterionCountUpdated() {
//        rubricService.updateCriterionCount(rubric);
//        assertThat(rubric.getCriterionCount()).isZero();
//
//        Element criterion1 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
//        Element criterion2 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
//
//        rubric.setChildren(Arrays.asList(criterion1, criterion2));
//        rubricService.updateCriterionCount(rubric);
//        assertThat(rubric.getCriterionCount()).isEqualTo(2);
//
//        rubric.setChildren(Collections.singletonList(criterion1));
//        rubricService.updateCriterionCount(rubric);
//        assertThat(rubric.getCriterionCount()).isEqualTo(1);
//    }
//
//    @Test
//    void findChildInRubric() {
//        Element child1 = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
//        Element grandChild = new Element(new RubricContent("test", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade()));
//        Element child2 = new Element(new RubricContent("test", RubricContent.BLOCK_TYPE, "test"), Collections.singletonList(grandChild));
//        rubric.setChildren(Arrays.asList(child1, child2));
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rubricJson = objectMapper.convertValue(rubric, JsonNode.class);
//
//        String[] path = new String[] {"rubric", "children", "0"};
//        assertThat(rubricService.findInRubric(rubricJson, path)).isEqualTo(objectMapper.convertValue(child1, JsonNode.class));
//
//        path = new String[] {"rubric", "children", "1", "children", "0"};
//        assertThat(rubricService.findInRubric(rubricJson, path)).isEqualTo(objectMapper.convertValue(grandChild, JsonNode.class));
//    }
//
//    //TODO tests for applyUpdate function
//}
