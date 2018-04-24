package com.zeed.isms.lib.models;

import com.zeed.isms.lib.enums.States;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name="bio_data")
public class BioData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String address1;

    @NotNull
    private String address2;

    @NotNull
    private String parentPhoneNo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private States state;

    @OneToOne
    @NotNull
    private IsmsUser ismsUser;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getParentPhoneNo() {
        return parentPhoneNo;
    }

    public void setParentPhoneNo(String parentPhoneNo) {
        this.parentPhoneNo = parentPhoneNo;
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    public IsmsUser getIsmsUser() {
        return ismsUser;
    }

    public void setIsmsUser(IsmsUser ismsUser) {
        this.ismsUser = ismsUser;
    }
}
