package com.KeyWallet.api;

import com.KeyWallet.entity.Password;
import com.KeyWallet.models.UserCreateRequest;
import com.KeyWallet.services.PasswordService;
import com.KeyWallet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KeyWalletController {

    private final PasswordService passwordService;
    private final UserService userService;


    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest user){

        return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping("/passwords/{userId}")
    public ResponseEntity<List<Password>> getPasswordsForUser(@PathVariable Long userId){

        return ResponseEntity.ok(passwordService.getPasswordForUser(userId));
    }

}
