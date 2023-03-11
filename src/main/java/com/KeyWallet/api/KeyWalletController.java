package com.KeyWallet.api;

import com.KeyWallet.entity.Password;
import com.KeyWallet.exception.*;
import com.KeyWallet.models.*;
import com.KeyWallet.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class KeyWalletController {

    private final PasswordService passwordService;
    private final UserRegisterService userRegisterService;
    private final UserService userService;
    private final MasterPasswordService masterPasswordService;




    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO user) {

        try {
            userService.loginUser(user);
            return ResponseEntity.ok().build();
        } catch (UserLogInException | IpAddressException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {

        try {
            userRegisterService.registerUser(user);
            return ResponseEntity.ok().build();
        } catch (UserRegisterException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/add-password")
    public ResponseEntity<?> addPassword(@RequestBody PasswordDTO passwordDTO) {

        try {
            passwordService.addPassword(passwordDTO);
        } catch (PasswordException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/decrypt")
    public ResponseEntity<CryptResponse> decryptPassword(@RequestParam String masterPassword,
                                                         @RequestParam Long passwordId) {

        try {
            String decryptedPassword = passwordService.decryptPassword(masterPassword, passwordId);
            CryptResponse response = new CryptResponse(decryptedPassword);
            return ResponseEntity.ok(response);
        } catch (PasswordException e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/encrypt")
    public ResponseEntity<CryptResponse> encryptPassword(@RequestParam String masterPassword,
                                                         @RequestParam Long passwordId) {

        try {
            String decryptedPassword = passwordService.encryptPassword(masterPassword, passwordId);
            CryptResponse response = new CryptResponse(decryptedPassword);
            return ResponseEntity.ok(response);
        } catch (PasswordException e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/password/change")
    public ResponseEntity<?> chengeUserMasterPassword(@RequestBody ChangePasswordDTO changePasswordDTO) {

        try {
            masterPasswordService.changeMasterPassword(changePasswordDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/passwords/{userLogin}")
    public ResponseEntity<List<Password>> getAllPasswordsForUser(@PathVariable String userLogin) {

        return ResponseEntity.ok(passwordService.getPasswordsForUser(userLogin));
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/password/share")
    public ResponseEntity<?> sharePassword(@RequestBody SharePasswordDTO body) {

        try {
            passwordService.sharePassword(body);
        } catch (SharePasswordException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping ("/password/{id}")
    public ResponseEntity<?> deletePassword( @PathVariable Long id) {

        try {
            passwordService.deletePassword(id);
        } catch (SharePasswordException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }



}
