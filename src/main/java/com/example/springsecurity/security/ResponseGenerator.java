package com.example.springsecurity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class ResponseGenerator {

    public void generateError(String message, HttpServletResponse response) {
        response.setHeader("error", message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Map<String, String> error = new HashMap<>();
        error.put("error_message", message);
        response.setContentType(APPLICATION_JSON_VALUE);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
