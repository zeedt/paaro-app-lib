package com.zeed.isms.lib.services;

import com.zeed.generic.ExcelUtils;
import com.zeed.isms.lib.apimodel.IsmsUserApiModel;
import com.zeed.isms.lib.apimodel.TeacherApiModel;
import com.zeed.isms.lib.enums.ResponseCode;
import com.zeed.isms.lib.models.Course;
import com.zeed.isms.lib.models.IsmsUser;
import com.zeed.isms.lib.models.Teacher;
import com.zeed.isms.lib.repository.CourseRepository;
import com.zeed.isms.lib.repository.TeacherRepository;
import com.zeed.isms.lib.requestmodel.TeacherApiRequestModel;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.enums.ResponseStatus;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.models.UserCategory;
import com.zeed.usermanagement.request.UserDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserDetailsRequest userDetailsRequest;
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExcelUtils excelUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TeacherApiModel getTeacherById(Long id){

        try {
            Teacher teacher = teacherRepository.findTeacherById(id);

            if (teacher!=null) {
                TeacherApiModel teacherApiModel = new TeacherApiModel(ResponseCode.COMPLETED,teacher,
                        null,"Successfully fetched");
                return teacherApiModel;
            }
            else {
                return new TeacherApiModel(ResponseCode.NO_DATA,null,
                        null,"Successfull with no data found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TeacherApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public TeacherApiModel getTeacherByStaffCode(String staffCode){

        try {
            Teacher teacher = teacherRepository.findTeacherByStaffId(staffCode);

            if (teacher!=null) {
                TeacherApiModel teacherApiModel = new TeacherApiModel(ResponseCode.COMPLETED,teacher,
                        null,"Successfully fetched");
                return teacherApiModel;
            }
            else {
                return new TeacherApiModel(ResponseCode.NO_DATA,null,
                        null,"Successfull with no data found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TeacherApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public TeacherApiModel addTeacher(TeacherApiRequestModel teacherApiRequestModel){
        Teacher teacher = teacherApiRequestModel.getTeacher();
        Set<String> courseCodes = teacherApiRequestModel.getCourseCodes();

        try {

            Teacher teacher1 = teacherRepository.findTeacherByStaffId(teacher.getStaffId());
            teacher.setDateCreated(new Date());
            if (teacher1 !=null) {
                return new TeacherApiModel(ResponseCode.RECORD_ALREADY_EXIST,null,
                        null,String.format("A record with staff %s already exist",teacher.getStaffId()));
            }
            ManagedUser managedUser = new ManagedUser();
            transformTeacherToManagedUser(managedUser,teacher);
            ManagedUserModelApi managedUserModelApi = userDetailsRequest.addManagedUser(managedUser);
            if(managedUserModelApi.getResponseStatus() == ResponseStatus.SUCCESSFUL){
                teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
                teacher.setActivated(true);
                teacher.setActive(true);
                teacher.setManagedUserId(managedUserModelApi.getManagedUser().getId());
                teacher.setActivatedDate(new java.sql.Date(new java.util.Date().getTime()));
                Set<Course> courses = new HashSet<>();
                if(courseCodes!=null){ courses = courseRepository.findCourseByCourseCodeIn(courseCodes);}
                if (courses!=null) {
                    teacher.setCourseHandled(courses);
                }
                teacherRepository.save(teacher);
//                teacher.setPassword("");
                TeacherApiModel teacherApiModel = new TeacherApiModel(ResponseCode.SAVED,teacher,
                        null,"Successfully saved");
                return teacherApiModel;
            } else if(managedUserModelApi.getResponseStatus() == ResponseStatus.ALREADY_EXIST) {
                return new TeacherApiModel(ResponseCode.RECORD_ALREADY_EXIST, teacher, null,"The user already exist. Contact Admin to sort this issue");
            }else {
                return new TeacherApiModel(ResponseCode.SYSTEM_ERROR, teacher, null,"Error occured due to " + managedUserModelApi.getMessage());
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            return new TeacherApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    @Transactional
    public TeacherApiModel deleteTeacherById(Long id){

        try {
            Teacher teacher = teacherRepository.findTeacherById(id);
            if (teacher==null) {
                return new TeacherApiModel(ResponseCode.INCOMPLETE,null,
                        null,"Record not found and cannot be deleted");
            }
            if (teacher.getCourseHandled()!=null){
                teacher.getCourseHandled().stream().forEach(course -> {
                    course.getTeachersList().remove(teacher);
                    courseRepository.save(course);
                });
            }
            teacherRepository.deleteTeacherById(id);
            TeacherApiModel teacherApiModel = new TeacherApiModel(ResponseCode.DELETED,null,
                    null,"Successfully deleted");
            return teacherApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new TeacherApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public TeacherApiModel updateTeacherById(TeacherApiRequestModel teacherApiRequestModel){

        try {
            Teacher teacher = teacherApiRequestModel.getTeacher();
            Teacher teacher1 = teacherRepository.findTeacherByStaffId(teacher.getStaffId());

            if (teacher1 == null) {
                return new TeacherApiModel(ResponseCode.RECORD_NOT_FOUND,null,
                        null,String.format("Update failed because user registered with the staff ID %s not found",teacher.getStaffId()));
            }

            if (teacher1!=null && teacher1.getId()!=teacher.getId()) {
                return new TeacherApiModel(ResponseCode.RECORD_ALREADY_EXIST,null,
                        null,String.format("Update failed because another user has been registered with the staff ID %s",teacher.getStaffId()));
            }
            teacher.setDateCreated(teacher1.getDateCreated());
            teacher.setManagedUserId(teacher1.getManagedUserId());
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
            teacherRepository.save(teacher);
            TeacherApiModel teacherApiModel = new TeacherApiModel(ResponseCode.UPDATED,null,
                    null,"Successfully updated");
            return teacherApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new TeacherApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }
    public TeacherApiModel fetchAllTeachers(){

        try {
            List<Teacher> teacherList = teacherRepository.findAll();
            TeacherApiModel teacherApiModel = new TeacherApiModel(ResponseCode.COMPLETED,null,
                    teacherList,"Successfully fetched all");
            return teacherApiModel;
        } catch (Exception e) {
            e.printStackTrace();
            return new TeacherApiModel(ResponseCode.SYSTEM_ERROR,null,
                    null,"System error occured due to " + e);
        }

    }

    public void transformTeacherToManagedUser(ManagedUser managedUser, Teacher teacher){
        managedUser.setUserName(teacher.getUsername());
        managedUser.setUserCategory(UserCategory.TEACHER);
        managedUser.setFirstName(teacher.getFirstName());
        managedUser.setLastName(teacher.getLastName());
        managedUser.setDateCreated(new java.sql.Date (teacher.getDateCreated().getTime()));
        managedUser.setPassword(teacher.getPassword());
        managedUser.setPhoneNumber(teacher.getPhoneNumber());
        managedUser.setEmail(teacher.getEmail());
    }

}
