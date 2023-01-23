package com.KeyWallet.services;

import com.KeyWallet.entity.IncorrectLogins;
import com.KeyWallet.entity.IpAddress;
import com.KeyWallet.exception.ExceptionMessages;
import com.KeyWallet.exception.IpAddressException;
import com.KeyWallet.repository.IncorrectLoginsRepository;
import com.KeyWallet.repository.IpAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;
    private final IncorrectLoginsRepository incorrectLoginsRepository;


    public List<IpAddress> getAll(){
        return StreamSupport.stream(ipAddressRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public Long getIpAddressIdByIpAddress(String ipAddress){
       return ipAddressRepository.findByIpAddress(ipAddress).getId();
    }

    @Transactional(dontRollbackOn = IpAddressException.class)
    public void checkIfIpAddressIsNotBlocked(String ipAddress) throws IpAddressException {

        IpAddress ipAddressObject = ipAddressRepository.findByIpAddress(ipAddress);
        if (Objects.isNull(ipAddressObject)) {
            return;
        }
        if (ipAddressObject.getPermanentLock()) {

            throw new IpAddressException(ExceptionMessages.PERMANENT_BLOCK_IP_ADDRESS.getCode());
        }
        if (ipAddressObject.getTempLock().isAfter(OffsetDateTime.now())) {

            throw new IpAddressException(ExceptionMessages.TEMP_BLOCK_IP_ADDRESS.getCode() + ipAddressObject.getTempLock());
        }
    }

    @Transactional
    public void goodLoginFromIp(String ipAddress, String sessionId) {

        IpAddress ipAddressObject = ipAddressRepository.findByIpAddress(ipAddress); // sprawdzamy czy istnieje w bazie
        if (Objects.isNull(ipAddressObject)) { // jeżeli nie istnieje

            IpAddress ipAddressObject1 = new IpAddress( // tworzymy nowy rekord
                    null,
                    1,
                    0,
                    0,
                    false,
                    OffsetDateTime.now(),
                    ipAddress);

            ipAddressRepository.save(ipAddressObject1); // zapisujemy nowy rekord

        } else { // jezeli rekord o danym ip istnieje w bazie danych

            ipAddressRepository.updateOkLoginNum(ipAddressObject.getOkLoginNum() + 1, ipAddressObject.getId()); // update good login num
            ipAddressRepository.updateLastBadLoginNum(0, ipAddressObject.getId());
        }
    }

    @Transactional(dontRollbackOn = IpAddressException.class)
    public void badLoginFromIp(String ipAddress, String sessionId) throws IpAddressException {

        IpAddress ipAddressObject = ipAddressRepository.findByIpAddress(ipAddress); // sprawdzamy czy istnieje w bazie

        if (Objects.isNull(ipAddressObject)) { // jeżeli nie istnieje

            ipAddressRepository.save(new IpAddress(
                    null,
                    0,
                    1,
                    1,
                    false,
                    OffsetDateTime.now(),
                    ipAddress)); // zapisujemy nowy rekord

            IpAddress forId = ipAddressRepository.findByIpAddress(ipAddress); // wyciagamy ponownie nowy rekord dla id
            saveNewIncorrectLogin(forId.getId(), sessionId);  // zapisujemy nowy rekord incorrectLogin

        } else { // jezeli rekord o danym ip istnieje w bazie danych

            if (ipAddressObject.getLastBadLoginNum() >= 10) { // jezeli 10 zlych logowan z ip -> blokujemy address

                setPermanentLock(ipAddressObject.getId()); // ustawiamy w bazie ze adress zablokowany
                updateLastBadLoginNum(ipAddressObject.getLastBadLoginNum() + 1, ipAddressObject.getId()); // zwiekszamy liczbe ostatnich logowan
                updateBadLoginNum(ipAddressObject.getBadLoginNum() + 1, ipAddressObject.getId()); // zwiekszamy ogólna liczbe złych logowań
                saveNewIncorrectLogin(ipAddressObject.getId(), sessionId); // zapisujemy nowy rekord incorrectLogin

                throw new IpAddressException(ExceptionMessages.PERMANENT_BLOCK_IP_ADDRESS.getCode()); // wyrzucamy wyjątek że addres został zablokowany
            }

            if (ipAddressObject.getLastBadLoginNum() >= 8) {

                OffsetDateTime newTempBlock = OffsetDateTime.now().plus(Duration.ofSeconds(10)); // nowy czas blokady
                updateTempLock(newTempBlock, ipAddressObject.getId()); // update nowego czasu tymczasowej blokady
                updateLastBadLoginNum(ipAddressObject.getLastBadLoginNum() + 1, ipAddressObject.getId());
                updateBadLoginNum(ipAddressObject.getBadLoginNum() + 1, ipAddressObject.getId());
                saveNewIncorrectLogin(ipAddressObject.getId(), sessionId); // zapisujemy nowy rekord incorrectLogin

                throw new IpAddressException(ExceptionMessages.TEMP_BLOCK_IP_ADDRESS.getCode() + newTempBlock.toString());

            }
            if (ipAddressObject.getLastBadLoginNum() >= 6) {

                OffsetDateTime newTempBlock = OffsetDateTime.now().plus(Duration.ofSeconds(5)); // nowy czas blokady
                updateTempLock(newTempBlock, ipAddressObject.getId()); // update nowego czasu tymczasowej blokady
                updateLastBadLoginNum(ipAddressObject.getLastBadLoginNum() + 1, ipAddressObject.getId());
                updateBadLoginNum(ipAddressObject.getBadLoginNum() + 1, ipAddressObject.getId());
                saveNewIncorrectLogin(ipAddressObject.getId(), sessionId); // zapisujemy nowy rekord incorrectLogin

                throw new IpAddressException(ExceptionMessages.TEMP_BLOCK_IP_ADDRESS.getCode() + newTempBlock.toString());

            }

            // jeżeli ipAddres nie łapie się do całościowej blokady ani do tymczasowej
            updateLastBadLoginNum(ipAddressObject.getLastBadLoginNum() + 1, ipAddressObject.getId());
            updateBadLoginNum(ipAddressObject.getBadLoginNum() + 1, ipAddressObject.getId());
            saveNewIncorrectLogin(ipAddressObject.getId(), sessionId); // zapisujemy nowy rekord incorrectLogin

        }

    }

    private void saveNewIncorrectLogin(Long idIpAddress, String sessionId) {

        incorrectLoginsRepository.save(new IncorrectLogins(null, OffsetDateTime.now(), sessionId, idIpAddress));
    }


    private void updateTempLock(OffsetDateTime time, Long ipAddressId) {

        ipAddressRepository.updateTempLock(time, ipAddressId);
    }


    private void updateLastBadLoginNum(Integer newValue, Long ipAddressId) {

        ipAddressRepository.updateLastBadLoginNum(newValue, ipAddressId);
    }


    private void updateBadLoginNum(Integer newValue, Long ipAddressId) {

        ipAddressRepository.updateBadLoginNum(newValue, ipAddressId);
    }


    private void setPermanentLock(Long ipAddressId) {

        ipAddressRepository.updatePermanentLock(true, ipAddressId);
    }

    @Transactional
    public void block(Long id) {
        ipAddressRepository.updatePermanentLock(true, id);
    }
@Transactional
    public void unblock(Long id){
        ipAddressRepository.updatePermanentLock(false, id);
    ipAddressRepository.updateBadLoginNum(0, id);
    ipAddressRepository.updateLastBadLoginNum(0, id);
    }
}
