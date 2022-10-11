package com.KeyWallet.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "password")
@Table(name = "password")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Password {

    @Id
    @Column(name="id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="id_user", nullable = false)
    private Long userId;

    @Column(name="web_address")
    private String webAddress;

    @Column(name="description")
    private String description;

    @Column(name="login", length = 30)
    private String login;

}
