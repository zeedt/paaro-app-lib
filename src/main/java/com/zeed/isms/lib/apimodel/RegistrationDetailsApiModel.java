package com.zeed.isms.lib.apimodel;

import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.models.RegistrationDetails;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class RegistrationDetailsApiModel implements Serializable {

    @NotNull
    private ResponseCode responseCode;

    private RegistrationDetails registrationDetails;

    private List<RegistrationDetails> registrationDetailsList;

    private String narration;

    public RegistrationDetailsApiModel(@NotNull ResponseCode responseCode, RegistrationDetails registrationDetails, List<RegistrationDetails> registrationDetailsList, String narration) {
        this.responseCode = responseCode;
        this.registrationDetails = registrationDetails;
        this.registrationDetailsList = registrationDetailsList;
        this.narration = narration;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public RegistrationDetails getRegistrationDetails() {
        return registrationDetails;
    }

    public void setRegistrationDetails(RegistrationDetails registrationDetails) {
        this.registrationDetails = registrationDetails;
    }

    public List<RegistrationDetails> getRegistrationDetailsList() {
        return registrationDetailsList;
    }

    public void setRegistrationDetailsList(List<RegistrationDetails> registrationDetailsList) {
        this.registrationDetailsList = registrationDetailsList;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
