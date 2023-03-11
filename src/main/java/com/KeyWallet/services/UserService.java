package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.IpAddressException;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.Pair;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final Sha512 sha512;
    private final AESenc aeSenc;

    @Transactional(dontRollbackOn = {UserLogInException.class, IpAddressException.class})
    public void loginUser(UserDTO userDTO) throws UserLogInException, IpAddressException {

        UserKW userKW = userRepository.findByLogin(userDTO.getLogin());
        if (Objects.isNull(userKW)) {
            throw new UserLogInException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode()); // wyjaek
        }


        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), userKW.getSalt());
        UserKW probablyUser = new UserKW(
                null, userDTO.getLogin(), encryptedPasswordAndSalt.getLeft(),
                encryptedPasswordAndSalt.getRight(), userDTO.getKeepPasswordAsHash(), OffsetDateTime.now()
        );
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

}





