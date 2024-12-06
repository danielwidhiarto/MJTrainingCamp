package com.project.bookMembership.visitPackage;

import com.project.bookMembership.DTO.MembershipRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.bookMembership.DTO.VisitPackageRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/visitpackage")
@RequiredArgsConstructor
@CrossOrigin
public class VisitPackageController {


    private final VisitPackageService visitPackageService;

    @PostMapping("/buy")
    public ResponseEntity<String> addClass(@ModelAttribute VisitPackageRequest visitPackageRequest, @RequestPart("buktiTransfer") MultipartFile buktiTransfer) {

        try {
            // Example check for empty file
            if (buktiTransfer.isEmpty()) {
                throw new RuntimeException("Bukti Transfer Is required");
            }

            // Save the buy visit request
            visitPackageService.save(visitPackageRequest);

            return ResponseEntity.ok("Buy visit successfully");

        } catch (RuntimeException e) {
            // Log the exception (optional)
            System.err.println("Error: " + e.getMessage());

            // Return a bad request status with error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
