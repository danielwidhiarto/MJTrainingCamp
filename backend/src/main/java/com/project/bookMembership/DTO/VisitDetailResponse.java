package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitDetailResponse {
    private Long idVisit;
    private Long price;
    private Long visitNumber;
    private Long visitUsed;
    private Long transactionId;
    private Long userId;
}
