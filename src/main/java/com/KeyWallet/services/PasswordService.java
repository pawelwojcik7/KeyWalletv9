package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.entity.Password;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.PasswordException;
import com.KeyWallet.models.Pair;
import com.KeyWallet.models.PasswordDTO;
import com.KeyWallet.repository.PasswordRepository;
import com.KeyWallet.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;
    private final AESenc aeSenc;


    public List<Password> getPasswordsForUser(String userLogin){

        // find user id
        UserKW user = userRepository.findByLogin(userLogin);
        return passwordRepository.findAllByUserId(user.getId());
    }

    @Transactional
    public  void addPassword(PasswordDTO passwordDTO) throws PasswordException {

        UserKW userKW = userRepository.findByLogin(passwordDTO.getUserDTO().getLogin());

        if(userKW==null){
            throw new PasswordException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        }

        Key key = aeSenc.generateKey(passwordDTO.getUserDTO().getPassword());

        try {
            Password password = new Password(
                    null,
                    aeSenc.encrypt(passwordDTO.getPassword(), key),
                    userKW.getId(),
                    passwordDTO.getUrl(),
                    passwordDTO.getDescription(),
                    passwordDTO.getLogin()
            );

            passwordRepository.save(password);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void changeAllPasswordsForUser(Long userId, String oldMasterPassword, String newMasterPassword){

        List<Password> userPasswords = passwordRepository.findAllByUserId(userId);

        userPasswords.forEach(password -> {
            Key key = aeSenc.generateKey(oldMasterPassword);
            String userPassword = aeSenc.decrypt(password.getPassword(), key);
            key = aeSenc.generateKey(newMasterPassword);
            String ePassword = aeSenc.encrypt(userPassword, key);
            passwordRepository.updatePasswordDataWithNewPassword(ePassword, userId);

        });

    }

    public String decryptPassword(String masterPassword, Long passId) throws PasswordException{

        Optional<Password> passwordOptional = passwordRepository.findById(passId);

        if(passwordOptional.isPresent()){
            Password password = passwordOptional.get();
            Key key = aeSenc.generateKey(masterPassword);

            return  aeSenc.decrypt(password.getPassword(), key);
        }
        else{
            throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        }
    }

    public String encryptPassword(String masterPassword, Long passId) throws PasswordException{

        Optional<Password> passwordOptional = passwordRepository.findById(passId);

        if(passwordOptional.isPresent()){
            Password password = passwordOptional.get();

            return  password.getPassword();
        }
        else{
            throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        }
    }

}
