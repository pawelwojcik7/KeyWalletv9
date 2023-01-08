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
import java.time.OffsetDateTime;

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
        checkUserPassword(userDTO.getPassword());

        if (userDTO.getKeepPasswordAsHash()) {
            registerUserWithHashPassword(userDTO);
        } else {
            registerUserWithoutHashPassword(userDTO);
        }
    }

    private void registerUserWithHashPassword(UserDTO userDTO) {

        UserKW userKW = userAfterSha512(userDTO);
        userKW.setPasswordHash(
                aeSenc.encrypt(
                        userKW.getPasswordHash(),
                        aeSenc.generateKey(
                                pepperProvider.getPepper()
                        )
                )
        );

        userRepository.save(userKW);
    }


    private void registerUserWithoutHashPassword(UserDTO userDTO) {

        UserKW userKW = userAfterSha512(userDTO);

        userKW.setPasswordHash(
                hmac.calculateHMAC(
                        userKW.getPasswordHash(),
                        pepperProvider.getPepper()
                )
        );

        userRepository.save(userKW);
    }

    private UserKW userAfterSha512(UserDTO userDTO){

        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), null);

        return new UserKW(
                null,
                userDTO.getLogin(),
                encryptedPasswordAndSalt.getLeft(),
                encryptedPasswordAndSalt.getRight(),
                userDTO.getKeepPasswordAsHash(),
                OffsetDateTime.now()
        );
    }

    private void checkIfUserExists(String login) {

        if (userRepository.existsByLogin(login)) {

            throw new UserRegisterException(ExceptionMessages.USER_ALREADY_EXIST.getCode());
        }

    }

    private void checkUserPassword(String password) {

        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"))

            throw new UserRegisterException(ExceptionMessages.WRONG_PASSWORD_FORMAT.getCode());
    }

}
