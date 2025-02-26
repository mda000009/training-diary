package com.isia.tfm.controller;

import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;
import com.isia.tfm.model.CreateUser201Response;
import com.isia.tfm.model.User;
import com.isia.tfm.service.SessionManagementService;
import com.isia.tfm.service.UserManagementService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureObservability
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementControllerTest {
    @InjectMocks
    private UserManagementController userManagementController;
    @Mock
    private UserManagementService userManagementService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        User user = TestUtils.readMockFile("user", User.class);
        CreateUser201Response createUser201Response = new CreateUser201Response();
        createUser201Response.setMessage("User successfully created.");

        when(userManagementService.createUser(any(User.class))).thenReturn(createUser201Response);

        ResponseEntity<CreateUser201Response> response = userManagementController.createUser(user);

        ResponseEntity<CreateUser201Response> expectedResponse = new ResponseEntity<>(createUser201Response, HttpStatus.CREATED);

        assertEquals(expectedResponse, response);
    }
}
