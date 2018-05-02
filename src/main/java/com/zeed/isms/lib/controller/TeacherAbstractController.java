package com.zeed.isms.lib.controller;

import com.zeed.isms.lib.apimodel.TeacherApiModel;
import com.zeed.isms.lib.requestmodel.TeacherApiRequestModel;
import com.zeed.isms.lib.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/teacher")
public abstract class TeacherAbstractController {

    @Autowired
    private TeacherService teacherService;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public TeacherApiModel addTeachers(@RequestBody TeacherApiRequestModel teacherApiRequestModel){
        return teacherService.addTeacher(teacherApiRequestModel);
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public TeacherApiModel deleteTeachers(@PathVariable("id") Long id){
        return teacherService.deleteTeacherById(id);
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public TeacherApiModel updateTeachers(@RequestBody TeacherApiRequestModel teacherApiRequestModel){
        return teacherService.updateTeacherById(teacherApiRequestModel);
    }
    @ResponseBody
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public TeacherApiModel getTeachers(@PathVariable("id") Long id){
        return teacherService.getTeacherById(id);
    }
    @ResponseBody
    @RequestMapping(value = "/getByTeacherCode/{teacherCode}", method = RequestMethod.GET)
    public TeacherApiModel getTeachersByTeacherCode(@PathVariable("teacherCode") String teacherCode){
        return teacherService.getTeacherByStaffCode(teacherCode);
    }
    @ResponseBody
    @RequestMapping(value = "/fetchAll", method = RequestMethod.GET)
    public TeacherApiModel fetchAllTeachers(){
        return teacherService.fetchAllTeachers();
    }

    @ResponseBody
    @RequestMapping(value = "/uploadBulk", method = RequestMethod.POST)
    public TeacherApiModel uploadInBulk(@RequestParam("file") MultipartFile file){
        return null;
//        return teacherService.uploadBulk(file);
    }
    
}
