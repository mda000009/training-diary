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
    public ReturnSession createSession(boolean calculateAndSaveTrainingVolume, boolean sendEmail, boolean saveExcel,
                                       Session session, String destinationEmail, String excelFilePath) {
        List<ExerciseEntity> exerciseEntityList = getExerciseToCreateList(session);
        ReturnSession returnSession = transactionHandlerService.saveSession(session, exerciseEntityList);
        log.debug("Saved training session");

        if (calculateAndSaveTrainingVolume) {
            try {
                transactionHandlerService.saveTrainingVolume(session);
                returnSession.setSavedTrainingVolumeSuccessfully("true");
                log.debug("Training volume for all exercises saved");
            } catch (Exception e) {
                log.error("Training volume could not be calculated and saved");
            }
        }
        if (sendEmail && destinationEmail != null && !destinationEmail.isEmpty()) {
            try {
                sendTrainingSessionEmail(destinationEmail, session);
                returnSession.setSentEmailSuccessfully("true");
                log.debug("Email successfully sent");
            } catch (Exception e) {
                log.error("Email could not be sent");
            }
        }
        if (saveExcel && excelFilePath != null && !excelFilePath.isEmpty()) {
            try {
                createExcelFile(session, exerciseEntityList, excelFilePath);
                returnSession.setSavedExcelSuccessfully("true");
                log.debug("Excel saved");
            } catch (Exception e) {
                log.error("Excel not be saved");
            }
        }

        return returnSession;
    }

    private List<ExerciseEntity> getExerciseToCreateList(Session session) {
        List<Integer> exerciseToCreateList = session.getTrainingVariables().stream()
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

    private void sendTrainingSessionEmail(String destinationEmail, Session session) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(destinationEmail);
        message.setSubject("Training Diary App");
        message.setText("User " + session.getUsername() + " has registered a new training session on " + session.getSessionDate().toString());
        emailSender.send(message);
    }

    private void createExcelFile(Session session, List<ExerciseEntity> exerciseEntityList, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = createSheetWithHeader(workbook);
            fillSheetWithData(sheet, session, exerciseEntityList);
            saveWorkbookToFile(workbook, session.getSessionId(), filePath);
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