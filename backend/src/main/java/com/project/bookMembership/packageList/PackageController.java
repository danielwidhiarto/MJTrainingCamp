package com.project.bookMembership.packageList;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.bookMembership.DTO.PackageRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/package")
@RequiredArgsConstructor
public class PackageController {

      private final PackageService packageService;
      private final PackageRepo packageRepo;

    @GetMapping("/get")
    public ResponseEntity<?> getPackage(@RequestParam(required = false) Long id) {
        // If ID is provided, get the package by ID
        if (id != null) {
            Optional<PackageList> packageById = packageRepo.findById(id);

            if (packageById.isPresent()) {
                return ResponseEntity.ok(packageById.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Package not found with ID: " + id);
            }
        }

        // If no ID is provided, return all packages
        List<PackageList> packageList = packageService.getAll();

        if (packageList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(packageList);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPackage(@RequestBody PackageRequest packageRequest) {
        PackageList savedPackage = packageService.save(packageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(savedPackage);
    }
}
