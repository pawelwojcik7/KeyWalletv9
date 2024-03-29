package com.KeyWallet.api;

import com.KeyWallet.entity.IpAddress;
import com.KeyWallet.entity.Password;
import com.KeyWallet.entity.UserLogin;
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
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class KeyWalletController {

    private final PasswordService passwordService;
    private final UserRegisterService userRegisterService;
    private final UserService userService;
    private final MasterPasswordService masterPasswordService;
    private final SmsCodeService smsCodeService;

    private final IpAddressService ipAddressService;

    private final UserLoginService userLoginService;



    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO user, HttpSession session) {

        try {
            userService.loginUser(user, session, fetchClientIpAddr());
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
    @PostMapping("/sms/code/confirmation")
    public ResponseEntity<?> confirmUser(@RequestBody SmsCodeDTO smsCodeDTO) {

        try {
            smsCodeService.verify(smsCodeDTO.getSmsCode(), smsCodeDTO.getLogin());
            return ResponseEntity.ok().build();
        } catch (SmsCodeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/sms/code/change")
    public ResponseEntity<?> changeSmsCodeForUser(@RequestBody String login) {

        try {
            smsCodeService.changeCode(login);
            return ResponseEntity.ok().build();
        } catch (SmsCodeException e) {
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
    @GetMapping("/ipAddresses")
    public ResponseEntity<List<IpAddress>> getAllIpAddresses() {

        return ResponseEntity.ok(ipAddressService.getAll());
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/ipAddresses/block/{id}")
    public ResponseEntity<?> blockIpAddress(@PathVariable Long id) {
        try {
            ipAddressService.block(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/ipAddresses/unblock/{id}")
    public ResponseEntity<?> unblockIpAddress(@PathVariable Long id) {

        try {
            ipAddressService.unblock(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
          return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/userLogins/{login}")
    public ResponseEntity<List<UserLogin>> blockIpAddress(@PathVariable String login) {
     return ResponseEntity.ok(userLoginService.getAllForUser(login));
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


    @SuppressWarnings("ConstantConditions")
    protected String fetchClientIpAddr() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = Optional.ofNullable(request.getHeader("X-FORWARDED-FOR")).orElse(request.getRemoteAddr());
        if (ip.equals("0:0:0:0:0:0:0:1")) ip = "127.0.0.1";
        Assert.isTrue(ip.chars().filter($ -> $ == '.').count() == 3, "Illegal IP: " + ip);
        return ip;
    }

}
