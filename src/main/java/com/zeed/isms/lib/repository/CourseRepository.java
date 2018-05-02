package com.zeed.isms.lib.repository;

import com.zeed.isms.lib.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long>{

    List<Course> findAllById(Long id);

    Course findCourseById(Long id);

    Course findCourseByCourseCode(String id);

    Set<Course> findCourseByCourseCodeIn(Set<String> courseCodes);

    void deleteCourseById(Long id);

}
