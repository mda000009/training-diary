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
    public CreateSessions201Response createSessions(CreateSessionsRequest createSessionsRequest) {
        List<ExerciseEntity> exerciseEntityList = getExerciseToCreateList(createSessionsRequest);
        List<ReturnSession> returnSessionList =
                transactionHandlerService.saveSessions(createSessionsRequest.getSessions(), exerciseEntityList);
        List<Session> filteredSessionList = filterSessionsCreated(createSessionsRequest.getSessions(), returnSessionList);
        log.debug("Saved training sessions");

        try {
            transactionHandlerService.saveTrainingVolume(filteredSessionList);
            log.debug("Training volume for each exercise of each session saved");
        } catch (Exception e) {
            log.error("Training volume could not be calculated and saved");
        }

        try {
            sendTrainingSessionEmail(createSessionsRequest.getDestinationEmail(), filteredSessionList);
            log.debug("An email successfully sent for each saved training session");
        } catch (Exception e) {
            log.error("The information email could not be sent");
        }

        try {
            createExcelFile(filteredSessionList, exerciseEntityList);
            log.debug("An excel successfully saved for each saved training session");
        } catch (Exception e) {
            log.error("The excel not be saved");
        }

        CreateSessions201Response createSessions201Response = new CreateSessions201Response();
        createSessions201Response.setSessions(returnSessionList);
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

    private void createExcelFile(List<Session> createdSessionList, List<ExerciseEntity> exerciseEntityList)
            throws IOException {
        for (Session session: createdSessionList) {
            // Crear un libro de trabajo
            Workbook workbook = new XSSFWorkbook();
            // Crear una hoja en el libro de trabajo
            Sheet sheet = workbook.createSheet("Session Data");

            // Crear la primera fila (encabezados)
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("EXERCISE_ID");
            headerRow.createCell(1).setCellValue("EXERCISE_NAME");
            headerRow.createCell(2).setCellValue("SET_NUMBER");
            headerRow.createCell(3).setCellValue("REPETITIONS");
            headerRow.createCell(4).setCellValue("WEIGHT");
            headerRow.createCell(5).setCellValue("RIR");

            // Llenar el archivo con datos
            int rowNum = 1;
            for (ExerciseEntity exerciseEntity : exerciseEntityList) {
                List<TrainingVariable> filteredTrainingVariableList = Utils.filterTrainingVariablesByExerciseId(
                        session.getTrainingVariables(), exerciseEntity.getExerciseId());
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

            String fileName = "SESSION_ID_" + session.getSessionId().toString() + "_DATA.xlsx";
            String filePath = "C:\\Users\\manue\\OneDrive\\Escritorio\\TFM\\Excel_examples\\" + fileName;
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            } finally {
                workbook.close();
            }
        }
    }

}