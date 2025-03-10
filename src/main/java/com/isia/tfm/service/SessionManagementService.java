package com.isia.tfm.service;

import com.isia.tfm.model.CreateSessions201Response;
import com.isia.tfm.model.CreateSessionsRequest;

/**
 * Service interface for managing sessions.
 */
public interface SessionManagementService {

    /**
     *
     * @param calculateAndSaveTrainingVolume the calculate and save training volume
     * @param sendEmail the send email
     * @param saveExcel the save excel
     * @param createSessionsRequest the create sessions request
     * @param destinationEmail the destination email
     * @param excelFilePath the excel file path
     * @return {@link CreateSessions201Response}
     */
    CreateSessions201Response createSessions(Boolean calculateAndSaveTrainingVolume, Boolean sendEmail,
                                             Boolean saveExcel, CreateSessionsRequest createSessionsRequest,
                                             String destinationEmail, String excelFilePath);

}
