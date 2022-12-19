package com.KeyWallet.entity;

import com.KeyWallet.models.LoginResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class IpLoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ip_his_id;

    @Column(name = "ipAddress")
    private String ipAddress;

    @Column(name = "loginDate")
    private LocalDateTime loginDate;

    @Column(name = "loginResult")
    private LoginResult loginResult;
}
