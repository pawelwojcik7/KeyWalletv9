package com.KeyWallet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;


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

    @Column(name = "sms_code", length = 6)
    private String smsCode;

    @Column(name = "code_timelife")
    private OffsetDateTime codeTimeLife;

    @Column(name = "security_question", length = 255)
    private String securityQuestion;

    @Column(name = "answer", length = 255)
    private String answer;

    @Column(name = "lockout_time")
    private OffsetDateTime lockoutTime;

    @Lob
    @Column(name = "session_id")
    private byte[] sessionId;

    @Column(name = "id_status")
    private Long idStatus;

    public UserKW(Long id, String login, String passwordHash, String salt, Boolean isPasswordKeptAsHash) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.isPasswordKeptAsHash = isPasswordKeptAsHash;
    }
}
