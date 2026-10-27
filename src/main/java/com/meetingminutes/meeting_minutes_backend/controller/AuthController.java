package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import com.meetingminutes.meeting_minutes_backend.security.JwtService;
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

    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token, String fullName, String email, String role) {}

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .filter(a -> passwordEncoder.matches(request.password(), a.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        String token = jwtService.generateToken(admin.getEmail(), admin.getRole());
        return new LoginResponse(token, admin.getFullName(), admin.getEmail(), admin.getRole());
    }
}