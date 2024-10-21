package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionResponse {

    private Long idTransaction;

    private String paymentType;
    private String paymentMethod;
    private String paymentStatus;
    private Long transactionPrice;
    private byte[] buktiTransfer;
}
