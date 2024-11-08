package com.project.bookMembership.membership;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepo extends JpaRepository<Membership,Long>{
    @Query("""
    SELECT m FROM Membership m 
    WHERE m.user.idUser = :userId 
    AND m.transaction.paymentStatus = 'VERIFIED'
    """)
    List<Membership> findByUserId(@Param("userId") Long userId);
}
