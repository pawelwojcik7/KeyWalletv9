package com.KeyWallet.repository;

import com.KeyWallet.entity.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends CrudRepository<Status, Long> {
}
