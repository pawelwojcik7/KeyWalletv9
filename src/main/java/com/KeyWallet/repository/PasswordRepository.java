package com.KeyWallet.repository;

import com.KeyWallet.entity.Password;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordRepository extends CrudRepository<Password, Long> {

    List<Password> findAllByUserId(Long id);
    @Modifying
    @Query("update Passwords set password = :password where passId = :id")
    void updatePasswordDataWithNewPassword(
            @Param("password") String password,
            @Param("id") Long id);

}

