package com.jungeeks.service.busines.impl;

import com.jungeeks.entitiy.User;
import com.jungeeks.service.busines.UploadUserService;
import com.jungeeks.service.entity.UserService;
import com.jungeeks.service.photoStorage.PhotoStorageService;
import com.jungeeks.service.photoStorage.enums.PHOTO_TYPE;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Log4j2
public class UploadUserServiceImpl implements UploadUserService {

    private UserService userService;

    private PhotoStorageService photoStorageService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPhotoStorageService(PhotoStorageService photoStorageService) {
        this.photoStorageService = photoStorageService;
    }

    @Override
    public void uploadPhoto(@NonNull Long userId, @NonNull MultipartFile multipartFile) {
        User user = userService.getUserById(userId);
        String path = user.getPhoto().get(0).getPath();
        photoStorageService.update(path, PHOTO_TYPE.USER);
        log.debug("User photo uploaded");
    }
}
