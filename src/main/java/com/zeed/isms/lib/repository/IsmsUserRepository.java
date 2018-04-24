package com.zeed.isms.lib.repository;

import com.zeed.isms.lib.enums.ClassLevel;
import com.zeed.isms.lib.enums.PresentClass;
import com.zeed.isms.lib.models.IsmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IsmsUserRepository extends JpaRepository<IsmsUser,Long>{

    List<IsmsUser> findAllById(Long id);

    List<IsmsUser> findAllByClassLevel(ClassLevel classLevel);

    List<IsmsUser> findAllBypresentClass(PresentClass presentClass);

    List<IsmsUser> findAllByManagedUserId(Long id);

}
