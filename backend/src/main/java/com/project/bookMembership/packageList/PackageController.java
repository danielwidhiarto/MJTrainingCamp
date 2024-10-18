package com.project.bookMembership.packageList;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/package")
@RequiredArgsConstructor
public class PackageController {

      private final PackageService packageService;

      @GetMapping("/get")
      public ResponseEntity<List<PackageList>> getPackage() {
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
