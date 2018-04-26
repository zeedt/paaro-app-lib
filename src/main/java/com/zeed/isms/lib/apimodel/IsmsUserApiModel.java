package com.zeed.isms.lib.apimodel;

import com.zeed.isms.lib.models.IsmsUser;
import com.zeed.usermanagement.enums.ResponseStatus;
import com.zeed.usermanagement.models.ManagedUser;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class IsmsUserApiModel implements Serializable{

    private IsmsUser ismsUser;

    @NotNull
    private ResponseStatus status;

    private String narration;

    public IsmsUserApiModel(IsmsUser ismsUser, @NotNull ResponseStatus status, String narration) {
        this.ismsUser = ismsUser;
        this.status = status;
        this.narration = narration;
    }

    public IsmsUser getIsmsUser() {
        return ismsUser;
    }

    public void setIsmsUser(IsmsUser ismsUser) {
        this.ismsUser = ismsUser;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
