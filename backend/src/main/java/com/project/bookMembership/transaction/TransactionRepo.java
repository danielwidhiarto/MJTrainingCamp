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

    @Query("""
    SELECT t FROM Transaction t 
    LEFT JOIN t.membership m 
    LEFT JOIN t.visitPackage v 
    WHERE (m.user.idUser = :userId OR v.user.idUser = :userId)
    """)
    List<Transaction> findByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.paymentStatus = 'WAITING FOR APPROVAL'")
    long countWaitingForApproval();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE MONTH(t.transactionDate) = MONTH(CURRENT_DATE) AND YEAR(t.transactionDate) = YEAR(CURRENT_DATE)")
    long countTransactionThisMonth();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.paymentStatus = 'VERIFIED'")
    long countTransactionSucess();

    @Query("SELECT COALESCE(SUM(t.transactionPrice), 0) FROM Transaction t WHERE t.paymentStatus = 'VERIFIED' AND EXTRACT(MONTH FROM t.transactionDate) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM t.transactionDate) = EXTRACT(YEAR FROM CURRENT_DATE)")
    long countTotalTransaction();

}