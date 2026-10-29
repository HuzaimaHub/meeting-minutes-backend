package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.security.JwtService;
import com.meetingminutes.meeting_minutes_backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;
    private final JwtService jwtService;

    public record UpdateProfileRequest(String fullName, String email, String currentPassword) {}
    public record ChangePasswordRequest(String currentPassword, String newPassword) {}

    @GetMapping
    public ProfileService.ProfileView get(Authentication authentication) {
        return service.get(authentication.getName());
    }

    // Returns a fresh token, because the token's subject is the email address
    @PutMapping
    public AuthController.LoginResponse update(@RequestBody UpdateProfileRequest request,
                                               Authentication authentication) {
        Admin admin = service.updateProfile(authentication.getName(), request.fullName(),
                request.email(), request.currentPassword());
        String token = jwtService.generateToken(admin.getEmail(), admin.getRole());
        return new AuthController.LoginResponse(token, admin.getFullName(),
                admin.getEmail(), admin.getRole());
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody ChangePasswordRequest request,
                               Authentication authentication) {
        service.changePassword(authentication.getName(), request.currentPassword(),
                request.newPassword());
    }
}