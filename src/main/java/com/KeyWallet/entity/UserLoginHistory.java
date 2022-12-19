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
public class UserLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long his_id;

    @ManyToOne
    @JoinColumn(name = "id")
    private UserKW userKW;

    @Column(name = "loginDate")
    private LocalDateTime loginDate;

    @Column(name = "loginResult")
    private LoginResult loginResult;
}
