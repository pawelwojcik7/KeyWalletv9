package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.entity.Password;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.PasswordException;
import com.KeyWallet.exception.SharePasswordException;
import com.KeyWallet.models.Pair;
import com.KeyWallet.models.PasswordDTO;
import com.KeyWallet.models.PasswordType;
import com.KeyWallet.models.SharePasswordDTO;
import com.KeyWallet.repository.PasswordRepository;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;
    private final AESenc aeSenc;

    private final String keyToSharedPasswords = "hwer82whfiuqedbnfiqoeubfgehggvqeaADFSDFbgvdqahrgbqdsf9bvhuepq985tgy";

    public void sharePassword(SharePasswordDTO dto) throws SharePasswordException {

        Optional<Password> passwordOptional = passwordRepository.findById(dto.getPasswordId());
        if (passwordOptional.isEmpty()) throw new SharePasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        Password password = passwordOptional.get();

        if(password.getParentPasswordId()!=null) throw new SharePasswordException("You can't share this password");

        Optional<UserKW> ownerOptional = userRepository.findById(password.getUserId());
        if (ownerOptional.isEmpty()) throw new SharePasswordException("Owner doesn't exist");
        UserKW owner = ownerOptional.get();

        UserKW shareToWho = userRepository.findByLogin(dto.getLogin());
        if (shareToWho == null) throw new SharePasswordException("User to share doesn't exist");

        Key key = aeSenc.generateKey(dto.getMasterPassword());
        Key keyToShare = aeSenc.generateKey(keyToSharedPasswords);
        String passwordString = aeSenc.decrypt(password.getPassword(), key);
        String finalPasswordString = aeSenc.encrypt(passwordString, keyToShare);

        Password passwordToShare = new Password(null,
                finalPasswordString,
                shareToWho.getId(),
                password.getId(),
                password.getUrl(),
                password.getDescription(),
                password.getLogin(),
                PasswordType.SHARED_TO_ME
        );

        passwordRepository.save(passwordToShare);


    }

    public List<Password> getPasswordsForUser(String userLogin) {

        // find user id
        UserKW user = userRepository.findByLogin(userLogin);
        List<Password> myMainPasswords = passwordRepository.findAllByUserId(user.getId());
        List<Password> mySharedPasswords = new ArrayList<>();
        List<Long> myPasswordsId = myMainPasswords.stream().map(Password::getId).collect(Collectors.toList());
        for(Long id: myPasswordsId){
           mySharedPasswords.addAll(passwordRepository.findAllByParentPasswordId(id));
        }
       mySharedPasswords.forEach(password -> {password.setType(PasswordType.SHARED_BY_ME);});
        myMainPasswords.addAll(mySharedPasswords);
        return  myMainPasswords;
    }

    @Transactional
    public void addPassword(PasswordDTO passwordDTO) throws PasswordException {

        UserKW userKW = userRepository.findByLogin(passwordDTO.getUserDTO().getLogin());

        if (userKW == null) {
            throw new PasswordException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        }

        Key key = aeSenc.generateKey(passwordDTO.getUserDTO().getPassword());

        try {
            Password password = new Password(
                    null,
                    aeSenc.encrypt(passwordDTO.getPassword(), key),
                    userKW.getId(),
                    null,
                    passwordDTO.getUrl(),
                    passwordDTO.getDescription(),
                    passwordDTO.getLogin(),
                    PasswordType.MY
            );

            passwordRepository.save(password);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void changeAllPasswordsForUser(Long userId, String oldMasterPassword, String newMasterPassword) {

        List<Password> userPasswords = passwordRepository.findAllByUserId(userId);

        userPasswords
                .stream()
                .map(userPassword -> {
                    Key key = aeSenc.generateKey(oldMasterPassword);
                    String password = aeSenc.decrypt(userPassword.getPassword(), key);
                    return new Pair<Long, String>(userPassword.getId(), password);
                })
                .forEach(pair -> {
                    Key key = aeSenc.generateKey(newMasterPassword);
                    String encryptedPassword = aeSenc.encrypt(pair.getRight(), key);
                    passwordRepository.updatePasswordDataWithNewPassword(encryptedPassword, pair.getLeft());
                });

    }

    public String decryptPassword(String masterPassword, Long passId) throws PasswordException {

        Optional<Password> passwordOptional = passwordRepository.findById(passId);

        if (passwordOptional.isPresent()) {
            Password password = passwordOptional.get();
            Key key;
            if (password.getType().equals(PasswordType.SHARED_TO_ME)){
                key = aeSenc.generateKey(keyToSharedPasswords);
            }else {
               key =  aeSenc.generateKey(masterPassword);
            }
            return aeSenc.decrypt(password.getPassword(), key);
        } else {
            throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        }
    }

    public String encryptPassword(String masterPassword, Long passId) throws PasswordException {

        Optional<Password> passwordOptional = passwordRepository.findById(passId);

        if (passwordOptional.isPresent()) {
            Password password = passwordOptional.get();

            return password.getPassword();
        } else {
            throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        }
    }

    public void deletePassword(Long passwordId) {
        Optional<Password> passwordOptional = passwordRepository.findById(passwordId);
        if(passwordOptional.isEmpty()) throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        Password password = passwordOptional.get();
        passwordRepository.delete(password);
        List<Password> childs = passwordRepository.findAllByParentPasswordId(passwordId);
        if(!childs.isEmpty()){
            passwordRepository.deleteAll(childs);
        }

    }
}
