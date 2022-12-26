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
import java.security.Key;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final Sha512 sha512;
    private final AESenc aeSenc;
    private final IpAddressService ipAddressService;


    public void loginUser(UserDTO userDTO, HttpSession session, String ipAddress) throws UserLogInException, IpAddressException {
        UserKW userKW = userRepository.findByLogin(userDTO.getLogin()); // szukamy czy uzytkownik istnieje
        if (Objects.isNull(userKW)) { // jezeli nie istnieje
            // zarejestrowanie bad loginu dla adressu ip
            throw new UserLogInException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode()); // wyjaek
        }

        ipAddressService.checkIfIpAddressIsNotBlockedForUser(ipAddress, userKW.getId()); //sprawdzenie czy ip nie jest zablokowane dla użytkownika

        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), userKW.getSalt());
        UserKW probablyUser = new UserKW(null, userDTO.getLogin(), encryptedPasswordAndSalt.getLeft(), encryptedPasswordAndSalt.getRight(), userDTO.getKeepPasswordAsHash());
        if (userKW.getIsPasswordKeptAsHash()) {
            Key key = aeSenc.generateKey(pepperProvider.getPepper());
            probablyUser.setPasswordHash(aeSenc.encrypt(probablyUser.getPasswordHash(), key));
            if (!userKW.getPasswordHash().equals(probablyUser.getPasswordHash())) {
                ipAddressService.badLoginFromIp(ipAddress, session.getId(), userKW.getId());
                throw new UserLogInException(ExceptionMessages.WRONG_PASSWORD.getCode());
            }
            else {
                ipAddressService.goodLoginFromIp(ipAddress, session.getId(), userKW.getId());
            }
        } else {
            String hmacCodedIncomingUserPassword = hmac.calculateHMAC(probablyUser.getPasswordHash(), pepperProvider.getPepper());
            if (!userKW.getPasswordHash().equals(hmacCodedIncomingUserPassword)) {
                ipAddressService.badLoginFromIp(ipAddress, session.getId(), userKW.getId());
                ipAddressService.badLoginFromIp(ipAddress, session.getId(), userKW.getId());
                throw new UserLogInException(ExceptionMessages.WRONG_PASSWORD.getCode());
            }
            else{
                ipAddressService.goodLoginFromIp(ipAddress, session.getId(), userKW.getId());
            }
        }

    }

}
