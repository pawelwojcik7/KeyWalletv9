package com.KeyWallet.services;

import com.KeyWallet.algorithms.HMAC;
import com.KeyWallet.algorithms.SSH512;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.models.UserCreateRequest;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.providers.SaltProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.desktop.ScreenSleepEvent;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SaltProvider saltProvider;
    private final PepperProvider pepperProvider;
    private final UserRepository userRepository;

    private final HMAC hmac;

    private final SSH512 ssh512;

    public Optional<UserKW> findById(long id){
        return userRepository.findById(id);
    }

    public UserKW save(UserCreateRequest userCreateRequest){

        UserKW user = new UserKW();
        String salt = saltProvider.getSalt();
        user.setSalt(salt);
        String password;
        if(userCreateRequest.getKeepAsHash()) {
            password = hmac
                    .encrypt(userCreateRequest.getPassword(),
                            pepperProvider.getPepper(),
                            salt);
        }
        else{
            password = ssh512
                    .encrypt(userCreateRequest.getPassword(),
                            pepperProvider.getPepper(),
                            salt);
        }
        user.setPasswordHash(password);
        user.setLogin(userCreateRequest.getLogin());
        user.setIsPasswordKeptAsHash(userCreateRequest.getKeepAsHash());

        return userRepository.save(user);
    }

}
