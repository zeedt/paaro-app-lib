package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.models.TransferRequestMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRequestMapRepository extends JpaRepository<TransferRequestMap, Long> {



}
