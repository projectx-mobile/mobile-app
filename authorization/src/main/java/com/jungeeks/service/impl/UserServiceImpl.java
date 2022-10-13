package com.jungeeks.service.impl;

import com.jungeeks.entity.User;
import com.jungeeks.entity.SecurityUserFirebase;
import com.jungeeks.exception.RegistrationFailedException;
import com.jungeeks.repository.UserRepository;
import com.jungeeks.service.SecurityService;
import com.jungeeks.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private SecurityService securityService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Transactional
    @Override
    public void checkUser(SecurityUserFirebase user) {
        User userDB = userRepository.findByFirebaseId(user.getUid());

        if (Objects.isNull(userDB)) {
            userDB = User.builder()
                    .firebaseId(user.getUid())
                    .email(user.getEmail())
                    .build();
            userRepository.save(userDB);
            log.debug("USer with Uid:" + userDB.getFirebaseId() + " added to db");
        } else {
            if (!userDB.getEmail().equals(user.getEmail())) {
                userDB.setEmail(user.getEmail());
                log.debug("User email updated");
            }
        }
    }

    @Transactional
    @Override
    public void updateAppRegistrationToken(String registrationToken) {
        User userDb = userRepository.findByFirebaseId(securityService.getUser().getUid());
        if (Objects.nonNull(userDb)) {
            userDb.setAppRegistrationToken(registrationToken);
            log.debug("User app registration token updated");
        } else {
            log.error("User not found");
            throw new RegistrationFailedException("User not found");
        }
    }

    @Override
    public boolean checkUserByContainsRegistrationToken() {
        User userDb = userRepository.findByFirebaseId(securityService.getUser().getUid());
        return Objects.nonNull(userDb.getAppRegistrationToken());
    }
}
