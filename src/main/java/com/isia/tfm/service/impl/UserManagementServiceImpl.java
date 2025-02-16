package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ApplicationUserEntity;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.CreateUser201Response;
import com.isia.tfm.model.User;
import com.isia.tfm.repository.ApplicationUserRespository;
import com.isia.tfm.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationUserRespository applicationUserRespository;

    @Override
    public CreateUser201Response createUser(User user) {
        CreateUser201Response createUser201Response = new CreateUser201Response();
        checkUsernameAndEmail(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ApplicationUserEntity applicationUserEntity = objectMapper.convertValue(user, ApplicationUserEntity.class);
        applicationUserEntity.setBirthDate(user.getBirthday());
        applicationUserEntity.setCreationDate(LocalDateTime.now());
        applicationUserEntity = applicationUserRespository.save(applicationUserEntity);
        if (applicationUserEntity.getUsername() != null) {
            createUser201Response.setMessage("User successfully created.");
        } else {
            throw new CustomException("500", "Internal Server Error", "Internal Server Error");
        }
        return createUser201Response;
    }

    private void checkUsernameAndEmail(User user) {
        List<ApplicationUserEntity> applicationUserEntityList = applicationUserRespository.findAll();
        if (checkUsername(user, applicationUserEntityList)) {
            throw new CustomException("409", "Conflict", "The username is already in use");
        } else if (checkEmail(user, applicationUserEntityList)) {
            throw new CustomException("409", "Conflict", "The email is already in use");
        }
    }

    private boolean checkUsername(User user, List<ApplicationUserEntity> applicationUserEntityList) {
        boolean usernameFound = false;
        int i= 0;
        while (!usernameFound && i < applicationUserEntityList.size()) {
            if (Objects.equals(user.getUsername(),applicationUserEntityList.get(i).getUsername())) {
                usernameFound = true;
            } else {
                i++;
            }
        }
        return usernameFound;
    }

    private boolean checkEmail(User user, List<ApplicationUserEntity> applicationUserEntityList) {
        boolean emailFound = false;
        int i= 0;
        while (!emailFound && i < applicationUserEntityList.size()) {
            if (Objects.equals(user.getEmail(),applicationUserEntityList.get(i).getEmail())) {
                emailFound = true;
            } else {
                i++;
            }
        }
        return emailFound;
    }

}
