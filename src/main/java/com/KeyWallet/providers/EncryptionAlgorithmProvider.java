package com.KeyWallet.providers;

import com.KeyWallet.interfaces.EncryptAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EncryptionAlgorithmProvider {

    private final List<EncryptAlgorithm> encryptAlgorithmList;

    public Optional<EncryptAlgorithm> getEncryptionAlgorithm(String name){
        return encryptAlgorithmList
                .stream()
                .filter(encryptAlgorithm -> encryptAlgorithm.getName().equals(name))
                .findAny();
    }
}
