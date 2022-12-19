package com.KeyWallet.services;

import com.KeyWallet.entity.IpLoginHistory;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.UserLogInException;
import com.KeyWallet.models.IpAddressBlockedDTO;
import com.KeyWallet.models.LoginResult;
import com.KeyWallet.repository.IpLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpLoginHistoryService {

    private final IpLoginHistoryRepository IpLoginHistoryRepository;

    public void storeIpLoginData(String ipAddress, LoginResult result) {
        IpLoginHistory loginHistory = IpLoginHistory.builder()
                .loginDate(LocalDateTime.now())
                .loginResult(result)
                .ipAddress(ipAddress)
                .build();
        IpLoginHistoryRepository.save(loginHistory);
    }

    public void checkIpLoginAttemptCount(String ipAddress) {
        List<IpLoginHistory> userLoginHistories = IpLoginHistoryRepository.findAllByIpAddress(ipAddress).stream()
                .sorted(Comparator.comparing(IpLoginHistory::getLoginDate).reversed())
                .collect(Collectors.toList());

        Optional<IpLoginHistory> lastSuccessfulLogin = userLoginHistories.stream()
                .filter(history -> LoginResult.SUCCESS.equals(history.getLoginResult()))
                .findFirst();

        int failedLoginAttempts = 0;
        if (lastSuccessfulLogin.isPresent()) {
            IpLoginHistory lastSuccessLoginInHistory = lastSuccessfulLogin.get();
            failedLoginAttempts = userLoginHistories.size() - (userLoginHistories.size() - userLoginHistories.indexOf(lastSuccessLoginInHistory));
        } else {
            failedLoginAttempts = userLoginHistories.size();
        }

        if (failedLoginAttempts >= 20) {
            throw new UserLogInException(ExceptionMessages.BLOCK_ACCOUNT_PERM.getCode());
        }
    }

    public List<IpAddressBlockedDTO> getInfoAboutIpAdresses() {
        List<IpAddressBlockedDTO> list = new ArrayList<>();
        List<IpLoginHistory> loginHistories = IpLoginHistoryRepository.findAll();
        Map<String, List<IpLoginHistory>> loginMap = loginHistories.stream().collect(Collectors.groupingBy(IpLoginHistory::getIpAddress));

        for (Map.Entry<String, List<IpLoginHistory>> ipAddressEntry : loginMap.entrySet()) {
            List<IpLoginHistory> loginHistoriesForIp = ipAddressEntry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(IpLoginHistory::getLoginDate).reversed())
                    .collect(Collectors.toList());
            Optional<IpLoginHistory> lastSuccessfulLogin = loginHistoriesForIp.stream()
                    .filter(history -> LoginResult.SUCCESS.equals(history.getLoginResult()))
                    .findFirst();

            int failedLoginAttempts = 0;
            if (lastSuccessfulLogin.isPresent()) {
                IpLoginHistory lastSuccessLoginInHistory = lastSuccessfulLogin.get();
                failedLoginAttempts = loginHistoriesForIp.size() - (loginHistoriesForIp.size() - loginHistoriesForIp.indexOf(lastSuccessLoginInHistory));
            } else {
                failedLoginAttempts = loginHistoriesForIp.size();
            }
            if(failedLoginAttempts >= 20 && loginHistoriesForIp.get(0).getLoginDate().plusMinutes(30L).isAfter(LocalDateTime.now())) {
                list.add(new IpAddressBlockedDTO(ipAddressEntry.getKey(), true));
            } else {
                list.add(new IpAddressBlockedDTO(ipAddressEntry.getKey(), false));
            }
        }

        return list;
    }

    @Transactional
    public void deleteAllRecordsByIpAddress(String ipAddress) {
        IpLoginHistoryRepository.deleteAllByIpAddress(ipAddress);
    }

}
