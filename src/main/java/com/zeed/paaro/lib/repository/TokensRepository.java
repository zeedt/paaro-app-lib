package com.zeed.paaro.lib.repository;

import com.zeed.paaro.lib.enums.Module;
import com.zeed.paaro.lib.models.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokensRepository extends JpaRepository<Tokens,Long> {

    Tokens findByEmailAndModuleAndTokenOrderByIdDesc(String email, Module module, String token);

}
