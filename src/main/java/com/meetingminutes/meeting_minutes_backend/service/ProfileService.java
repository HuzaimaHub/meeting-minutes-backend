package com.meetingminutes.meeting_minutes_backend.service;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public record ProfileView(Integer adminId, String fullName, String email, String role,
                              String gender, LocalDateTime createdAt) {}

    public ProfileView get(String email) {
        Admin a = current(email);
        return new ProfileView(a.getAdminId(), a.getFullName(), a.getEmail(), a.getRole(),
                a.getGender(), a.getCreatedAt());
    }

    @Transactional
    public Admin updateProfile(String currentEmail, String fullName, String newEmail,
                               String currentPassword) {
        Admin admin = current(currentEmail);

        String name = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        if (name.length() < 2 || name.length() > 100) {
            throw bad("The full name must be between 2 and 100 characters.");
        }

        String email = newEmail == null ? "" : newEmail.trim();
        if (!email.equalsIgnoreCase(admin.getEmail())) {
            if (!EMAIL.matcher(email).matches() || email.length() > 100) {
                throw bad("Enter a valid email address (up to 100 characters).");
            }
            if (currentPassword == null
                    || !passwordEncoder.matches(currentPassword, admin.getPassword())) {
                throw bad("Enter your current password to change your email.");
            }
            adminRepository.findByEmail(email)
                    .filter(other -> !other.getAdminId().equals(admin.getAdminId()))
                    .ifPresent(other -> {
                        throw bad("That email address is already in use.");
                    });
            admin.setEmail(email);
        }

        admin.setFullName(name);
        return adminRepository.save(admin);
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        Admin admin = current(email);
        if (currentPassword == null
                || !passwordEncoder.matches(currentPassword, admin.getPassword())) {
            throw bad("Your current password is incorrect.");
        }
        PasswordPolicy.check(newPassword);
        if (passwordEncoder.matches(newPassword, admin.getPassword())) {
            throw bad("The new password must be different from the current one.");
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }

    private Admin current(String email) {
        return adminRepository.findByEmail(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}