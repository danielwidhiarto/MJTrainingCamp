package com.project.bookMembership.packageList;

import java.util.List;

import com.project.bookMembership.DTO.PackageRequest;

public interface PackageService {
    
        List<PackageList> getAll();
        PackageList save(PackageRequest packageRequest); 

}
