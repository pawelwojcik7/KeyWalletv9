package com.KeyWallet.repository;


import com.KeyWallet.entity.UserKW;
import com.KeyWallet.entity.UserLoginHistory;
import com.KeyWallet.models.LoginResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LoginHistoryRepository extends CrudRepository<UserLoginHistory, Long> {

    UserLoginHistory findFirstByUserKWAndLoginResultOrderByLoginDateDesc(UserKW userKW, LoginResult loginResult);

    List<UserLoginHistory> findAllByUserKW(UserKW userkw);
}
