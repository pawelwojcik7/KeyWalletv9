package com.KeyWallet.repository;

import com.KeyWallet.entity.UserLogin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends CrudRepository<UserLogin, Long> {
}
