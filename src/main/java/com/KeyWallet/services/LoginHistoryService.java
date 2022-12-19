package com.KeyWallet.services;


import com.KeyWallet.entity.UserKW;
import com.KeyWallet.entity.UserLoginHistory;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.LastLoginDataDTO;
import com.KeyWallet.models.LoginResult;
import com.KeyWallet.repository.LoginHistoryRepository;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public void storeLoginAttempt(UserKW UserKW, LoginResult loginResult) {
        UserLoginHistory loginHistoryRecord = UserLoginHistory.builder()
                .loginResult(loginResult)
                .userKW(UserKW)
                .loginDate(LocalDateTime.now())
                .build();

        historyRepository.save(loginHistoryRecord);
    }


    public void checkUserLoginAttemptCount(UserKW UserKW) {
        List<UserLoginHistory> userLoginHistories = historyRepository.findAllByUserKW(UserKW).stream()
                .sorted(Comparator.comparing(UserLoginHistory::getLoginDate).reversed())
                .collect(Collectors.toList());

        Optional<UserLoginHistory> lastSuccessfulLogin = userLoginHistories.stream()
                .filter(history -> LoginResult.SUCCESS.equals(history.getLoginResult()))
                .findFirst();
        int failedLoginAttempts = 0;
        if (lastSuccessfulLogin.isPresent()) {
            UserLoginHistory lastSuccessLoginInHistory = lastSuccessfulLogin.get();
            failedLoginAttempts = userLoginHistories.size() - (userLoginHistories.size() - userLoginHistories.indexOf(lastSuccessLoginInHistory));
        } else {
            failedLoginAttempts = userLoginHistories.size();
        }
        switch (failedLoginAttempts) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                processDoubleLoginFailedAttempt(userLoginHistories.get(0));
                break;
            case 3:
                processTripleLoginFailedAttempt(userLoginHistories.get(0));
                break;
            case 4:
                processFourthLoginFailedAttempts(userLoginHistories.get(0));
                break;
            default:
                processFifthAndMoreLoginFailedAttempts(userLoginHistories.get(0));
                break;
        }
    }

    private void processDoubleLoginFailedAttempt(UserLoginHistory lastFailedLogin) {
        if (lastFailedLogin.getLoginDate().plusSeconds(5L).isAfter(LocalDateTime.now())) {
            throw new UserLogInException(ExceptionMessages.TWO_FAILED_USER_LOGINS.getCode());
        }
    }

    private void processTripleLoginFailedAttempt(UserLoginHistory lastFailedLogin) {
        if (lastFailedLogin.getLoginDate().plusSeconds(10L).isAfter(LocalDateTime.now())) {
            throw new UserLogInException(ExceptionMessages.THREE_FAILED_USER_LOGINS.getCode());
        }
    }

    private void processFourthLoginFailedAttempts(UserLoginHistory lastFailedLogin) {
        if (lastFailedLogin.getLoginDate().plusMinutes(2L).isAfter(LocalDateTime.now())) {
            throw new UserLogInException(ExceptionMessages.FOUR_FAILED_USER_LOGINS.getCode());
        }
    }

    private void processFifthAndMoreLoginFailedAttempts(UserLoginHistory lastFailedLogin) {
        if(lastFailedLogin.getLoginDate().plusMinutes(30L).isAfter(LocalDateTime.now())) {
            throw new UserLogInException(ExceptionMessages.FIVE_FAILED_USER_LOGINS.getCode());
        }
    }


    public LastLoginDataDTO getLastLoginDataForUser(String login) {
        UserKW UserKW = userRepository.findByLogin(login);

        List<UserLoginHistory> userLoginHistories = historyRepository.findAllByUserKW(UserKW).stream()
                .sorted(Comparator.comparing(UserLoginHistory::getLoginDate).reversed())
                .collect(Collectors.toList());

        Optional<LocalDateTime> lastSuccessfulLogin = userLoginHistories.stream()
                .filter(history -> LoginResult.SUCCESS.equals(history.getLoginResult()))
                .findFirst()
                .map(UserLoginHistory::getLoginDate);

        Optional<LocalDateTime> lastFailedLogin = userLoginHistories.stream()
                .filter(history -> LoginResult.FAILURE.equals(history.getLoginResult()))
                .findFirst()
                .map(UserLoginHistory::getLoginDate);


        return new LastLoginDataDTO(
                lastSuccessfulLogin.orElse(null),
                lastFailedLogin.orElse(null)
        );
    }
}
