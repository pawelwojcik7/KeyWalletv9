package com.KeyWallet.services;

import com.KeyWallet.entity.Password;
import com.KeyWallet.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;
    public List<Password> getPasswordForUser(Long userId){

        return passwordRepository.findAllByuserId(userId);
    }

}
