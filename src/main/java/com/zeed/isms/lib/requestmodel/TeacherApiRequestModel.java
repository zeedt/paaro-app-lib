package com.zeed.isms.lib.requestmodel;

import com.zeed.isms.lib.models.Teacher;

import java.util.Set;

public class TeacherApiRequestModel {

    private Teacher teacher;

    private Set<String> courseCodes;

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Set<String> getCourseCodes() {
        return courseCodes;
    }

    public void setCourseCodes(Set<String> courseCodes) {
        this.courseCodes = courseCodes;
    }
}
