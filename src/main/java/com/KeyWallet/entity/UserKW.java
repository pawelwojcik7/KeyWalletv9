package com.KeyWallet.entity;

import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity(name = "userKW")
@Table(name = "userKW")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserKW {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", length = 30, nullable = false)
    private String login;

    @Column(name = "password_hash", length = 512, nullable = false)
    private String passwordHash;

    @Column(name = "salt", length = 36)
    private String salt;

    @Column(name = "isPasswordKeptAsHash")
    private Boolean isPasswordKeptAsHash;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userId")
    private List<Password> passwords;

}
