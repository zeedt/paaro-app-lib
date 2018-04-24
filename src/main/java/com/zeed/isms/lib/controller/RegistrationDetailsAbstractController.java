package com.zeed.isms.lib.controller;

import com.zeed.isms.lib.apimodel.RegistrationDetailsApiModel;
import com.zeed.isms.lib.models.RegistrationDetails;
import com.zeed.isms.lib.services.RegistrationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/regDetails")
public abstract class RegistrationDetailsAbstractController {

    @Autowired
    private RegistrationDetailsService registrationDetailsService;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RegistrationDetailsApiModel addRegDetails(@RequestBody RegistrationDetails registrationDetails){
        return registrationDetailsService.addRegDetails(registrationDetails);
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public RegistrationDetailsApiModel deleteRegDetails(@PathVariable("id") Long id){
        return registrationDetailsService.deleteRegDetailsById(id);
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RegistrationDetailsApiModel updateRegDetails(@RequestBody RegistrationDetails registrationDetails){
        return registrationDetailsService.updateRegDetailsById(registrationDetails);
    }
    @ResponseBody
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public RegistrationDetailsApiModel getRegDetails(@PathVariable("id") Long id){
        return registrationDetailsService.getRegDetailsById(id);
    }
    @ResponseBody
    @RequestMapping(value = "/fetchAll", method = RequestMethod.GET)
    public RegistrationDetailsApiModel fetchAllRegDetails(){
        return registrationDetailsService.fetchALlRegDetails();
    }

    @ResponseBody
    @RequestMapping(value = "/uploadBulk", method = RequestMethod.POST)
    public RegistrationDetailsApiModel uploadInBulk(@RequestParam("file") MultipartFile file){
        try {
            return registrationDetailsService.uploadBulk(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
