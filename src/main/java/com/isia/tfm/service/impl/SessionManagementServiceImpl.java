package com.isia.tfm.service.impl;

import com.isia.tfm.entity.*;
import com.isia.tfm.exception.CustomException;
import com.isia.tfm.model.*;
import com.isia.tfm.repository.*;
import com.isia.tfm.service.SessionManagementService;
import com.isia.tfm.service.TransactionHandlerService;
import com.isia.tfm.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class SessionManagementServiceImpl implements SessionManagementService {

    TransactionHandlerService transactionHandlerService;
    ExerciseRepository exerciseRepository;
    JavaMailSender emailSender;

    public SessionManagementServiceImpl(TransactionHandlerService transactionHandlerService,
                                        ExerciseRepository exerciseRepository,
                                        JavaMailSender emailSender) {
        this.transactionHandlerService = transactionHandlerService;
        this.exerciseRepository = exerciseRepository;
        this.emailSender = emailSender;
    }

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public CreateSessions201Response createSessions(Boolean calculateAndSaveTrainingVolume, Boolean sendEmail,
                                                    Boolean saveExcel, CreateSessionsRequest createSessionsRequest,
                                                    String destinationEmail, String excelFilePath) {
        CreateSessions201Response createSessions201Response = new CreateSessions201Response();
        createSessions201Response.setSavedTrainingVolumeSuccessfully("false");
        createSessions201Response.setSentEmailSuccessfully("false");
        createSessions201Response.setSavedExcelSuccessfully("false");
        List<ExerciseEntity> exerciseEntityList = getExerciseToCreateList(createSessionsRequest);
        List<ReturnSession> returnSessionList =
                transactionHandlerService.saveSessions(createSessionsRequest.getSessions(), exerciseEntityList);
        createSessions201Response.setSessions(returnSessionList);
        List<Session> filteredSessionList = filterSessionsCreated(createSessionsRequest.getSessions(), returnSessionList);
        log.debug("Saved training sessions");

        if (calculateAndSaveTrainingVolume) {
            try {
                transactionHandlerService.saveTrainingVolume(filteredSessionList);
                createSessions201Response.setSavedTrainingVolumeSuccessfully("true");
                log.debug("Training volume for each exercise of each session saved");
            } catch (Exception e) {
                log.error("Training volume could not be calculated and saved");
            }
        }

        if (sendEmail) {
            try {
                sendTrainingSessionEmail(destinationEmail, filteredSessionList);
                createSessions201Response.setSentEmailSuccessfully("true");
                log.debug("An email successfully sent for each saved training session");
            } catch (Exception e) {
                log.error("The information email could not be sent");
            }
        }

        if (saveExcel) {
            try {
                createExcelFile(filteredSessionList, exerciseEntityList, excelFilePath);
                createSessions201Response.setSavedExcelSuccessfully("true");
                log.debug("An excel successfully saved for each saved training session");
            } catch (Exception e) {
                log.error("The excel not be saved");
            }
        }

        return createSessions201Response;
    }

    private List<ExerciseEntity> getExerciseToCreateList(CreateSessionsRequest createSessionsRequest) {
        List<Integer> exerciseToCreateList = createSessionsRequest.getSessions().stream()
                .flatMap(session -> session.getTrainingVariables().stream())
                .map(TrainingVariable::getExerciseId)
                .toList();
        Set<Integer> createdExerciseSet = new HashSet<>(exerciseRepository.findAllExerciseIds());
        Optional<String> exerciseNotCreated = exerciseToCreateList.stream()
                .filter(exercise -> !createdExerciseSet.contains(exercise))
                .findFirst()
                .map(String::valueOf);

        exerciseNotCreated.ifPresent(exerciseId -> {
            throw new CustomException("404", "Not found", "The exercise with ID " + exerciseId + " is not created");
        });
        return exerciseRepository.findAllById(exerciseToCreateList);
    }

    private List<Session> filterSessionsCreated(List<Session> sessionList, List<ReturnSession> returnSessionList) {
        List<Integer> validSessionIds = returnSessionList.stream()
                .filter(returnSession -> "Session successfully created".equals(returnSession.getStatus()))
                .map(ReturnSession::getSessionId)
                .toList();
        return sessionList.stream()
                .filter(session -> validSessionIds.contains(session.getSessionId()))
                .toList();
    }

    private void sendTrainingSessionEmail(String destinationEmail, List<Session> sessionList) {
        String user = sessionList.get(0).getUsername();
        sessionList.stream()
                .map(session -> createEmailMessage(destinationEmail, user, session))
                .forEach(emailSender::send);
    }

    private SimpleMailMessage createEmailMessage(String destinationEmail, String user, Session session) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(destinationEmail);
        message.setSubject("Training Diary App");
        message.setText("User " + user + " has registered a new training session on " + session.getSessionDate().toString());
        return message;
    }

    private void createExcelFile(List<Session> createdSessionList, List<ExerciseEntity> exerciseEntityList, String filePath) throws IOException {
        for (Session session : createdSessionList) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = createSheetWithHeader(workbook);
                fillSheetWithData(sheet, session, exerciseEntityList);
                saveWorkbookToFile(workbook, session.getSessionId());
            }
        }
    }

    private Sheet createSheetWithHeader(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Session Data");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"EXERCISE_ID", "EXERCISE_NAME", "SET_NUMBER", "REPETITIONS", "WEIGHT", "RIR"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        return sheet;
    }

    private void fillSheetWithData(Sheet sheet, Session session, List<ExerciseEntity> exerciseEntityList) {
        int rowNum = 1;
        for (ExerciseEntity exerciseEntity : exerciseEntityList) {
            List<TrainingVariable> filteredTrainingVariableList =
                    Utils.filterTrainingVariablesByExerciseId(session.getTrainingVariables(), exerciseEntity.getExerciseId());
            for (TrainingVariable trainingVariable : filteredTrainingVariableList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(exerciseEntity.getExerciseId().toString());
                row.createCell(1).setCellValue(exerciseEntity.getExerciseName());
                row.createCell(2).setCellValue(trainingVariable.getSetNumber().toString());
                row.createCell(3).setCellValue(trainingVariable.getRepetitions().toString());
                row.createCell(4).setCellValue(trainingVariable.getWeight().toString());
                row.createCell(5).setCellValue(trainingVariable.getRir().toString());
            }
        }
    }

    protected void saveWorkbookToFile(Workbook workbook, Integer sessionId, String filePath) throws IOException {
        String fileName = "SESSION_ID_" + sessionId + "_DATA.xlsx";
        String filePathToSave = filePath + fileName;
        try (FileOutputStream fileOut = new FileOutputStream(filePathToSave)) {
            workbook.write(fileOut);
        }
    }

}