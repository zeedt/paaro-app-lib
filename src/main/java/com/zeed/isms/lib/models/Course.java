package com.zeed.isms.lib.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zeed.isms.lib.enums.ClassLevel;
import com.zeed.isms.lib.enums.PresentClass;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "course_title")
    private String courseTitle;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = "course_teachers", joinColumns = {@JoinColumn (name = "course_id")}, inverseJoinColumns = {@JoinColumn(name = "teacher_id")})
    private Set<Teacher> teachersList = new HashSet<>();

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "class")
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private PresentClass presentClass;

    @Column(name = "class_level")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private ClassLevel classLevel;

    @Column(name = "course_desc")
    private String courseDescription;

    @OneToOne
    private Teacher coordinator;

    @NotNull
    private Date dateCreated;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Set<Teacher> getTeachersList() {
        return teachersList;
    }

    public void setTeachersList(Set<Teacher> teachersList) {
        this.teachersList = teachersList;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public PresentClass getPresentClass() {
        return presentClass;
    }

    public void setPresentClass(PresentClass presentClass) {
        this.presentClass = presentClass;
    }

    public ClassLevel getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(ClassLevel classLevel) {
        this.classLevel = classLevel;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Teacher getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Teacher coordinator) {
        this.coordinator = coordinator;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
