package com.KeyWallet.services;

import com.KeyWallet.entity.UserKW;
import com.KeyWallet.interfaces.EncryptAlgorithm;
import com.KeyWallet.models.UserCreateDto;
import com.KeyWallet.providers.EncryptionAlgorithmProvider;
import com.KeyWallet.providers.PepperProvider;
import com.KeyWallet.providers.SaltProvider;
import com.KeyWallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SaltProvider saltProvider;
    private final PepperProvider pepperProvider;
    private final EncryptionAlgorithmProvider algorithmProvider;
    private final UserRepository userRepository;

    public Optional<UserKW> findById(long id){
        return userRepository.findById(id);
    }

    public UserKW save(UserCreateDto userCreateDto){

        EncryptAlgorithm encryptAlgorithm;
        UserKW user = new UserKW();
        String salt = saltProvider.getSalt();
        user.setSalt(salt);

        Optional<EncryptAlgorithm> optional = algorithmProvider.getEncryptionAlgorithm(userCreateDto.getAlgorithmType());
        if(optional.isPresent()) encryptAlgorithm = optional.get();
        else encryptAlgorithm = algorithmProvider.getEncryptionAlgorithm("SSH512").get();

        String password = encryptAlgorithm
                .encrypt(userCreateDto.getPassword(),
                        pepperProvider.getPepper(),
                        salt);
        user.setPasswordHash(password);
        user.setLogin(userCreateDto.getLogin());
        if(encryptAlgorithm.getName().equals("SSH512")) user.setIsPasswordKeptAsHash(true);
        else user.setIsPasswordKeptAsHash(false);

        return userRepository.save(user);
    }

}
