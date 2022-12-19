package com.KeyWallet.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity(name = "userKW")
@Table(name = "userKW")
@Getter
@Setter
@NoArgsConstructor
@Builder
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


    public UserKW(Long id, String login, String passwordHash, String salt, Boolean isPasswordKeptAsHash) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.isPasswordKeptAsHash = isPasswordKeptAsHash;
    }

}
