package com.project.bookMembership.packageList;

import java.util.List;

public interface PackageService {
    
        List<PackageList> getAll();
        PackageList save(PackageRequest packageRequest); 

}
