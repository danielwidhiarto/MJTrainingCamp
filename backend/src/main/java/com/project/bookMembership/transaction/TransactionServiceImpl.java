package com.project.bookMembership.transaction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;


import com.project.bookMembership.membership.Membership;
import com.project.bookMembership.membership.MembershipRepo;
import com.project.bookMembership.user.User;
import com.project.bookMembership.visitPackage.VisitPackage;
import com.project.bookMembership.visitPackage.VisitPackageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bookMembership.DTO.GetTransactionResponse;

import utils.ImageUtils;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepository;
    private final MembershipRepo membershipRepo;
    private final VisitPackageRepo visitPackageRepo;
    @Autowired
    public TransactionServiceImpl(TransactionRepo transactionRepository,VisitPackageRepo visitPackageRepo,MembershipRepo membershipRepo) {
        this.transactionRepository = transactionRepository;
        this.visitPackageRepo = visitPackageRepo;
        this.membershipRepo = membershipRepo;
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


            if (tx.getMembership() != null) {
                Membership member = tx.getMembership();
                User user = member.getUser();

                    response.setMemberName(user.getName());
                    response.setMembershipId(member.getIdMember());

            }

            // Handle VisitPackage if it is not null
            if (tx.getVisitPackage() != null) {
                VisitPackage visitPackage = tx.getVisitPackage();
                User visitUser = visitPackage.getUser();
                if (visitUser != null) {
                    response.setMemberName(visitUser.getName());
                }


                response.setVisitId(visitPackage.getIdVisit());
            }

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

            if (tx.getMembership() != null) {
                Membership member = tx.getMembership();
                User user = member.getUser();

                response.setMemberName(user.getName());
                response.setMembershipId(member.getIdMember());

            }

            // Handle VisitPackage if it is not null
            if (tx.getVisitPackage() != null) {
                VisitPackage visitPackage = tx.getVisitPackage();
                User visitUser = visitPackage.getUser();
                if (visitUser != null) {
                    response.setMemberName(visitUser.getName());
                }


                response.setVisitId(visitPackage.getIdVisit());
            }


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