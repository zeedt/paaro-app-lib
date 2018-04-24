package com.zeed.isms.lib.models;


import com.zeed.isms.lib.enums.ClassLevel;
import com.zeed.isms.lib.enums.PresentClass;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "isms_user")
public class IsmsUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "class_level")
    @NotNull
    private ClassLevel classLevel;

    @Column(name = "present_class")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private PresentClass presentClass;

    @Column(name = "expected_graduation_year")
    private Date expectedGraduationYear;

    @NotNull
    @Column(name = "managed_user_id", unique = true)
    private Long managedUserId;

    @NotNull
    @Column(name = "is_active")
    private boolean isActive = false;

    @NotNull
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

    public Date getExpectedGraduationYear() {
        return expectedGraduationYear;
    }

    public void setExpectedGraduationYear(Date expectedGraduationYear) {
        this.expectedGraduationYear = expectedGraduationYear;
    }

    public Long getManagedUserId() {
        return managedUserId;
    }

    public void setManagedUserId(Long managedUserId) {
        this.managedUserId = managedUserId;
    }
}
