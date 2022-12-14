package com.KeyWallet.api;

import com.KeyWallet.entity.Password;
import com.KeyWallet.exception.PasswordException;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.exception.UserRegisterException;
import com.KeyWallet.models.ChangePasswordDTO;
import com.KeyWallet.models.CryptResponse;
import com.KeyWallet.models.PasswordDTO;
import com.KeyWallet.models.UserDTO;
import com.KeyWallet.services.PasswordService;
import com.KeyWallet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class KeyWalletController {

    @SuppressWarnings("ConstantConditions")
    protected String fetchClientIpAddr() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = Optional.ofNullable(request.getHeader("X-FORWARDED-FOR")).orElse(request.getRemoteAddr());
        if (ip.equals("0:0:0:0:0:0:0:1")) ip = "127.0.0.1";
        Assert.isTrue(ip.chars().filter($ -> $ == '.').count() == 3, "Illegal IP: " + ip);
        return ip;
    }

    private final PasswordService passwordService;
    private final UserService userService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO user, HttpSession session) {

        try {
            userService.loginUser(user, session.getId(), fetchClientIpAddr());
            return ResponseEntity.ok().build();
        } catch (UserLogInException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        try {
            userService.registerUser(user);
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

        userService.changeMasterPassword(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/passwords/{userLogin}")
    public ResponseEntity<List<Password>> getAllPasswordsForUser(@PathVariable String userLogin) {

        return ResponseEntity.ok(passwordService.getPasswordsForUser(userLogin));
    }


}
