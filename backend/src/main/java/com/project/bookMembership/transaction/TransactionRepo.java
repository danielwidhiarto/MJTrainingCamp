package com.project.bookMembership.transaction;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionRepo  extends JpaRepository<Transaction,Long> {

    @Query("SELECT t FROM Transaction t WHERE t.membership.idMember = :membershipId")
    List<Transaction> findByMembershipId(@Param("membershipId") Long membershipId);

    @Query("SELECT t FROM Transaction t WHERE t.membership.user.idUser = :userId ")
    List<Transaction> findByUser( @Param("userId") Long userId);
}