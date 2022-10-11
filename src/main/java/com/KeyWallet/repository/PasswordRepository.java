package com.KeyWallet.repository;

import com.KeyWallet.entity.Password;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordRepository extends CrudRepository<Password, Long> {

    //@Query("SELECT p FROM password p WHERE p.userId = :id")
    List<Password> findAllByuserId(Long id);
}

