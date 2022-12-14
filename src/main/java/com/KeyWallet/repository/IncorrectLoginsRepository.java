package com.KeyWallet.repository;

import com.KeyWallet.entity.IncorrectLogins;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncorrectLoginsRepository extends CrudRepository<IncorrectLogins, Long> {
}
