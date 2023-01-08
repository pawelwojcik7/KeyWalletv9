package com.KeyWallet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.OffsetDateTime;

@Entity(name ="incorrect_logins")
@Table(name = "incorrect_logins")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IncorrectLogins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "id_address", nullable = false)
    private Long idAddress;

}
