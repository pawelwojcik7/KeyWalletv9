package com.KeyWallet.repository;

import com.KeyWallet.entity.UserKW;
import com.KeyWallet.models.UserStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface UserRepository extends CrudRepository<UserKW, Long> {

    UserKW findByLogin(String login);


    boolean existsByLogin(String login);

    @Modifying
    @Query("update userKW set isPasswordKeptAsHash = :isPasswordKeptAsHash, passwordHash = :passwordHash, salt = :salt where id = :id")
    void updateUserDataWithNewPassword(
            @Param("isPasswordKeptAsHash") Boolean isPasswordKeptAsHash,
            @Param("passwordHash") String passwordHash,
            @Param("salt") String salt,
            @Param("id") Long id);

    @Modifying
    @Query("update userKW set smsCode = :smsCode, codeTimeLife = :codeTimeLife where login = :login")
    void updateUserDataWithNewSmsCode(
            @Param("smsCode") Integer smsCode,
            @Param("codeTimeLife")OffsetDateTime codeTimeLife,
            @Param("login") String login);

    @Modifying
    @Query("update userKW set status = :status where login = :login")
    void updateUserDataWithStatus(
            @Param("status") UserStatus status,
            @Param("login") String login);

    @Modifying
    @Query("update userKW set lockoutTime= :lockoutTime where id = :id")
    void updateUserDataWithTempLock(
            @Param("lockoutTime") OffsetDateTime lockoutTime,
            @Param("id") Long id);

}
