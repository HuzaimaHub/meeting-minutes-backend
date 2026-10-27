package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.service.BrandStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/brand")
@RequiredArgsConstructor
public class PublicBrandController {

    private final BrandStorageService storage;

    public record BrandInfo(boolean custom, long version, String systemName,
                            String orgName, String orgNameSw, String orgShort) {}

    @GetMapping
    public BrandInfo info() throws IOException {
        Optional<Path> logo = storage.findLogo();
        BrandStorageService.Org org = storage.getOrg();
        boolean custom = logo.isPresent();
        long version = custom ? Files.getLastModifiedTime(logo.get()).toMillis() : 0;
        return new BrandInfo(custom, version, storage.getSystemName(),
                org.name(), org.nameSw(), org.shortName());
    }

    @GetMapping("/logo")
    public ResponseEntity<byte[]> logo() throws IOException {
        Path logo = storage.findLogo()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No custom logo"));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storage.contentType(logo)))
                .cacheControl(CacheControl.noCache())
                .body(Files.readAllBytes(logo));
    }
}