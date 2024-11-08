package com.project.bookMembership.visitPackage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VisitPackageRepo extends JpaRepository<VisitPackage,Long> {


    @Query("""
    SELECT v FROM VisitPackage v
    JOIN v.transaction t
    WHERE v.user.idUser = :userId
    AND t.paymentStatus = 'VERIFIED'
""")
    List<VisitPackage> findByUserId(@Param("userId") Long userId);
}