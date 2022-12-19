package com.KeyWallet.services;


import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.repository.IpLoginHistoryRepository;
import com.KeyWallet.repository.LoginHistoryRepository;
import com.KeyWallet.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class LoginServiceTest {

    @Autowired
    private PepperProvider pepperProvider;

    @Autowired
    private Sha512 sha512;

    @Autowired
    private HMAC hmac;

    @Autowired
    private AESenc aeSenc;

    private UserRepository userRepository;
    private UserDTO testData;
    private UserKW testDataFromDB;

    private UserService userService;

    @Before
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        LoginHistoryRepository historyRepository = Mockito.mock(LoginHistoryRepository.class);
        LoginHistoryService historyService = new LoginHistoryService(historyRepository, userRepository);
        IpLoginHistoryRepository ipLoginHistoryRepository = Mockito.mock(IpLoginHistoryRepository.class);
        IpLoginHistoryService ipLoginHistoryService = new IpLoginHistoryService(ipLoginHistoryRepository);
        PasswordService passwordService = Mockito.mock(PasswordService.class);
        userService = new UserService(
                passwordService,
                pepperProvider,
                userRepository,
                hmac,
                sha512,
                aeSenc,
                historyService,
                ipLoginHistoryService
        );
        testData = new UserDTO(
                "test",
                "test1",
                true
        );
        testDataFromDB = new UserKW();
        testDataFromDB.setLogin(testData.getLogin());
        testDataFromDB.setPasswordHash("test");
    }

    @Test(expected = UserLogInException.class)
    public void checkUserCredentials_expectedUserDoesNotExist_whenUserNotFoundInDB() {
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        userService.loginUser(testData, "", "");
    }


    @Test(expected = UserLogInException.class)
    public void checkUserCredentials_wrongPasswordException_whenUserWrongPasswordProvidedAndKeptAsHAsh() {
        testDataFromDB.setIsPasswordKeptAsHash(Boolean.TRUE);
        when(userRepository.findByLogin(testData.getLogin())).thenReturn(testDataFromDB);
        userService.loginUser(testData, "", "");
    }

    @Test(expected = UserLogInException.class)
    public void checkUserCredentials_wrongPasswordException_whenUserWrongPasswordProvidedAndKeptNotAsHash() {
        testDataFromDB.setIsPasswordKeptAsHash(Boolean.FALSE);
        when(userRepository.findByLogin(testData.getLogin())).thenReturn(testDataFromDB);
        userService.loginUser(testData, "", "");
    }

}