package com.group13.tcsprojectgrading.services;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Json {
    private static final ObjectMapper mapper;

    static {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        mapper = new ObjectMapper();
//        mapper.setDateFormat(df);
    }

    public static <T> T toObject(final Class<T> type, final String json) throws IOException {
        return mapper.readerFor(type).readValue(json);
    }

    public static <T> String fromObject(final Class<T> type, Object writeToJson) throws IOException {
        return mapper.writerFor(type).with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(writeToJson);
    }

    public static <T> ObjectWriter getObjectWriter(final Class<T> type) {
        return mapper.writerFor(type).with(SerializationFeature.INDENT_OUTPUT);
    }

    public static ObjectWriter getObjectWriter() {
        return mapper.writer().with(SerializationFeature.INDENT_OUTPUT);
    }

    public static ObjectReader getObjectReader() {
        return mapper.reader();
    }

    public static JsonNode createNode() {
        return mapper.createObjectNode();
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }
}
