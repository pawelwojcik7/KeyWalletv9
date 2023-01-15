package com.KeyWallet.services;

import com.KeyWallet.entity.UserLogin;
import com.KeyWallet.exception.IpAddressException;
import com.KeyWallet.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepository userLoginRepository;
    private final IpAddressService ipAddressService;

    public void registerCorrectLogin(Long userId, HttpSession session, String ipAddress) {

        ipAddressService.goodLoginFromIp(ipAddress, session.getId());
        userLoginRepository.save(
                new UserLogin(
                        null,
                        OffsetDateTime.now(),
                        true,
                        userId,
                        session.toString(),
                        ipAddressService.getIpAddressIdByIpAddress(ipAddress)
                )
        );
    }

    public void registerBadLogin(Long userId, HttpSession session, String ipAddress) throws IpAddressException {

        try {
            ipAddressService.badLoginFromIp(ipAddress, session.getId());
        } catch (IpAddressException e) {
            throw e;
        } finally {
            userLoginRepository.save(
                    new UserLogin(
                            null,
                            OffsetDateTime.now(),
                            false,
                            userId,
                            session.toString(),
                            ipAddressService.getIpAddressIdByIpAddress(ipAddress)
                    )
            );
        }
    }

    public List<UserLogin> getSortedUserLogins(Long userId) {

        Comparator<UserLogin> comparator = Comparator.comparing(UserLogin::getTime).reversed();
        List<UserLogin> userLogins = userLoginRepository.findUserLoginByIdUser(userId);
        userLogins.sort(comparator);

        return userLogins;
    }


}
