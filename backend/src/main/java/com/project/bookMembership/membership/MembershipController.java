package com.project.bookMembership.membership;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.bookMembership.DTO.MembershipRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/buy")
    public ResponseEntity<String> buymembership(
            @ModelAttribute MembershipRequest membershipRequest,
            @RequestPart("buktiTransfer") MultipartFile buktiTransfer) {

        try {
            if (buktiTransfer.isEmpty()) {
                throw new RuntimeException("Bukti Transfer Is required");
            }
            membershipService.save(membershipRequest);

            return ResponseEntity.ok("Buy membership successfully");

        } catch (RuntimeException e) {

            System.err.println("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    


}
