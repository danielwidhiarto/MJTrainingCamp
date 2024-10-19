package com.project.bookMembership.transaction;

import java.util.Optional;

import com.project.bookMembership.DTO.GetTransactionResponse;

public interface TransactionService {
    Transaction save(Transaction transaction);
    Optional<GetTransactionResponse> getById(Long id);
}
