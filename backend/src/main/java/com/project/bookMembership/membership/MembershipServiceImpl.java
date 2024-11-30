package com.project.bookMembership.membership;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bookMembership.DTO.MembershipRequest;
import com.project.bookMembership.config.JwtService;
import com.project.bookMembership.transaction.Transaction;
import com.project.bookMembership.transaction.TransactionService;
import com.project.bookMembership.user.User;
import com.project.bookMembership.user.UserRepo;
import java.io.IOException;
import utils.ImageUtils;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepo membershipRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final TransactionService transactionService;

    @Autowired
    public MembershipServiceImpl(MembershipRepo membershipRepo,
                                UserRepo userRepo,
                                JwtService jwtService,
                                TransactionService transactionService) {
        this.membershipRepo = membershipRepo;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.transactionService = transactionService;
    }

    @Override
public Membership save(MembershipRequest membershipRequest) {
        try {
    String emailz = jwtService.extractUsername(membershipRequest.getToken());
    User user = userRepo.findByEmail(emailz)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Transaction transaction = Transaction.builder()
        //     .visitStartDate(membershipRequest.getVisitStartDate())
        //     .visitEndDate(membershipRequest.getVisitEndDate())
             .paymentType("Membership")
            .paymentMethod(membershipRequest.getPaymentMethod())
            .paymentStatus("WAITING FOR APPROVAL")
            .transactionPrice(membershipRequest.getTransactionPrice())
            .buktiTransfer(ImageUtils.compressImage(membershipRequest.getBuktiTransfer().getBytes()))
            .build();

            System.out.println("Transaction bukti_transfer (length): " + transaction.getBuktiTransfer());

    transactionService.save(transaction);

    Membership membership = Membership.builder()
            .user(user)
            .transaction(transaction)
            .startDate(membershipRequest.getStartDate())
            .endDate(membershipRequest.getEndDate())
            .price(membershipRequest.getPrice())
            .duration(membershipRequest.getDuration())
            .build();


    Membership savedMembership = membershipRepo.save(membership);

    transaction.setMembership(membership);

    transactionService.save(transaction);


    return savedMembership;

} catch (IOException e) {
        throw new RuntimeException("Error processing image", e);
    }
}}
