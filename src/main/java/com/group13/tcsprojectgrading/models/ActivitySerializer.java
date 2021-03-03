package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ActivitySerializer extends JsonSerializer<Activity> {

    @Override
    public void serialize(Activity value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getProjectId());
        gen.writeStringField("course_id", value.getCourseId());
        gen.writeStringField("user_id", value.getUserId());
        gen.writeStringField("last_opened", value.getTimestamp().toString());
        gen.writeStringField("name", value.getProjectName());
        gen.writeStringField("created_at", value.getProjectCreatedAt().toString());
        gen.writeEndObject();
    }
}
