package com.KeyWallet.services;

import com.KeyWallet.entity.UserKW;
import com.KeyWallet.entity.UserLoginHistory;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.LoginResult;
import com.KeyWallet.repository.LoginHistoryRepository;
import com.KeyWallet.repository.UserRepository;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class LoginHistoryServiceTest {

    private LoginHistoryService historyService;
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
    }

    @ParameterizedTest
    @MethodSource("parametersProvider")
    public void test(LoginHistoryRepository repository, ExceptionMessages expectedErrorCode) {
        historyService = new LoginHistoryService(repository, userRepository);
        try {
            historyService.checkUserLoginAttemptCount(new UserKW());
        } catch (UserLogInException e) {
            Assertions.assertEquals(expectedErrorCode, ExceptionMessages.getByValue(e.getMessage()));
        }
    }


    private static Stream<Arguments> parametersProvider() {
        return Stream.of(
                Arguments.of(
                        initFirstRepository(),
                        ExceptionMessages.TWO_FAILED_USER_LOGINS
                ),
                Arguments.of(
                        initSecondRepository(),
                        ExceptionMessages.THREE_FAILED_USER_LOGINS
                ),
                Arguments.of(
                        initThirdRepository(),
                        ExceptionMessages.FOUR_FAILED_USER_LOGINS
                ),
                Arguments.of(
                        initFourthRepository(),
                        ExceptionMessages.FIVE_FAILED_USER_LOGINS
                )
        );
    }

    private static LoginHistoryRepository initFirstRepository() {
        LoginHistoryRepository historyRepository = Mockito.mock(LoginHistoryRepository.class);
        List<UserLoginHistory> loginHistories = IntStream.range(1, 3)
                .boxed()
                .map(v -> UserLoginHistory.builder()
                        .loginResult(LoginResult.FAILURE)
                        .loginDate(LocalDateTime.now().plusSeconds(v))
                        .build())
                .collect(Collectors.toList());
        when(historyRepository.findAllByUserKW(any())).thenReturn(loginHistories);
        return historyRepository;
    }

    private static LoginHistoryRepository initSecondRepository() {
        LoginHistoryRepository historyRepository = Mockito.mock(LoginHistoryRepository.class);
        List<UserLoginHistory> loginHistories = IntStream.range(1, 4)
                .boxed()
                .map(v -> UserLoginHistory.builder()
                        .loginResult(LoginResult.FAILURE)
                        .loginDate(LocalDateTime.now().plusSeconds(v))
                        .build())
                .collect(Collectors.toList());
        when(historyRepository.findAllByUserKW(any())).thenReturn(loginHistories);
        return historyRepository;
    }

    private static LoginHistoryRepository initThirdRepository() {
        LoginHistoryRepository historyRepository = Mockito.mock(LoginHistoryRepository.class);
        List<UserLoginHistory> loginHistories = IntStream.range(1, 5)
                .boxed()
                .map(v -> UserLoginHistory.builder()
                        .loginResult(LoginResult.FAILURE)
                        .loginDate(LocalDateTime.now().plusSeconds(v))
                        .build())
                .collect(Collectors.toList());
        when(historyRepository.findAllByUserKW(any())).thenReturn(loginHistories);
        return historyRepository;
    }

    private static LoginHistoryRepository initFourthRepository() {
        LoginHistoryRepository historyRepository = Mockito.mock(LoginHistoryRepository.class);
        List<UserLoginHistory> loginHistories = IntStream.range(1, 6)
                .boxed()
                .map(v -> UserLoginHistory.builder()
                        .loginResult(LoginResult.FAILURE)
                        .loginDate(LocalDateTime.now().plusSeconds(v))
                        .build())
                .collect(Collectors.toList());
        when(historyRepository.findAllByUserKW(any())).thenReturn(loginHistories);
        return historyRepository;
    }
}