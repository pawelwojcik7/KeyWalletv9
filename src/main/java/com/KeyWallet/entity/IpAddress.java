package com.KeyWallet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.OffsetDateTime;

@Entity(name = "ip_address")
@Table(name = "ip_address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ok_login_num", nullable = false)
    private Integer okLoginNum;

    @Column(name = "bad_login_num", nullable = false)
    private Integer badLoginNum;

    @Column(name = "last_bad_login_num", nullable = false)
    private Integer lastBadLoginNum;

    @Column(name = "permanent_lock", nullable = false)
    private Boolean permanentLock;

    @Column(name = "temp_lock")
    private OffsetDateTime tempLock;

    @Column(name="userId", nullable = false)
    private Long userId;

    @Column(name = "ipAddress", nullable = false)
    private String ipAddress;

}
