package com.KeyWallet.services;

import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.entity.UserLogin;
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
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;
    private final HMAC hmac;
    private final Sha512 sha512;
    private final AESenc aeSenc;
    private final IpAddressService ipAddressService;
    private final UserLoginService userLoginService;

    @Transactional(dontRollbackOn = {UserLogInException.class, IpAddressException.class})
    public void loginUser(UserDTO userDTO, HttpSession session, String ipAddress) throws UserLogInException, IpAddressException {

        ipAddressService.checkIfIpAddressIsNotBlocked(ipAddress); //sprawdzenie czy ip nie jest zablokowane / rzucamy wyjatek jezeli tak


        UserKW userKW = userRepository.findByLogin(userDTO.getLogin()); // szukamy czy uzytkownik istnieje


        if (Objects.isNull(userKW)) { // jezeli nie istnieje
            ipAddressService.badLoginFromIp(ipAddress, session.getId());
            throw new UserLogInException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode()); // wyjaek
        }


        //check(userKW.getId(), session, ipAddress);
        Pair<String, String> encryptedPasswordAndSalt = sha512.encodeHashValue(userDTO.getPassword(), userKW.getSalt());
        UserKW probablyUser = new UserKW(
                null, userDTO.getLogin(), encryptedPasswordAndSalt.getLeft(),
                encryptedPasswordAndSalt.getRight(), userDTO.getKeepPasswordAsHash(),OffsetDateTime.now()
        );
        if (userKW.getIsPasswordKeptAsHash()) {
            Key key = aeSenc.generateKey(pepperProvider.getPepper());
            probablyUser.setPasswordHash(aeSenc.encrypt(probablyUser.getPasswordHash(), key));
            if (!userKW.getPasswordHash().equals(probablyUser.getPasswordHash())) {
                userLoginService.registerBadLogin(userKW.getId(), session, ipAddress);
                throw new UserLogInException(ExceptionMessages.WRONG_PASSWORD.getCode());
            } else {
                ipAddressService.goodLoginFromIp(ipAddress, session.getId());
                userLoginService.registerCorrectLogin(userKW.getId(), session, ipAddress);
                List<UserLogin> userLogins = userLoginService.getSortedUserLogins(userKW.getId());
                if (userLogins.size() < 2) {
                    return;
                }
                if (userLogins.size() == 2) {
                    checkTwoLast(userLogins, userKW.getId(), session, ipAddress);
                }
                if (userLogins.size() == 3) {
                    checkThreeLast(userLogins, userKW.getId(), session, ipAddress);
                    checkTwoLast(userLogins, userKW.getId(), session, ipAddress);
                }
                if (userLogins.size() >= 4) {
                    checkFourLast(userLogins, userKW.getId(), session, ipAddress);
                    checkThreeLast(userLogins, userKW.getId(), session, ipAddress);
                    checkTwoLast(userLogins, userKW.getId(), session, ipAddress);
                }
            }
        } else {
            String hmacCodedIncomingUserPassword = hmac.calculateHMAC(probablyUser.getPasswordHash(), pepperProvider.getPepper());
            if (!userKW.getPasswordHash().equals(hmacCodedIncomingUserPassword)) {
                userLoginService.registerBadLogin(userKW.getId(), session, ipAddress);
                throw new UserLogInException(ExceptionMessages.WRONG_PASSWORD.getCode());
            } else {
                userLoginService.registerCorrectLogin(userKW.getId(), session, ipAddress);
                ipAddressService.goodLoginFromIp(ipAddress, session.getId());
                List<UserLogin> userLogins = userLoginService.getSortedUserLogins(userKW.getId());
                if (userLogins.size() < 2) {
                    return;
                }
                if (userLogins.size() == 2) {
                    checkTwoLast(userLogins, userKW.getId(), session, ipAddress);
                }
                if (userLogins.size() == 3) {
                    checkThreeLast(userLogins, userKW.getId(), session, ipAddress);
                    checkTwoLast(userLogins, userKW.getId(), session, ipAddress);
                }
                if (userLogins.size() >= 4) {
                    checkFourLast(userLogins, userKW.getId(), session, ipAddress);
                    checkThreeLast(userLogins, userKW.getId(), session, ipAddress);
                    checkTwoLast(userLogins, userKW.getId(), session, ipAddress);
                }
            }
        }

    }

    @Transactional(dontRollbackOn = {UserLogInException.class, IpAddressException.class})
    public void check(Long userId, HttpSession session, String ipAddress) throws UserLogInException {

        Optional<UserKW> optUser = userRepository.findById(userId);
        if (optUser.get().getLockoutTime().isAfter(OffsetDateTime.now())) {
            userLoginService.registerBadLogin(userId, session, ipAddress);
            throw new UserLogInException(ExceptionMessages.USER_BLOCKED.getCode() + optUser.get().getLockoutTime());
        }

    }

    @Transactional(dontRollbackOn = {UserLogInException.class, IpAddressException.class})
    public void checkTwoLast(List<UserLogin> userLogins, Long userId, HttpSession session, String ipAddress) {
        if (!userLogins.get(0).getCorrect() &&
                !userLogins.get(1).getCorrect()) {
            OffsetDateTime time = OffsetDateTime.now().plus(Duration.ofSeconds(5));
            userRepository.updateUserDataWithTempLock(time, userId);
            userLoginService.registerBadLogin(userId, session, ipAddress);
            throw new UserLogInException(ExceptionMessages.USER_BLOCKED.getCode() + time);
        }
    }

    @Transactional(dontRollbackOn = {UserLogInException.class, IpAddressException.class})
    public void checkThreeLast(List<UserLogin> userLogins, Long userId, HttpSession session, String ipAddress) {
        if (!userLogins.get(0).getCorrect() &&
                !userLogins.get(1).getCorrect() &&
                !userLogins.get(2).getCorrect()) {
            OffsetDateTime time = OffsetDateTime.now().plus(Duration.ofSeconds(10));
            userRepository.updateUserDataWithTempLock(time, userId);
            userLoginService.registerBadLogin(userId, session, ipAddress);
            throw new UserLogInException(ExceptionMessages.USER_BLOCKED.getCode() + time);
        }
    }

    @Transactional(dontRollbackOn = {UserLogInException.class, IpAddressException.class})
    public void checkFourLast(List<UserLogin> userLogins, Long userId, HttpSession session, String ipAddress) {
        if (!userLogins.get(0).getCorrect() &&
                !userLogins.get(1).getCorrect() &&
                !userLogins.get(2).getCorrect() &&
                !userLogins.get(3).getCorrect()) {
            OffsetDateTime time = OffsetDateTime.now().plus(Duration.ofMinutes(2));
            userRepository.updateUserDataWithTempLock(time, userId);
            userLoginService.registerBadLogin(userId, session, ipAddress);
            throw new UserLogInException(ExceptionMessages.USER_BLOCKED.getCode() + time);
        }
    }

}
