package com.isia.tfm.controller;

import com.isia.tfm.api.UserManagementApi;
import com.isia.tfm.model.CreateUser201Response;
import com.isia.tfm.model.User;
import com.isia.tfm.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User management controller
 */
@RestController
@RequestMapping({"/training-diary/v1"})
public class UserManagementController implements UserManagementApi {

    @Autowired
    private UserManagementService userManagementService;

    @Override
    public ResponseEntity<CreateUser201Response> createUser(User user) {
        CreateUser201Response response = userManagementService.createUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
