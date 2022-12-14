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

import java.net.http.HttpRequest;
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


    public List<Password> getPasswordsForUser(String userLogin) {

        // find user id
        UserKW user = userRepository.findByLogin(userLogin);
        return passwordRepository.findAllByUserId(user.getId());
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
                    passwordDTO.getUrl(),
                    passwordDTO.getDescription(),
                    passwordDTO.getLogin()
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


    private HttpRequest factory(String method, String body){
        HttpRequest request;
        switch (method){
            case "post": request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body)).header("accept", "application/json").build();
            break;
            case "get": request = HttpRequest.newBuilder().GET().header("accept", "application/json").build();
            default: request = HttpRequest.newBuilder().GET().header("accept", "application/json").build();
        }
        return request;
    }

    public String decryptPassword(String masterPassword, Long passId) throws PasswordException {

        Optional<Password> passwordOptional = passwordRepository.findById(passId);

        if (passwordOptional.isPresent()) {
            Password password = passwordOptional.get();
            Key key = aeSenc.generateKey(masterPassword);

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

}
