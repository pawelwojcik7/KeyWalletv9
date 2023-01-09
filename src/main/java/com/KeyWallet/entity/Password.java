package com.KeyWallet.entity;

import com.KeyWallet.models.PasswordType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "password")
@Table(name = "password")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Password {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "id_user", nullable = false)
    private Long userId;

    @Column(name = "parent_password_id", nullable = true)
    private Long parentPasswordId;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "login", length = 30)
    private String login;

    @Column(name = "type", nullable = false)
    private PasswordType type;


}
