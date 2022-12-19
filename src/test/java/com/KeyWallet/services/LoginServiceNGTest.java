package com.KeyWallet.services;


import com.KeyWallet.algorithms.AESenc;
import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.MD5;
import com.KeyWallet.algorithms.Sha512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.Pair;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.providers.SaltProvider;
import com.KeyWallet.repository.IpLoginHistoryRepository;
import com.KeyWallet.repository.LoginHistoryRepository;
import com.KeyWallet.repository.UserRepository;
import org.h2.security.SHA256;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginServiceNGTest {

    Sha512 sha512;

    private UserRepository userRepository;
    private UserService userService;

    @BeforeMethod
    public void setUp() {
        PepperProvider pepperProvider = Mockito.mock(PepperProvider.class);
        SaltProvider saltProvider =  Mockito.mock(SaltProvider.class);
        when(pepperProvider.getPepper()).thenReturn("pepper");
        sha512 = Mockito.mock(Sha512.class);
        HMAC hmac = new HMAC();
        MD5 md5 = new MD5();
        AESenc aeSenc = new AESenc(md5);
        userRepository = Mockito.mock(UserRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        LoginHistoryRepository historyRepository = Mockito.mock(LoginHistoryRepository.class);
        LoginHistoryService historyService = new LoginHistoryService(historyRepository, userRepository);
        IpLoginHistoryRepository ipLoginHistoryRepository = Mockito.mock(IpLoginHistoryRepository.class);
        IpLoginHistoryService ipLoginHistoryService = new IpLoginHistoryService(ipLoginHistoryRepository);
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
    }


    @Test(dataProvider = "provider", expectedExceptions = UserLogInException.class)
    public void unsuccessfulUserLogin_exceptionThrownExpected_whenIncorrectDataProvided(UserDTO userDTO, UserKW userKW) {
        when(userRepository.findByLogin(userDTO.getLogin())).thenReturn(userKW);
        when(sha512.encodeHashValue(any(String.class), anyString())).thenReturn(new Pair<>("password", "salt"));
        userService.loginUser(userDTO, "", "");
    }


    @DataProvider(name = "provider")
    private Object[][] dataProvider() {
        Object[][] data = new Object[3][2];
        UserDTO userDTO = initUserDTOBaseObject();
        UserKW userKW = initUserKWBaseObject();


        userDTO.setKeepPasswordAsHash(Boolean.TRUE);
        data[0][0] = userDTO;
        data[0][1] = null;


        userKW.setIsPasswordKeptAsHash(Boolean.TRUE);
        data[1][0] = userDTO;
        data[1][1] = userKW;


        userKW.setIsPasswordKeptAsHash(Boolean.FALSE);
        data[2][0] = userDTO;
        data[2][1] = userKW;

        return data;
    }


    private UserDTO initUserDTOBaseObject(){
        return UserDTO.builder()
                .login("testLogin")
                .password("testPasswd")
                .keepPasswordAsHash(Boolean.FALSE)
                .build();
    }

    private UserKW initUserKWBaseObject() {
        return UserKW.builder()
                .login("testLogin")
                .passwordHash("hash")
                .salt("salt")
                .isPasswordKeptAsHash(Boolean.FALSE)
                .build();
    }

}