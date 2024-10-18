package com.project.bookMembership.packageList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepo extends JpaRepository<PackageList,Long> {
    
    
}
