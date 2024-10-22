package com.project.bookMembership.transaction;

import java.util.List;
import java.util.Optional;

import com.project.bookMembership.DTO.UpdateTransactionStatusRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.bookMembership.DTO.GetTransactionResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

//     @PostMapping("/get")
//     public ResponseEntity<GetTransactionResponse> getTransaction(@RequestParam Long id) {
//         Optional<GetTransactionResponse> transactionResponse = transactionService.getById(id);
//
//         if (transactionResponse.isPresent()) {
//
//             return ResponseEntity.ok(transactionResponse.get());
//         } else {
//
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//         }
//        // return ResponseEntity.of(transactionResponse);
//}


    @GetMapping("/get")
    public ResponseEntity<?> getAllTransactions(@RequestParam(required = false) Long id) {
        if (id != null) {
            // Fetch by ID
            Optional<GetTransactionResponse> transactionResponse = transactionService.getById(id);

            if (transactionResponse.isPresent()) {
                return ResponseEntity.ok(transactionResponse.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            // Fetch all transactions
            List<GetTransactionResponse> transactionResponses = transactionService.getAllTransactions();

            if (transactionResponses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(transactionResponses);
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateTransactionStatus(@PathVariable Long id, @RequestBody UpdateTransactionStatusRequest updateRequest) {
        try {
            // Update the transaction status
            transactionService.updateTransactionStatus(id, updateRequest.getTransactionStatus());
            return ResponseEntity.ok("Transaction status updated successfully");
        } catch (RuntimeException ex) {
            // Handle the exception and return a BAD_REQUEST status with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
