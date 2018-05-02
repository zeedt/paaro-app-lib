package com.zeed.isms.lib.apimodel;

import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.models.Teacher;

import javax.validation.constraints.NotNull;
import java.util.List;

public class TeacherApiModel {

    @NotNull
    private ResponseCode responseCode;

    private Teacher teacher;

    private List<Teacher> teachers;

    private String narration;

    public TeacherApiModel(@NotNull ResponseCode responseCode, Teacher teacher, List<Teacher> teachers, String narration) {
        this.responseCode = responseCode;
        this.teacher = teacher;
        this.teachers = teachers;
        this.narration = narration;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

}
