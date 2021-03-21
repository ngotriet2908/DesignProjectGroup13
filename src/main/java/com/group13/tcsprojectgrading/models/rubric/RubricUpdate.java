package com.group13.tcsprojectgrading.models.rubric;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RubricUpdate {
    // time in UTC (not in the current timezone)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date timestamp;

    private String update;

    public RubricUpdate(Date timestamp, String updates) {
        this.timestamp = timestamp;
        this.update = updates;
    }

    public RubricUpdate(String updates) {
        this.timestamp = new Date();
        this.update = updates;
    }

    public RubricUpdate(JsonNode update) {
        this.timestamp = new Date();
//        this.updates = new HashMap<>();
//        this.updates.put("update", update.toString());
        this.update = update.toString();
    }

//    public RubricUpdate(Map<String, Object> updates) {
//        this.timestamp = new Date();
//        this.updates = updates;
//    }
//
//    public RubricUpdate(JsonPatch updates) {
//        this.timestamp = new Date();
//        this.updates = new HashMap<>();
//        this.updates.put("updates", updates);
//    }
//
//    public RubricUpdate(Date timestamp, Map<String, Object> updates) {
//        this.timestamp = timestamp;
//        this.updates = updates;
//    }

    public RubricUpdate() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

//    public Map<String, Object> getUpdates() {
//        return updates;
//    }
//
//    public void setUpdates(Map<String, Object> updates) {
//        this.updates = updates;
//    }


    public String getUpdates() {
        return update;
    }

    public void setUpdates(String updates) {
        this.update = updates;
    }
}
