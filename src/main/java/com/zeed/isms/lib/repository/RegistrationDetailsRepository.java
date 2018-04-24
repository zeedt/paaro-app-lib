package com.zeed.isms.lib.repository;

import com.zeed.isms.lib.models.RegistrationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationDetailsRepository extends JpaRepository<RegistrationDetails,Long>{

    List<RegistrationDetails> findAllById(Long id);

    RegistrationDetails findByRegNo(String regNo);

    void deleteRegistrationDetailsById(Long id);


}
