package com.zeed.isms.lib.services;

import com.zeed.generic.ExcelUtils;
import com.zeed.isms.lib.apimodel.CourseApiModel;
import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.models.Course;
import com.zeed.isms.lib.models.Teacher;
import com.zeed.isms.lib.repository.CourseRepository;
import com.zeed.isms.lib.repository.TeacherRepository;
import com.zeed.isms.lib.requestmodel.CourseApiRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CourseService {

    @Autowired
    public CourseRepository courseRepository;

    @Autowired
    public TeacherRepository teacherRepository;

    @Autowired
    public ExcelUtils excelUtils;

    public CourseApiModel getCoursesById(Long id){

        try {
            Course course = courseRepository.findCourseById(id);

            if (course!=null) {
                CourseApiModel courseApiModel = new CourseApiModel(ResponseCode.COMPLETED,course,
                        null,"Successfully fetched");
                return courseApiModel;
            }
            else {
                return new CourseApiModel(ResponseCode.NO_DATA,null,
                        null,"Successfull with no data found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CourseApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public CourseApiModel getCoursesByCourseCode(String courseCode){

        try {
            Course course = courseRepository.findCourseByCourseCode(courseCode);

            if (course!=null) {
                CourseApiModel courseApiModel = new CourseApiModel(ResponseCode.COMPLETED,course,
                        null,"Successfully fetched");
                return courseApiModel;
            }
            else {
                return new CourseApiModel(ResponseCode.NO_DATA,null,
                        null,"Successfull with no data found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CourseApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public CourseApiModel addCourse(CourseApiRequestModel courseApiRequestModel){

        Course course = courseApiRequestModel.getCourse();
        Set<String> staffCodes = (courseApiRequestModel.getStaffCodes()!=null) ? courseApiRequestModel.getStaffCodes() : new HashSet<>();

        try {

            Course course1 = courseRepository.findCourseByCourseCode(course.getCourseCode());

            if (course1 !=null) {
                return new CourseApiModel(ResponseCode.RECORD_ALREADY_EXIST,null,
                        null,String.format("A record with course code %s already exist",course.getCourseCode()));
            }
            course.setDateCreated(new Date());
            // Get all the teachers and add to the course
            if (staffCodes!=null && !staffCodes.isEmpty()) {
                Set<Teacher> teacherList = teacherRepository.findTeachersByStaffIdIn(staffCodes);
                if ((teacherList != null)) {
                    course.setTeachersList(teacherList);
                } else {
                    course.setTeachersList(new HashSet<>());
                }
            }
            // Find the coordinator and add;
            Teacher teacher = teacherRepository.findTeacherByStaffId(courseApiRequestModel.getCoordinatorCode());
            //Decide about the iscordinator field in teacher model
            if (teacher!=null) { course.setCoordinator(teacher); }
            courseRepository.save(course);
            CourseApiModel courseApiModel = new CourseApiModel(ResponseCode.SAVED,course,
                    null,"Successfully saved");
            return courseApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new CourseApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    @Transactional
    public CourseApiModel deleteCoursesById(Long id){

        try {
            Course course = courseRepository.findCourseById(id);

            if (course == null) {
                return new CourseApiModel(ResponseCode.INCOMPLETE,null,
                        null,"Record not found and delete operation was not performed");
            }
            courseRepository.deleteCourseById(id);
            CourseApiModel courseApiModel = new CourseApiModel(ResponseCode.DELETED,null,
                    null,"Successfully deleted");
            return courseApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new CourseApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public CourseApiModel updateCoursesById(CourseApiRequestModel courseApiRequestModel){

        Course course = courseApiRequestModel.getCourse();
        Set<String> staffCodes = courseApiRequestModel.getStaffCodes();

        try {
            Course course1 = courseRepository.findCourseByCourseCode(course.getCourseCode());

            if (course1 == null) {
                return new CourseApiModel(ResponseCode.RECORD_NOT_FOUND,null,
                        null,String.format("Update failed because no course with the Course code %s found",course.getCourseCode()));
            }
            if (course1!=null && course1.getId()!=course.getId()) {
                return new CourseApiModel(ResponseCode.RECORD_ALREADY_EXIST,null,
                        null,String.format("Update failed because another course has been registered ID with the course code %s",course.getCourseCode()));
            }
            Teacher coord = null;
            if (courseApiRequestModel.isUpdateCoordinator() && courseApiRequestModel.getCoordinatorCode() != null ) {
                coord = teacherRepository.findTeacherByStaffId(courseApiRequestModel.getCoordinatorCode());
                if (coord!=null) { course.setCoordinator(coord); }
            }else{
                course.setCoordinator(course1.getCoordinator());
            }
            courseRepository.save(course);
            String message = "Successfully updated";
            if (coord==null && courseApiRequestModel.isUpdateCoordinator()) {
                message = "Successfully updated but coordinator not found and wasn't set";
            }
            CourseApiModel courseApiModel = new CourseApiModel(ResponseCode.UPDATED,null,
                    null,"message");

            return courseApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new CourseApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }
    public CourseApiModel fetchAllCourses(){

        try {
            List<Course> courseList = courseRepository.findAll();
            CourseApiModel courseApiModel = new CourseApiModel(ResponseCode.COMPLETED,null,
                    courseList,"Successfully fetched all");
            return courseApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new CourseApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

}
