package com.isia.tfm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isia.tfm.entity.ApplicationUserEntity;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.CreateUser201Response;
import com.isia.tfm.model.User;
import com.isia.tfm.repository.ApplicationUserRepository;
import com.isia.tfm.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserManagementServiceImplTest {

    @InjectMocks
    private UserManagementServiceImpl userManagementServiceImpl;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        User user = TestUtils.readMockFile("user", User.class);
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthdate(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());

        when(passwordEncoder.encode(anyString())).thenReturn("example");
        when(objectMapper.convertValue(user, ApplicationUserEntity.class))
                .thenReturn(applicationUserEntity);
        when(applicationUserRepository.findAll()).thenReturn(new ArrayList<>());
        when(applicationUserRepository.save(any(ApplicationUserEntity.class))).thenReturn(applicationUserEntity);

        CreateUser201Response response = userManagementServiceImpl.createUser(user);

        CreateUser201Response expectedResponse = new CreateUser201Response();
        expectedResponse.setMessage("User successfully created.");

        assertEquals(expectedResponse, response);
    }

    @Test
    void createUserErrorUsername() {
        User user = TestUtils.readMockFile("user", User.class);
        List<ApplicationUserEntity> applicationUserEntityList = new ArrayList<>();
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthdate(), "Male", "junit@example.com",
                user.getPhoneNumber(), LocalDateTime.now());
        applicationUserEntityList.add(applicationUserEntity);

        when(applicationUserRepository.findAll()).thenReturn(applicationUserEntityList);

        CustomException e = assertThrows(CustomException.class, () -> {
            userManagementServiceImpl.createUser(user);
        });

        assertEquals("The username is already in use", e.getMessage());
    }

    @Test
    void createUserErrorEmail() {
        User user = TestUtils.readMockFile("user", User.class);
        List<ApplicationUserEntity> applicationUserEntityList = new ArrayList<>();
        ApplicationUserEntity applicationUserEntity = new ApplicationUserEntity("example", user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getBirthdate(), "Male", user.getEmail(),
                user.getPhoneNumber(), LocalDateTime.now());
        applicationUserEntityList.add(applicationUserEntity);

        when(applicationUserRepository.findAll()).thenReturn(applicationUserEntityList);

        CustomException e = assertThrows(CustomException.class, () -> {
            userManagementServiceImpl.createUser(user);
        });

        assertEquals("The email is already in use", e.getMessage());
    }

}