package com.jungeeks.service.dto.impl;

import com.jungeeks.dto.FamilyMemberDto;
import com.jungeeks.entitiy.Photo;
import com.jungeeks.entitiy.User;
import com.jungeeks.service.entity.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = FamilyMemberServiceImplTest.class)
class FamilyMemberServiceImplTest {

    @Mock
    UserService userService;

    @InjectMocks
    FamilyMemberServiceImpl familyMemberService;

    static List<FamilyMemberDto> familyMembersTD;
    static List<User> usersTD;

    @BeforeAll
    static void prepareTestData() {
        familyMembersTD = List.of(/*FamilyMemberDto.builder()
                        .id(1L)
                        .photoPath("photo1")
                        .username("Dev1")
                        .build(),*/
                FamilyMemberDto.builder()
                        .id(2L)
                        .photoPath("photo2")
                        .username("Dev2")
                        .build());

        usersTD = List.of(User.builder()
                        .id(1L)
                        .name("Dev1")
                        .photo(List.of(Photo.builder()
                                .path("photo1")
                                .build()))
                        .build(),
                User.builder()
                        .id(2L)
                        .name("Dev2")
                        .photo(List.of(Photo.builder()
                                .path("photo2")
                                .build()))
                        .build());
    }

    //Fix this test after security configuration(Change skip adding current identified user to List<FamilyMember>)
    @Test
    void getFamilyMembers() {
        Mockito.when(userService.getAllByFamilyId(any())).thenReturn(usersTD);

        List<FamilyMemberDto> familyMembers = familyMemberService.getFamilyMembers("familyId");

        assertEquals(familyMembersTD, familyMembers);
    }
}