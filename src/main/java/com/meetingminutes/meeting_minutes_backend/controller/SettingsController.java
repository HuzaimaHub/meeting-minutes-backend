package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.service.BrandStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final BrandStorageService storage;

    public record SystemNameRequest(String name) {}
    public record OrgRequest(String name, String nameSw, String shortName) {}

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam("file") MultipartFile file) throws IOException {
        storage.saveLogo(file);
    }

    @DeleteMapping("/logo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetLogo() throws IOException {
        storage.deleteLogo();
    }

    @PutMapping("/system-name")
    public void saveSystemName(@RequestBody SystemNameRequest request) throws IOException {
        storage.saveSystemName(request.name());
    }

    @DeleteMapping("/system-name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetSystemName() throws IOException {
        storage.resetSystemName();
    }

    @PutMapping("/org")
    public void saveOrg(@RequestBody OrgRequest request) throws IOException {
        storage.saveOrg(request.name(), request.nameSw(), request.shortName());
    }

    @DeleteMapping("/org")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetOrg() throws IOException {
        storage.resetOrg();
    }
}