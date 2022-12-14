package com.KeyWallet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.OffsetDateTime;

@Entity(name = "user_login")
@Table(name = "user_login")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @Column(name = "correct", nullable = false)
    private Integer correct;

    @Column(name = "id_user", nullable = false, length = 11)
    private Long idUser;

    @Column(name = "computer")
    private String computer;

    @Column(name = "session")
    private String session;

    @Column(name = "id_address", nullable = false)
    private BigInteger idAddress;

}

