package com.group13.tcsprojectgrading.controllers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.Json;

import javax.swing.text.Document;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static ArrayNode groupPages(List<String> responseString) throws JsonProcessingException {
        ArrayNode arrayNode = Json.createArrayNode();
        final ObjectReader reader = Json.getObjectReader();

        for(String nodeListString: responseString) {
            JsonNode jsonNode = reader.readTree(nodeListString);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                arrayNode.add(node);
            }
        }
        return arrayNode;
    }
}
