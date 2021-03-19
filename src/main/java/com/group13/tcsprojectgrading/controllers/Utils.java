package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.models.rubric.Rubric;

import javax.swing.text.Document;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static ArrayNode groupPages(ObjectMapper objectMapper, List<String> responseString) throws JsonProcessingException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(String nodeListString: responseString) {
            JsonNode jsonNode = objectMapper.readTree(nodeListString);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                arrayNode.add(node);
            }
        }
        return arrayNode;
    }


}
