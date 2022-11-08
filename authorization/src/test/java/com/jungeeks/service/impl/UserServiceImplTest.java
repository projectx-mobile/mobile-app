package com.jungeeks.service.impl;

import com.jungeeks.entity.ClientApp;
import com.jungeeks.entity.User;
import com.jungeeks.entity.enums.USER_STATUS;
import com.jungeeks.exception.RegistrationFailedException;
import com.jungeeks.exception.UserNotFoundException;
import com.jungeeks.repository.UserRepository;
import com.jungeeks.security.entity.SecurityUserFirebase;
import com.jungeeks.service.SecurityService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserServiceImplTest.class)
@Tag("unit")
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityService securityService;

    private static User user;
    private static SecurityUserFirebase securityUserFirebaseWithSameEmail;
    private static SecurityUserFirebase securityUserFirebaseWithAnotherEmail;

    private static final String FIREBASE_ID = "test";

    @BeforeAll
    static void setUp() {
        user = User.builder()
                .id(1L)
                .firebaseId(FIREBASE_ID)
                .email("test@gmail.com")
                .clientApps(new ArrayList<>())
                .build();
        securityUserFirebaseWithSameEmail = SecurityUserFirebase.builder()
                .uid(FIREBASE_ID)
                .email("test@gmail.com")
                .build();
        securityUserFirebaseWithAnotherEmail = SecurityUserFirebase.builder()
                .uid(FIREBASE_ID)
                .email("anotherEmailTetst@gmail.com")
                .build();
    }

    @Test
    void checkUserWithSameEmails() {
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.ofNullable(user));

        userService.checkUser(securityUserFirebaseWithSameEmail);

        verify(userRepository, times(1)).findByFirebaseId(FIREBASE_ID);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void checkUserWithAnotherEmails() {
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.ofNullable(user));

        userService.checkUser(securityUserFirebaseWithAnotherEmail);

        verify(userRepository, times(1)).findByFirebaseId(FIREBASE_ID);

        user.setEmail(securityUserFirebaseWithAnotherEmail.getEmail());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void checkWithNullUserFromDb() {
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.empty());

        userService.checkUser(securityUserFirebaseWithSameEmail);

        verify(userRepository, times(1)).findByFirebaseId(FIREBASE_ID);

        User userToSave = User.builder()
                .email(securityUserFirebaseWithSameEmail.getEmail())
                .firebaseId(securityUserFirebaseWithSameEmail.getUid())
                .build();

        verify(userRepository, times(0)).save(userToSave);
    }

    @Test
    void updateAppRegistrationTokenExistsUser() {
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.ofNullable(user));
        when(securityService.getUser()).thenReturn(securityUserFirebaseWithSameEmail);

        boolean update = userService.updateAppRegistrationToken("newToken");

        assertTrue(update);
    }

    @Test
    void updateAppRegistrationTokenNotExistsUser() {
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.empty());
        when(securityService.getUser()).thenReturn(securityUserFirebaseWithSameEmail);

        assertThrows(RegistrationFailedException.class, () -> userService.updateAppRegistrationToken("0"));
    }

    @Test
    void checkUserByContainsRegistrationTokenExistsUser() {
        User user = User.builder().clientApps(List.of(ClientApp.builder().build())).build();
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.ofNullable(user));
        when(securityService.getUser()).thenReturn(securityUserFirebaseWithSameEmail);

        boolean clientApps = userService.checkUserByContainsRegistrationToken();

        assertTrue(clientApps);
    }

    @Test
    void checkUserByContainsRegistrationTokenNotExistsUser() {
        User user = User.builder().clientApps(new ArrayList<>()).build();
        when(userRepository.findByFirebaseId(any())).thenReturn(Optional.ofNullable(user));
        when(securityService.getUser()).thenReturn(securityUserFirebaseWithSameEmail);

        boolean clientApps = userService.checkUserByContainsRegistrationToken();

        assertFalse(clientApps);
    }

    @Test
    void checkUserStatusWhenUserStatusIsActivePositive() {
        when(userRepository.findByFirebaseId(any())).thenReturn(
                Optional.of(User.builder()
                        .user_status(USER_STATUS.ACTIVE)
                        .build()));
        SecurityUserFirebase testDataUser = SecurityUserFirebase.builder()
                .uid("uid")
                .build();

        assertFalse(userService.checkUserStatus(testDataUser));
    }
    @Test
    void checkUserStatusWhenUserStatusIsBannedPositive() {
        when(userRepository.findByFirebaseId(any())).thenReturn(
                Optional.of(User.builder()
                        .user_status(USER_STATUS.BANNED)
                        .build()));
        SecurityUserFirebase testDataUser = SecurityUserFirebase.builder()
                .uid("uid")
                .build();

        assertTrue(userService.checkUserStatus(testDataUser));
    }

    @Test
    void checkUserStatusWhenUserStatusIsRemovedPositive() {
        when(userRepository.findByFirebaseId(any())).thenReturn(
                Optional.of(User.builder()
                        .user_status(USER_STATUS.REMOVED)
                        .build()));
        SecurityUserFirebase testDataUser = SecurityUserFirebase.builder()
                .uid("uid")
                .build();

        assertTrue(userService.checkUserStatus(testDataUser));
    }

    @Test
    void checkUserStatusNegative() {
        when(userRepository.findByFirebaseId(any())).thenReturn(
                Optional.empty());
        SecurityUserFirebase testDataUser = SecurityUserFirebase.builder()
                .uid("uid")
                .build();
        assertThrows(UserNotFoundException.class,
                ()->userService.checkUserStatus(testDataUser),
                "User with uid uid not found");
    }
}