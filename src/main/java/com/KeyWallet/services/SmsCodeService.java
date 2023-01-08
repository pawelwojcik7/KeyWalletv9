package com.KeyWallet.services;

import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.SmsCodeException;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.UserStatus;
import com.KeyWallet.providers.SmsCodeProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsCodeService {

    //zmiana kodu dla usera o danym loginie
    //weryfikacja kodu
    //zapisanie kodu

    private final UserRepository userRepository;
    private final SmsCodeProvider smsCodeProvider;

    @Transactional
    public void changeCode(String login) throws SmsCodeException {
        UserKW userKW = userRepository.findByLogin(login);

        if (Objects.isNull(userKW)) {
            throw new SmsCodeException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        } else {
            Integer smsCode = smsCodeProvider.generateSmsCode();
            OffsetDateTime now = OffsetDateTime.now().plus(Duration.ofMinutes(10));
            log.info("New SMS Code for user " + userKW.getLogin() + ": " + smsCode + ". Code is valid until: " + now);
            userRepository.updateUserDataWithNewSmsCode(smsCode, now, login);
        }
    }

    public void verify(Integer smsCode, String userLogin) throws SmsCodeException {
        UserKW userKW = userRepository.findByLogin(userLogin);
        if (Objects.isNull(userKW)) {
            throw new SmsCodeException(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        } else {
            if (Objects.equals(smsCode, userKW.getSmsCode())) {
                userRepository.updateUserDataWithStatus(UserStatus.VERIFIED, userLogin);
            } else throw new SmsCodeException(ExceptionMessages.INVALID_SMS_CODE.getCode());
        }

    }
}
