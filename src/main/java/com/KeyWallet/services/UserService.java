package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.SSH512;

import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.exception.UserRegisterException;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.providers.SaltProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Key;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SaltProvider saltProvider;
    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final SSH512 ssh512;
    private final AESenc aeSenc;

    public void loginUser(UserDTO userDTO) throws UserLogInException {
        UserKW user = userRepository.findByLogin(userDTO.getLogin());
        if(Objects.isNull(user)){
            throw new UserLogInException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        }
        UserKW incomingUser = ssh512.encodeHashValue(userDTO, user.getSalt());
        if (userKW.getIsPasswordKeptAsHash()) {
            Key key = aesCodingService.generateKey(configuration.getPepper());
            incomingUser.setPasswordHash(aesCodingService.encrypt(incomingUser.getPasswordHash(), key));
            if (!userKW.getPasswordHash().equals(incomingUser.getPasswordHash())) {
                historyService.storeLoginAttempt(userKW, LoginResult.FAILURE);
                ipLoginHistoryService.storeIpLoginData(ipAddress, LoginResult.FAILURE);
                throw new UserLogInException(ErrorCodes.WRONG_PASSWORD.getCode());
            }
        } else {
            String hmacCodedIncomingUserPassword = hmacCodingService.calculateHMAC(incomingUser.getPasswordHash(), configuration.getPepper());
            if (!userKW.getPasswordHash().equals(hmacCodedIncomingUserPassword)) {
                historyService.storeLoginAttempt(userKW, LoginResult.FAILURE);
                ipLoginHistoryService.storeIpLoginData(ipAddress, LoginResult.FAILURE);
                throw new UserLogInException(ErrorCodes.WRONG_PASSWORD.getCode());
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

        UserKW userKW = ssh512.encodeHashValue(userDTO);
        Key key = aeSenc.generateKey(pepperProvider.getPepper());
        userKW.setPasswordHash(aeSenc.encrypt(userKW.getPasswordHash(), key));

        userRepository.save(userKW);
    }

    private void registerUserWithoutHashPassword(UserDTO userDTO) {

        UserKW userKW = ssh512.encodeHashValue(userDTO);
        userKW.setPasswordHash(hmac.calculateHMAC(userKW.getPasswordHash(), pepperProvider.getPepper()));

        userRepository.save(userKW);
    }


    private void checkIfUserExists(String login) {
        if(userRepository.existsByLogin(login)){
            throw new UserRegisterException(ExceptionMessages.USER_ALREADY_EXIST.getCode());
        }
    }
    

}
