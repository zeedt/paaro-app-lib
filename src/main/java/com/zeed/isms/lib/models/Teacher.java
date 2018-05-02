package com.zeed.isms.lib.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.zeed.isms.lib.enums.PresentClass;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Teacher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(unique = true)
    @NotNull
    private String username;

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;


    @NotNull
    @Column(name = "date_created")
    private Date dateCreated;

    @NotNull
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Column(name = "email")
    private String email;


    @Column(name = "staff_id",unique = true)
    private String staffId;

    @NotNull
    private boolean isClassTeacher = false;

    private PresentClass classOfClassTeacher;

    @NotNull
    private boolean isCourseCordinator = false;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teachersList" ,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Set<Course> courseHandled = new HashSet<>();


    @NotNull
    @Column(name = "managed_user_id", unique = true)
    private Long managedUserId;


    @NotNull
    @Column(name = "is_active")
    private boolean isActive = false;


    @Column(name = "is_activated")
    private boolean isActivated = false;

    @Column(name = "activated_date")
    private Date activatedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public boolean isClassTeacher() {
        return isClassTeacher;
    }

    public void setClassTeacher(boolean classTeacher) {
        isClassTeacher = classTeacher;
    }

    public PresentClass getClassOfClassTeacher() {
        return classOfClassTeacher;
    }

    public void setClassOfClassTeacher(PresentClass classOfClassTeacher) {
        this.classOfClassTeacher = classOfClassTeacher;
    }

    public boolean isCourseCordinator() {
        return isCourseCordinator;
    }

    public void setCourseCordinator(boolean courseCordinator) {
        isCourseCordinator = courseCordinator;
    }

    public Set<Course> getCourseHandled() {
        return courseHandled;
    }

    public void setCourseHandled(Set<Course> courseHandled) {
        this.courseHandled = courseHandled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getManagedUserId() {
        return managedUserId;
    }

    public void setManagedUserId(Long managedUserId) {
        this.managedUserId = managedUserId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public Date getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(Date activatedDate) {
        this.activatedDate = activatedDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
