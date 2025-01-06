package com.project.bookMembership.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {

    private int totalUser;
    private long activeMemberThisMonth;
    private long thisMonthTransaction;
    private long pendingTransaction;
    private long countTransactionSuccess;
    private long totalSuccesTransactionRevenue;

}
