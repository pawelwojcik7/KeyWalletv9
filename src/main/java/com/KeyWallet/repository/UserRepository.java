package com.KeyWallet.repository;

import java.util.*;
import com.KeyWallet.entity.Password;
import com.KeyWallet.entity.UserKW;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserKW, Long> {

}
