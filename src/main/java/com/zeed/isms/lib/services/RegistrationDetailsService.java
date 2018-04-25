package com.zeed.isms.lib.services;

import com.zeed.generic.ExcelUtils;
import com.zeed.isms.lib.apimodel.RegistrationDetailsApiModel;
import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.enums.UserType;
import com.zeed.isms.lib.models.RegistrationDetails;
import com.zeed.isms.lib.repository.RegistrationDetailsRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class RegistrationDetailsService {

    @Autowired
    public RegistrationDetailsRepository registrationDetailsRepository;

    @Autowired
    public ExcelUtils excelUtils;

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
            registrationDetails.setDateUploaded(new Date());
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

    @Transactional
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
            RegistrationDetails registrationDetails1 = registrationDetailsRepository.findByRegNo(registrationDetails.getRegNo());

            if (registrationDetails1!=null && registrationDetails1.getId()!=registrationDetails.getId()) {
                return new RegistrationDetailsApiModel(ResponseCode.RECORD_ALREADY_EXIST,null,
                        null,String.format("Update failed because another user has been registered with the registration number %s",registrationDetails.getRegNo()));
            }
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
    public RegistrationDetailsApiModel fetchAllRegDetails(){

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



    @Transactional
    public RegistrationDetailsApiModel uploadBulk (MultipartFile file) {

        try {
            Workbook workbook = excelUtils.getWorkBook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);
            HashMap<String,Integer> headers = excelUtils.extractHeaderWithRowNum(workbook,1);

            for (int i=1;i<sheet.getPhysicalNumberOfRows();i++) {

                Row row = sheet.getRow(i);
                RegistrationDetails registrationDetails = new RegistrationDetails();
                registrationDetails.setName(row.getCell(0).getStringCellValue());
                registrationDetails.setRegNo(row.getCell(1).getStringCellValue());
                registrationDetails.setDateOfReg(row.getCell(2).getDateCellValue());
                registrationDetails.setExptYearOfGrad(row.getCell(3).getDateCellValue());
                registrationDetails.setUserType(UserType.valueOf(row.getCell(4).getStringCellValue()));
                registrationDetails.setDateUploaded(new Date());
                registrationDetailsRepository.save(registrationDetails);
            }

            return new RegistrationDetailsApiModel(ResponseCode.BULK_UPLOAD_SUCCESSFULL,null,
                    null,"Successfully uploaded all records");
        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationDetailsApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

}

