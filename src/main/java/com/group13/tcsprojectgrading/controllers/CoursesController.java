package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
class CoursesController {
    private final CanvasApi canvasApi;

    @Autowired
    public CoursesController(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<String> courses() {
        List<String> response = this.canvasApi.getCanvasCoursesApi().getUserCourseList();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // TODO: the following line sends back only the first batch of the list of courses
            return new ResponseEntity<>(response.get(0), HttpStatus.OK);
        }
    }
}
