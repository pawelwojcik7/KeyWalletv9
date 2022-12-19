package com.KeyWallet.repository;
import com.KeyWallet.entity.IpLoginHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface IpLoginHistoryRepository extends CrudRepository<IpLoginHistory, Long> {
    List<IpLoginHistory> findAllByIpAddress(String ipAddress);
    List<IpLoginHistory> findAll();
    void deleteAllByIpAddress(String ipAddress);
}
