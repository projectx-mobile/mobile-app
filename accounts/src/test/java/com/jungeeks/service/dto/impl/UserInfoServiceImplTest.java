package com.jungeeks.service.dto.impl;

import com.jungeeks.dto.FamilyMemberDto;
import com.jungeeks.dto.UserInfoDto;
import com.jungeeks.exception.InvalidRequestException;
import com.jungeeks.entity.*;
import com.jungeeks.entity.enums.USER_STATUS;
import com.jungeeks.service.dto.FamilyMemberService;
import com.jungeeks.service.entity.UserService;
import com.jungeeks.security.entity.SecurityUserFirebase;
import com.jungeeks.security.service.AuthorizationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@SpringBootTest(classes = UserInfoServiceImplTest.class)
class UserInfoServiceImplTest {

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    @Mock
    private FamilyMemberService familyMemberService;

    @InjectMocks
    private UserInfoServiceImpl userInfoService;

    private static User testDataUser;
    private static User testDataValidAuthUser;
    private static User testDataNotValidAuthUser;
    private static UserInfoDto testDataUserInfoDto;

    private static List<FamilyMemberDto> testDataFamilyMembers;

    @BeforeAll
    static void prepare() {

        testDataValidAuthUser = User.builder()
                .family(Family.builder()
                        .id("familyIdValid")
                        .build())
                .build();
        testDataNotValidAuthUser = User.builder()
                .family(Family.builder()
                        .id("familyIdNotValid")
                        .build())
                .build();

        testDataUser = User.builder()
                .name("Dev")
                .email("dev123@gmail.com")
                .user_status(USER_STATUS.ACTIVE)
                .photo(List.of(Photo.builder()
                        .path("devPhoto")
                        .creationDate(LocalDateTime.now())
                        .build()))
                .family(Family.builder()
                        .id(testDataValidAuthUser
                                .getFamily()
                                .getId())
                        .build())
                .build();

        testDataFamilyMembers = List.of(FamilyMemberDto.builder()
                        .id(1L)
                        .photoPath("photo1")
                        .username("Dev1")
                        .build(),
                FamilyMemberDto.builder()
                        .id(2L)
                        .photoPath("photo2")
                        .username("Dev2")
                        .build());

        testDataUserInfoDto = UserInfoDto.builder()
                .username(testDataUser.getName())
                .familyMembers(testDataFamilyMembers)
                .photoPath(testDataUser.getPhoto().get(0).getPath())
                .build();
    }

    @Test
    void getUserInfoByUserIdPositive() {
        when(authorizationService.getUser()).thenReturn(SecurityUserFirebase.builder()
                .uid("uid")
                .build());
        when(userService.getUserByUid(any())).thenReturn(testDataValidAuthUser);
        when(userService.getUserById(any())).thenReturn(testDataUser);
        when(familyMemberService.getFamilyMembers(any())).thenReturn(testDataFamilyMembers);

        UserInfoDto userInfoByUserId = userInfoService.getUserInfoByUserId(1L);

        assertEquals(testDataUserInfoDto, userInfoByUserId);
    }

    @Test
    void getUserInfoByUserIdNegative() {
        when(authorizationService.getUser()).thenReturn(SecurityUserFirebase.builder()
                .uid("uid")
                .build());
        when(userService.getUserByUid(any())).thenReturn(testDataNotValidAuthUser);
        when(userService.getUserById(any())).thenReturn(testDataUser);
        when(familyMemberService.getFamilyMembers(any())).thenReturn(testDataFamilyMembers);

        assertThrows(InvalidRequestException.class,
                () -> userInfoService.getUserInfoByUserId(1L),
                "Invalid id parameter");
    }

    @Test
    void getUserInfoByUserUId() {
        when(userService.getUserByUid(any())).thenReturn(testDataUser);
        when(familyMemberService.getFamilyMembers(any())).thenReturn(testDataFamilyMembers);

        UserInfoDto userInfoByUserId = userInfoService.getUserInfoByUserUId("uid");

        assertEquals(testDataUserInfoDto, userInfoByUserId);
    }
}