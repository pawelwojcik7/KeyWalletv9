package com.KeyWallet.repository;

import com.KeyWallet.entity.UserLogin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLoginRepository extends CrudRepository<UserLogin, Long> {

    List<UserLogin> findUserLoginByIdUser(Long idUser);
}
