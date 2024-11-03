package com.project.bookMembership.transaction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bookMembership.DTO.GetTransactionResponse;

import utils.ImageUtils;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepo transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    @Override
    public Optional<GetTransactionResponse> getById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);

        return transaction.map(tx -> {
            GetTransactionResponse response = new GetTransactionResponse();
            response.setIdTransaction(tx.getIdTransaction());
            response.setPaymentMethod(tx.getPaymentMethod());
            response.setPaymentType(tx.getPaymentType());
            response.setTransactionPrice(tx.getTransactionPrice());
            response.setPaymentStatus(tx.getPaymentStatus());


            try {
                byte[] decompressedImage = ImageUtils.decompressImage(tx.getBuktiTransfer());
                response.setBuktiTransfer(decompressedImage);
                return response; 
            } catch (DataFormatException | java.io.IOException exception) {
                
                throw new RuntimeException("Error decompressing image for Transaction ID: " + tx.getIdTransaction(), exception);
            } 
        });
    }

    @Override
    public List<GetTransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream().map(tx -> {
            GetTransactionResponse response = new GetTransactionResponse();
            response.setIdTransaction(tx.getIdTransaction());
            response.setPaymentMethod(tx.getPaymentMethod());
            response.setPaymentType(tx.getPaymentType());
            response.setTransactionPrice(tx.getTransactionPrice());
            response.setPaymentStatus(tx.getPaymentStatus());


//            if (tx.getBuktiTransfer() != null) {
//                try {
//                    byte[] decompressedImage = ImageUtils.decompressImage(tx.getBuktiTransfer());
//                    response.setBuktiTransfer(decompressedImage);
//                } catch (DataFormatException | IOException exception) {
//                    throw new RuntimeException("Error decompressing image for Transaction ID: " + tx.getIdTransaction(), exception);
//                }
//            } else {
                response.setBuktiTransfer(null);
//            }

            return response;
        }).collect(Collectors.toList());
    }
    public Transaction updateTransactionStatus(Long id, String newStatus) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setPaymentStatus(newStatus);
            return transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
    }

}