package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LastLoginDataDTO {
    private LocalDateTime lastSuccessLogin;
    private LocalDateTime lastFailedLogin;
}
