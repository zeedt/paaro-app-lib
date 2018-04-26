package com.zeed.isms.lib.services;

import com.zeed.isms.lib.apimodel.IsmsUserApiModel;
import com.zeed.isms.lib.models.IsmsUser;
import com.zeed.isms.lib.models.RegistrationDetails;
import com.zeed.isms.lib.repository.IsmsUserRepository;
import com.zeed.isms.lib.repository.RegistrationDetailsRepository;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.enums.ResponseStatus;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.request.UserDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class IsmsUserService {

    @Autowired
    private UserDetailsRequest userDetailsRequest;

    @Autowired
    private IsmsUserRepository ismsUserRepository;

    @Autowired
    private RegistrationDetailsRepository registrationDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public IsmsUserApiModel addIsmsUSer(IsmsUser ismsUser){

        try {
            RegistrationDetails registrationDetails = registrationDetailsRepository.findByRegNo(ismsUser.getRegNo());
            if(registrationDetails == null) {
                return new IsmsUserApiModel(ismsUser, ResponseStatus.NOT_FOUND,String.format("Registration number: %s not found ",ismsUser.getRegNo()));
            }
            ismsUser.setDateCreated(new Date(new java.util.Date().getTime()));
            //Register the user in Managed as managed user before registering as ISMS user
            ManagedUser managedUser = new ManagedUser();
            transformIsmsUserToManagedUser(managedUser,ismsUser);
            ManagedUserModelApi managedUserModelApi = userDetailsRequest.addManagedUser(managedUser);
            if(managedUserModelApi.getResponseStatus() == ResponseStatus.SUCCESSFUL){
                ismsUser.setPassword(passwordEncoder.encode(ismsUser.getPassword()));
                ismsUser.setActivated(true);
                ismsUser.setManagedUserId(managedUserModelApi.getManagedUser().getId());
                ismsUser.setActivatedDate(new Date(new java.util.Date().getTime()));
                ismsUserRepository.save(ismsUser);
                ismsUser.setPassword("");
                return new IsmsUserApiModel(ismsUser, ResponseStatus.SUCCESSFUL,"Success");
            } else if(managedUserModelApi.getResponseStatus() == ResponseStatus.ALREADY_EXIST) {
                return new IsmsUserApiModel(ismsUser, ResponseStatus.ALREADY_EXIST,"The user already exist. Contact Admin to sort this issue");
            }else {
                return new IsmsUserApiModel(ismsUser, ResponseStatus.SYSTEM_ERROR,"Error occured due to " + managedUserModelApi.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new IsmsUserApiModel(ismsUser, ResponseStatus.SYSTEM_ERROR,e.getCause().toString());
        }

    }

    public void transformIsmsUserToManagedUser(ManagedUser managedUser, IsmsUser ismsUser){
        managedUser.setUserName(ismsUser.getUsername());
        managedUser.setUserCategory(ismsUser.getUserCategory());
        managedUser.setFirstName(ismsUser.getFirstName());
        managedUser.setLastName(ismsUser.getLastName());
        managedUser.setDateCreated(ismsUser.getDateCreated());
        managedUser.setPassword(ismsUser.getPassword());
        managedUser.setPhoneNumber(ismsUser.getPhoneNumber());
        managedUser.setEmail(ismsUser.getEmail());
    }

}
