package com.KeyWallet.repository;

import com.KeyWallet.entity.IpAddress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface IpAddressRepository extends CrudRepository<IpAddress, Long> {


    IpAddress findByIpAddress(String ipAddress);

    @Modifying
    @Query("update ip_address set badLoginNum = :badLoginNum where id = :id")
    void updateBadLoginNum(
            @Param("badLoginNum") Integer badLoginNum,
            @Param("id") Long id);

    @Modifying
    @Query("update ip_address set lastBadLoginNum = :lastBadLoginNum where id = :id")
    void updateLastBadLoginNum(
            @Param("lastBadLoginNum") Integer lastBadLoginNum,
            @Param("id") Long id);

    @Modifying
    @Query("update ip_address set okLoginNum = :okLoginNum where id = :id")
    void updateOkLoginNum(
            @Param("okLoginNum") Integer okLoginNum,
            @Param("id") Long id);

    @Modifying
    @Query("update ip_address set permanentLock = :permanentLock where id = :id")
    void updatePermanentLock(
            @Param("permanentLock") Boolean permanentLock,
            @Param("id") Long id);

    @Modifying
    @Query("update ip_address set tempLock = :tempLock where id = :id")
    void updateTempLock(
            @Param("tempLock") OffsetDateTime tempLock,
            @Param("id") Long id);
}
