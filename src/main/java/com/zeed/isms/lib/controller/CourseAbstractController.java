package com.zeed.isms.lib.controller;

import com.zeed.isms.lib.apimodel.CourseApiModel;
import com.zeed.isms.lib.models.Course;
import com.zeed.isms.lib.requestmodel.CourseApiRequestModel;
import com.zeed.isms.lib.services.CourseService;
import com.zeed.isms.lib.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/course")
public abstract class CourseAbstractController {


    @Autowired
    private CourseService courseService;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CourseApiModel addCourses(@RequestBody CourseApiRequestModel courseApiRequestModel){
        return courseService.addCourse(courseApiRequestModel);
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CourseApiModel deleteCourses(@PathVariable("id") Long id){
        return courseService.deleteCoursesById(id);
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CourseApiModel updateCourses(@RequestBody CourseApiRequestModel courseApiRequestModel){
        return courseService.updateCoursesById(courseApiRequestModel);
    }
    @ResponseBody
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public CourseApiModel getCourses(@PathVariable("id") Long id){
        return courseService.getCoursesById(id);
    }
    @ResponseBody
    @RequestMapping(value = "/getByCourseCode/{courseCode}", method = RequestMethod.GET)
    public CourseApiModel getCoursesByCourseCode(@PathVariable("courseCode") String courseCode){
        return courseService.getCoursesByCourseCode(courseCode);
    }
    @ResponseBody
    @RequestMapping(value = "/fetchAll", method = RequestMethod.GET)
    public CourseApiModel fetchAllCourses(){
        return courseService.fetchAllCourses();
    }

    @ResponseBody
    @RequestMapping(value = "/uploadBulk", method = RequestMethod.POST)
    public CourseApiModel uploadInBulk(@RequestParam("file") MultipartFile file){
        return null;
//        return courseService.uploadBulk(file);
    }

}
