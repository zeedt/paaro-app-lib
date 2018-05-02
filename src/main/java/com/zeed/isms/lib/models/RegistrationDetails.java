package com.zeed.isms.lib.models;

import com.zeed.usermanagement.models.UserCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "registration_details")
public class RegistrationDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(name = "reg_no", unique = true)
    private String regNo;

    @NotNull
    @Column(name = "date_of_reg")
    private Date dateOfReg;

    @NotNull
    @Column(name = "expt_year_of_grad")
    private Date exptYearOfGrad;

    @Column(name = "date_uploaded")
    private Date dateUploaded;

    @Column(name = "user_type")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private UserCategory userType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public Date getDateOfReg() {
        return dateOfReg;
    }

    public void setDateOfReg(Date dateOfReg) {
        this.dateOfReg = dateOfReg;
    }

    public Date getExptYearOfGrad() {
        return exptYearOfGrad;
    }

    public void setExptYearOfGrad(Date exptYearOfGrad) {
        this.exptYearOfGrad = exptYearOfGrad;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public UserCategory getUserType() {
        return userType;
    }

    public void setUserType(UserCategory userType) {
        this.userType = userType;
    }
}
