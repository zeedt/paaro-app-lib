package com.zeed.isms.lib.requestmodel;

import com.zeed.isms.lib.models.Course;

import java.util.List;
import java.util.Set;

public class CourseApiRequestModel {

    private Course course;

    private Set<String> staffCodes;

    private String coordinatorCode;

    private boolean updateCoordinator = false;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<String> getStaffCodes() {
        return staffCodes;
    }

    public void setStaffCodes(Set<String> staffCodes) {
        this.staffCodes = staffCodes;
    }

    public String getCoordinatorCode() {
        return coordinatorCode;
    }

    public void setCoordinatorCode(String coordinatorCode) {
        this.coordinatorCode = coordinatorCode;
    }

    public boolean isUpdateCoordinator() {
        return updateCoordinator;
    }

    public void setUpdateCoordinator(boolean updateCoordinator) {
        this.updateCoordinator = updateCoordinator;
    }
}
