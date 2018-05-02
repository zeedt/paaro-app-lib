package com.zeed.isms.lib.repository;

import com.zeed.isms.lib.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    List<Teacher> findAllById(Long id);

    Teacher findTeacherByStaffId(String staffId);

    Teacher findTeacherById(Long id);

    Set<Teacher> findTeachersByStaffIdIn(Set<String> staffId);

    void deleteTeacherById(Long id);

}
