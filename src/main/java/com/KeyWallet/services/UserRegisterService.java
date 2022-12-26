package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.UserRegisterException;
import com.KeyWallet.models.Pair;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Key;

@Service
@RequiredArgsConstructor
public class UserRegisterService {

    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final Sha512 sha512;
    private final AESenc aeSenc;


    @Transactional
    public void registerUser(UserDTO userDTO) {

        checkIfUserExists(userDTO.getLogin());
        if (userDTO.getKeepPasswordAsHash()) {
            registerUserWithHashPassword(userDTO);
        } else {
            registerUserWithoutHashPassword(userDTO);
        }
    }

    private void registerUserWithHashPassword(UserDTO userDTO) {

        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), null);
        UserKW userKW = new UserKW(null, userDTO.getLogin(), encryptedPasswordAndSalt.getLeft(), encryptedPasswordAndSalt.getRight(), userDTO.getKeepPasswordAsHash());
        Key key = aeSenc.generateKey(pepperProvider.getPepper());
        userKW.setPasswordHash(aeSenc.encrypt(userKW.getPasswordHash(), key));

        userRepository.save(userKW);
    }


    private void registerUserWithoutHashPassword(UserDTO userDTO) {

        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), null);
        UserKW userKW = new UserKW(null, userDTO.getLogin(), encryptedPasswordAndSalt.getLeft(), encryptedPasswordAndSalt.getRight(), userDTO.getKeepPasswordAsHash());
        userKW.setPasswordHash(hmac.calculateHMAC(userKW.getPasswordHash(), pepperProvider.getPepper()));

        userRepository.save(userKW);
    }


    private void checkIfUserExists(String login) {

        if (userRepository.existsByLogin(login)) {
            throw new UserRegisterException(ExceptionMessages.USER_ALREADY_EXIST.getCode());
        }

    }


}
