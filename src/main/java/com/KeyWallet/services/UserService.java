package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.exception.UserRegisterException;
import com.KeyWallet.models.ChangePasswordDTO;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Key;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordService passwordService;
    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final Sha512 sha512;
    private final AESenc aeSenc;

    public void loginUser(UserDTO userDTO) throws UserLogInException {
        UserKW userKW = userRepository.findByLogin(userDTO.getLogin());
        if(Objects.isNull(userKW)){
            throw new UserLogInException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        }
        UserKW probablyUser = sha512.encodeHashValue(userDTO, userKW.getSalt());
        if (userKW.getIsPasswordKeptAsHash()) {
            Key key = aeSenc.generateKey(pepperProvider.getPepper());
            probablyUser.setPasswordHash(aeSenc.encrypt(probablyUser.getPasswordHash(), key));
            if (!userKW.getPasswordHash().equals(probablyUser.getPasswordHash())) {

                throw new UserLogInException(ExceptionMessages.WRONG_PASSWORD.getCode());
            }
        } else {
            String hmacCodedIncomingUserPassword = hmac.calculateHMAC(probablyUser.getPasswordHash(), pepperProvider.getPepper());
            if (!userKW.getPasswordHash().equals(hmacCodedIncomingUserPassword)) {
                throw new UserLogInException(ExceptionMessages.WRONG_PASSWORD.getCode());
            }
        }
    }

    @Transactional
    public void registerUser(UserDTO userDTO) {

        checkIfUserExists(userDTO.getLogin());
        if(userDTO.getKeepPasswordAsHash()){
            registerUserWithHashPassword(userDTO);
        } else {
            registerUserWithoutHashPassword(userDTO);
        }
    }

    private void registerUserWithHashPassword(UserDTO userDTO){

        UserKW userKW = sha512.encodeHashValue(userDTO, null);
        Key key = aeSenc.generateKey(pepperProvider.getPepper());
        userKW.setPasswordHash(aeSenc.encrypt(userKW.getPasswordHash(), key));

        userRepository.save(userKW);
    }

    @Transactional
    public void changeMasterPassword(ChangePasswordDTO changePasswordDTO){

        UserKW existingUser = userRepository.findByLogin(changePasswordDTO.getLogin());

        UserDTO userDTO = new UserDTO(
                changePasswordDTO.getLogin(),
                changePasswordDTO.getNewPassword(),
                changePasswordDTO.getKeepAsHash()
        );

        UserKW newUser = sha512.encodeHashValue(userDTO, null);

        if(changePasswordDTO.getKeepAsHash()){
            Key key = aeSenc.generateKey(pepperProvider.getPepper());
            newUser.setPasswordHash(aeSenc.encrypt(newUser.getPasswordHash(),key));
            userRepository.updateUserDataWithNewPassword(
                    newUser.getIsPasswordKeptAsHash(),
                    newUser.getPasswordHash(),
                    newUser.getSalt(),
                    newUser.getId()
            );
        }
        else{
            String newPassword = hmac.calculateHMAC(newUser.getPasswordHash(), pepperProvider.getPepper());
            userRepository.updateUserDataWithNewPassword(
                    newUser.getIsPasswordKeptAsHash(),
                    newPassword,
                    newUser.getSalt(),
                    newUser.getId()
            );
        }

        passwordService.changeAllPasswordsForUser(
                existingUser.getId(),
                changePasswordDTO.getOldPassword(),
                changePasswordDTO.getNewPassword());

    }

    private void registerUserWithoutHashPassword(UserDTO userDTO) {

        UserKW userKW = sha512.encodeHashValue(userDTO, null);
        userKW.setPasswordHash(hmac.calculateHMAC(userKW.getPasswordHash(), pepperProvider.getPepper()));

        userRepository.save(userKW);
    }


    private void checkIfUserExists(String login) {
        if(userRepository.existsByLogin(login)){
            throw new UserRegisterException(ExceptionMessages.USER_ALREADY_EXIST.getCode());
        }

    }


}
