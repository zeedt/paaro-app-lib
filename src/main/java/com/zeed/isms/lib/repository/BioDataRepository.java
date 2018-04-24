package com.zeed.isms.lib.repository;

import com.zeed.isms.lib.models.BioData;
import com.zeed.isms.lib.models.IsmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BioDataRepository extends JpaRepository<BioData,Long> {

    BioData findByIsmsUser(IsmsUser ismsUser);

}
