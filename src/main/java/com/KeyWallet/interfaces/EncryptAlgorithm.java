package com.KeyWallet.interfaces;

public interface EncryptAlgorithm {

    String getName();
    String encrypt(String text, String pepper, String salt);

}
