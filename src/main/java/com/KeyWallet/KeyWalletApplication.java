package com.KeyWallet;

import com.KeyWallet.entity.Password;
import com.KeyWallet.entity.UserKW;
import com.KeyWallet.repository.PasswordRepository;
import com.KeyWallet.repository.UserRepository;
import com.KeyWallet.services.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class KeyWalletApplication {

	public static void main(String[] args) {
	ConfigurableApplicationContext context =  SpringApplication.run(KeyWalletApplication.class, args);
//		PasswordRepository passwordRepository = context.getBean(PasswordRepository.class);
//		UserRepository userRepository = context.getBean(UserRepository.class);
//		userRepository.save(new UserKW(1L, "login", "hash", "salt", false, List.of()));
//		passwordRepository.save(new Password(1L, "asd", 1L, "asd", "", ""));

	}

}
