package com.project.bookMembership.transaction;

import java.util.List;
import java.util.Optional;

import com.project.bookMembership.DTO.GetTransactionResponse;
import com.project.bookMembership.DTO.GetUserTransactionRequest;

public interface TransactionService {
    Transaction save(Transaction transaction);
    Optional<GetTransactionResponse> getById(Long id);
    List<GetTransactionResponse> getByUser(GetUserTransactionRequest request);
    List<GetTransactionResponse> getAllTransactions();
    Transaction updateTransactionStatus(Long id, String newStatus,String notes);
}
