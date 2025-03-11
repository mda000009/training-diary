package com.isia.tfm.controller;

import com.isia.tfm.api.SessionManagementApi;
import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.model.Session;
import com.isia.tfm.service.SessionManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Session management controller
 */
@RestController
@RequestMapping({"/training-diary/v1"})
public class SessionManagementController implements SessionManagementApi {

    SessionManagementService sessionManagementService;

    public SessionManagementController(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    @Override
    public ResponseEntity<ReturnSession> createSession(boolean calculateAndSaveTrainingVolume, boolean sendEmail,
                                                       boolean saveExcel, Session session,
                                                       String destinationEmail, String excelFilePath) {
        ReturnSession response = sessionManagementService.createSession(
                calculateAndSaveTrainingVolume, sendEmail, saveExcel, session, destinationEmail, excelFilePath);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
