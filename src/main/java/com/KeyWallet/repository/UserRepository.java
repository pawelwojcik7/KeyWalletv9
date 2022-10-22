package com.KeyWallet.repository;

import com.KeyWallet.entity.UserKW;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserKW, Long> {

    UserKW findByLogin(String login);

    boolean existsByLogin(String login);

    @Modifying
    @Query("update UserDB set isPasswordKeptAsHash = :isPasswordKeptAsHash, passwordHash = :passwordHash, salt = :salt where id = :id")
    void updateUserDataWithNewPassword(
            @Param("isPasswordKeptAsHash") Boolean isPasswordKeptAsHash,
            @Param("passwordHash") String passwordHash,
            @Param("salt") String salt,
            @Param("id") Long id);


}
