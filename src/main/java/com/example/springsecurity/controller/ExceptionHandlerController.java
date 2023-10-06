package com.example.springsecurity.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.springsecurity.exp.EmailAlreadyExistsException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        List<String> errors = new LinkedList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    private ResponseEntity<?> handler(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler({EmailAlreadyExistsException.class})
    private ResponseEntity<?> handler(EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({JWTDecodeException.class})
    private ResponseEntity<?> handler(JWTDecodeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({SignatureException.class})
    private ResponseEntity<?> handler(SignatureException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
