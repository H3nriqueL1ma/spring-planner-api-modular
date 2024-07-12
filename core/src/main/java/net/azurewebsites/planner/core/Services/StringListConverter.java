package net.azurewebsites.planner.core.Services;

import jakarta.persistence.AttributeConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class StringListConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List strings) {
        try {
            return objectMapper.writeValueAsString(strings);
        } catch (Exception error) {
            throw new RuntimeException("Error converting list to JSON string. ", error);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, List.class);
        } catch (Exception error) {
            throw new RuntimeException("Error converting JSON string to list", error);
        }
    }
}
