package com.project.bookMembership.membership;


import java.util.Date;

import com.project.bookMembership.transaction.Transaction;
import com.project.bookMembership.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "membership")
public class Membership {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idMember;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;

    private Date startDate;
    private Date endDate;
    private Long price;
    private Long duration;
    
}
