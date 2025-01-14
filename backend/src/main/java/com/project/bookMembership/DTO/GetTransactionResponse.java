package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionResponse {

    private Long idTransaction;

    private String paymentType;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentNotes;
    private Long transactionPrice;
    private Long membershipId;
    private  MembershipDetailResponse membershipDetail;
    private VisitDetailResponse visitDetail;
    private Long visitId;
    private String memberName;
    private byte[] buktiTransfer;
    private Date transactionDate;


}
