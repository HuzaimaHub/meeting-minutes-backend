package com.meetingminutes.meeting_minutes_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

final class PasswordPolicy {

    private PasswordPolicy() {}

    static void check(String password) {
        if (password == null || password.length() < 8) {
            throw bad("The password must be at least 8 characters long.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw bad("The password is too long (maximum 72 characters).");
        }
        if (!password.matches(".*[A-Za-z].*")) {
            throw bad("The password must contain at least one letter.");
        }
        if (!password.matches(".*\\d.*")) {
            throw bad("The password must contain at least one number.");
        }
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}