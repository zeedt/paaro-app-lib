package com.zeed.isms.lib.models;


import com.zeed.isms.lib.enums.ClassLevel;
import com.zeed.isms.lib.enums.PresentClass;
import com.zeed.usermanagement.models.UserCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity(name = "isms_user")
public class IsmsUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String username;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_category")
    private UserCategory userCategory;

    @NotNull
    @Column(name = "date_created")
    private java.sql.Date dateCreated;

    @NotNull
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Column(name = "email")
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "class_level")
    private ClassLevel classLevel;

    @Column(name = "present_class")
    @Enumerated(value = EnumType.STRING)
    private PresentClass presentClass;

    @NotNull
    @Column(name = "managed_user_id", unique = true)
    private Long managedUserId;

    @NotNull
    @Column(name = "is_active")
    private boolean isActive = false;

    @NotNull
    @Column(name = "reg_no", unique = true)
    private String regNo;

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

    public ClassLevel getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(ClassLevel classLevel) {
        this.classLevel = classLevel;
    }

    public PresentClass getPresentClass() {
        return presentClass;
    }

    public void setPresentClass(PresentClass presentClass) {
        this.presentClass = presentClass;
    }

    public Long getManagedUserId() {
        return managedUserId;
    }

    public void setManagedUserId(Long managedUserId) {
        this.managedUserId = managedUserId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(UserCategory userCategory) {
        this.userCategory = userCategory;
    }

    public java.sql.Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(java.sql.Date dateCreated) {
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

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }
}
