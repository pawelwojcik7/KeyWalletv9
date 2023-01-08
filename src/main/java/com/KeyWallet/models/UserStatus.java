package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserStatus {
    UNVERIFIED("UNVERFIED"),
    LOGGED_VERIFIED("VERIFIED"),
    LOGGED_OUT_VERIFIED("VERIFIED"),
    VERIFIED("VERIFIED"),
    BLOCKED("VERIFIED"),
    BLOCKED_UNVERIFIED("UNVERIFIED"),
    LOGGED_UNVERIFIED("UNVERIFIED"),
    LOGED_OUT_UNVERIFIED("UNVERIFIED");

    @Getter
    public final String code;
}
