package com.erebelo.spring.common.utils.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;

@UtilityClass
public class ObjectMapperProvider {

    public static final ObjectMapper INSTANCE;

    private static final String ISO_LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    static {
        INSTANCE = new com.fasterxml.jackson.databind.ObjectMapper();

        // Register JavaTimeModule for LocalDate serialization/deserialization
        INSTANCE.registerModule(new JavaTimeModule());

        // Set the ObjectMapper to include all properties during serialization, even if
        // they are null or have default values
        INSTANCE.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // Configure the date format for LocalDate
        INSTANCE.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Set a custom date format using SimpleDateFormat
        INSTANCE.setDateFormat(new SimpleDateFormat(ISO_LOCAL_DATE_FORMAT));
    }
}
