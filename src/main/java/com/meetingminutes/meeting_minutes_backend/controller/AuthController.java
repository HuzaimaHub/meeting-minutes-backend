package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import com.meetingminutes.meeting_minutes_backend.security.JwtService;
import com.meetingminutes.meeting_minutes_backend.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;

    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token, String fullName, String email, String role) {}
    public record ForgotRequest(String email) {}
    public record ResetRequest(String token, String newPassword) {}
    public record MessageResponse(String message) {}

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .filter(a -> passwordEncoder.matches(request.password(), a.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        String token = jwtService.generateToken(admin.getEmail(), admin.getRole());
        return new LoginResponse(token, admin.getFullName(), admin.getEmail(), admin.getRole());
    }

    @PostMapping("/forgot-password")
    public MessageResponse forgotPassword(@RequestBody ForgotRequest request) {
        passwordResetService.requestReset(request.email());
        return new MessageResponse(
                "If an account exists for that email address, a reset link has been sent.");
    }

    @GetMapping("/reset-password/check")
    public MessageResponse checkResetToken(@RequestParam String token) {
        passwordResetService.validateToken(token);
        return new MessageResponse("Valid");
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@RequestBody ResetRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return new MessageResponse("Your password has been changed. You can now sign in.");
    }
}