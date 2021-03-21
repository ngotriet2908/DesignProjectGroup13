package com.group13.tcsprojectgrading.models.rubric;

import com.fasterxml.jackson.databind.JsonNode;

public class RubricPatch {
    // operation
    private String op;
    // path to updated part
    private String path;
    // updated value
    private JsonNode value;

    public RubricPatch(String op, String path, JsonNode value) {
        this.op = op;
        this.path = path;
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RubricUpdate{" +
                "op='" + op + '\'' +
                ", path='" + path + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
