package com.project.bookMembership.packageList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bookMembership.DTO.PackageRequest;
@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepo packageRepo;

    @Autowired
    public PackageServiceImpl(PackageRepo packageRepo) {
        this.packageRepo = packageRepo;
    }

    @Override
    public List<PackageList> getAll() {
        return packageRepo.findAll();
    }

 
    @Override
public PackageList save(PackageRequest request) {
   
    PackageList packageList = PackageList.builder()
        .packageName(request.getPackageName())
        .price(request.getPrice())
        .visitNumber(request.getVisitNumber())
        .type(request.getType())
        .status(request.getStatus())
        .duration(request.getDuration())
        .build();
    

    return packageRepo.save(packageList);
}
}