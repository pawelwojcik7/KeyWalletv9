package com.KeyWallet.services;

import com.KeyWallet.entity.IpLoginHistory;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.LoginResult;
import com.KeyWallet.repository.IpLoginHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IpLoginHistoryServiceTest {

    private IpLoginHistoryService historyService;
    private static final String ipAddress = "172.168.1.1";

    @Before
    public void setUp() {
        IpLoginHistoryRepository repository = Mockito.mock(IpLoginHistoryRepository.class);
        prepareMock(repository);
        historyService = new IpLoginHistoryService(repository);
    }

    @Test(expected = UserLogInException.class)
    public void loginFromIp_ExceptionAboutIpBlockadeThrown_WhenSequenceOf20FailedLoginAttemptsProvided() {
        historyService.checkIpLoginAttemptCount(ipAddress);
    }

    private void prepareMock(IpLoginHistoryRepository repository) {
        List<IpLoginHistory> loginHistoryList = IntStream.range(1, 21)
                .boxed()
                .map(val -> IpLoginHistory.builder()
                        .loginResult(LoginResult.FAILURE)
                        .ipAddress(ipAddress)
                        .loginDate(LocalDateTime.now().plusSeconds(val.longValue()))
                        .build())
                .collect(Collectors.toList());

        Mockito.when(repository.findAllByIpAddress(ipAddress)).thenReturn(loginHistoryList);
    }
}