package com.zeed.isms.lib.apimodel;

import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.models.Course;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CourseApiModel {

    @NotNull
    private ResponseCode responseCode;

    private Course course;

    private List<Course> courses;

    private String narration;

    public CourseApiModel(@NotNull ResponseCode responseCode, Course course, List<Course> courses, String narration) {
        this.responseCode = responseCode;
        this.course = course;
        this.courses = courses;
        this.narration = narration;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
    
}
