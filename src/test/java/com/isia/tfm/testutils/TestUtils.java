package com.isia.tfm.testutils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Paths;

public class TestUtils {

    public static <T extends Object> T readMockFile (String path, Class <T> type) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(Paths.get("src/test/resources/mocks/" + path + ".json").toFile(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
