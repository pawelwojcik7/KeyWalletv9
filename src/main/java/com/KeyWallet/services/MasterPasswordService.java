package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.models.ChangePasswordDTO;
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
public class MasterPasswordService {

    private final PasswordService passwordService;
    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final Sha512 sha512;
    private final AESenc aeSenc;

    @Transactional
    public void changeMasterPassword(ChangePasswordDTO changePasswordDTO) {

        UserKW existingUser = userRepository.findByLogin(changePasswordDTO.getLogin());

        UserDTO userDTO = new UserDTO(
                changePasswordDTO.getLogin(),
                changePasswordDTO.getNewPassword(),
                changePasswordDTO.getKeepAsHash()
        );

        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), null);
        UserKW newUser = new UserKW(null, userDTO.getLogin(), encryptedPasswordAndSalt.getLeft(), encryptedPasswordAndSalt.getRight(), userDTO.getKeepPasswordAsHash());

        if (changePasswordDTO.getKeepAsHash()) {
            Key key = aeSenc.generateKey(pepperProvider.getPepper());
            newUser.setPasswordHash(aeSenc.encrypt(newUser.getPasswordHash(), key));
            userRepository.updateUserDataWithNewPassword(
                    newUser.getIsPasswordKeptAsHash(),
                    newUser.getPasswordHash(),
                    newUser.getSalt(),
                    existingUser.getId()
            );
        } else {
            String newPassword = hmac.calculateHMAC(newUser.getPasswordHash(), pepperProvider.getPepper());
            userRepository.updateUserDataWithNewPassword(
                    newUser.getIsPasswordKeptAsHash(),
                    newPassword,
                    newUser.getSalt(),
                    existingUser.getId()
            );
        }

        passwordService.changeAllPasswordsForUser(
                existingUser.getId(),
                changePasswordDTO.getOldPassword(),
                changePasswordDTO.getNewPassword());

    }
}
