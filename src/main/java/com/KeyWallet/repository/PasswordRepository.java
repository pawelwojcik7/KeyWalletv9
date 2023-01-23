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
    List<Password> findAllByParentPasswordId(Long parentPasswordId);

    List<Password> findAllByParentPasswordIdAndUserId(Long parenPasswordId, Long userId);

    @Modifying
    @Query("update password set password = :password where id = :id")
    void updatePasswordDataWithNewPassword(
            @Param("password") String password,
            @Param("id") Long id);

}

