package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IpAddressBlockedDTO {
    private String ipAddress;
    private boolean isBlocked;
}
