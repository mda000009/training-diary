package com.isia.tfm.service;

import com.isia.tfm.model.ReturnSession;
import com.isia.tfm.model.Session;

/**
 * Service interface for managing sessions.
 */
public interface SessionManagementService {

    /**
     *
     * @param calculateAndSaveTrainingVolume the calculate and save training volume
     * @param sendEmail the send email
     * @param saveExcel the save excel
     * @param session the session
     * @param destinationEmail the destination email
     * @param excelFilePath the Excel file path
     * @return {@link ReturnSession}
     */
    ReturnSession createSession(Boolean calculateAndSaveTrainingVolume, Boolean sendEmail,
                                 Boolean saveExcel, Session session,
                                 String destinationEmail, String excelFilePath);

}
