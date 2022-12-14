package com.KeyWallet.repository;

import com.KeyWallet.entity.IpAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpAddressRepository extends CrudRepository<IpAddress, Long> {
}
