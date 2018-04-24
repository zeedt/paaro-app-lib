package com.zeed.isms.lib.services;

import com.zeed.isms.lib.apimodel.RegistrationDetailsApiModel;
import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.models.RegistrationDetails;
import com.zeed.isms.lib.repository.RegistrationDetailsRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RegistrationDetailsService {

    @Autowired
    public RegistrationDetailsRepository registrationDetailsRepository;

    public RegistrationDetailsApiModel getRegDetailsById(Long id){

        try {
            List<RegistrationDetails> registrationDetails = registrationDetailsRepository.findAllById(id);

            if (registrationDetails!=null && !registrationDetails.isEmpty()) {
                RegistrationDetailsApiModel registrationDetailsApiModel = new RegistrationDetailsApiModel(ResponseCode.COMPLETED,registrationDetails.get(0),
                        null,"Successfully fetched");
                return registrationDetailsApiModel;
            }
            else {
                return new RegistrationDetailsApiModel(ResponseCode.NO_DATA,null,
                        null,"Successfull with no data found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public RegistrationDetailsApiModel getRegDetailsByRegNo(String regNo){

        try {
            RegistrationDetails registrationDetails = registrationDetailsRepository.findByRegNo(regNo);

            if (registrationDetails!=null) {
                RegistrationDetailsApiModel registrationDetailsApiModel = new RegistrationDetailsApiModel(ResponseCode.COMPLETED,registrationDetails,
                        null,"Successfully fetched");
                return registrationDetailsApiModel;
            }
            else {
                return new RegistrationDetailsApiModel(ResponseCode.NO_DATA,null,
                        null,"Successfull with no data found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public RegistrationDetailsApiModel addRegDetails(RegistrationDetails registrationDetails){

        try {

            RegistrationDetails registrationDetails1 = registrationDetailsRepository.findByRegNo(registrationDetails.getRegNo());

            if (registrationDetails1 !=null) {
                return new RegistrationDetailsApiModel(ResponseCode.RECORD_ALREADY_EXIST,null,
                        null,String.format("A record with staff %s already exist",registrationDetails.getRegNo()));
            }

            registrationDetailsRepository.save(registrationDetails);

                RegistrationDetailsApiModel registrationDetailsApiModel = new RegistrationDetailsApiModel(ResponseCode.SAVED,registrationDetails,
                        null,"Successfully saved");
                return registrationDetailsApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public RegistrationDetailsApiModel deleteRegDetailsById(Long id){

        try {
            registrationDetailsRepository.deleteRegistrationDetailsById(id);

                RegistrationDetailsApiModel registrationDetailsApiModel = new RegistrationDetailsApiModel(ResponseCode.DELETED,null,
                        null,"Successfully deleted");
                return registrationDetailsApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public RegistrationDetailsApiModel updateRegDetailsById(RegistrationDetails registrationDetails){

        try {
            registrationDetailsRepository.save(registrationDetails);
                RegistrationDetailsApiModel registrationDetailsApiModel = new RegistrationDetailsApiModel(ResponseCode.UPDATED,null,
                        null,"Successfully updated");
                return registrationDetailsApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }
    public RegistrationDetailsApiModel fetchALlRegDetails(){

        try {
            List<RegistrationDetails> registrationDetailsList = registrationDetailsRepository.findAll();
                RegistrationDetailsApiModel registrationDetailsApiModel = new RegistrationDetailsApiModel(ResponseCode.COMPLETED,null,
                        registrationDetailsList,"Successfully fetched all");
                return registrationDetailsApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }



    public RegistrationDetailsApiModel uploadBulk(MultipartFile file) throws IOException {

        Workbook workbook = new HSSFWorkbook(file.getInputStream());

        FileInputStream fileBytes = new FileInputStream(new File(""));


        return null;
    }





}

