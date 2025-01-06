package com.project.bookMembership.transaction;

import java.util.List;
import java.util.Optional;

import com.project.bookMembership.DTO.AdminDashboardDTO;
import com.project.bookMembership.DTO.GetUserTransactionRequest;
import com.project.bookMembership.DTO.UpdateTransactionStatusRequest;
import com.project.bookMembership.membership.MembershipRepo;
import com.project.bookMembership.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.bookMembership.DTO.GetTransactionResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;
    private final MembershipRepo membershipRepo;
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
    @PostMapping("/getByUser")
    public ResponseEntity<?> getTransactionByUser(@RequestBody GetUserTransactionRequest request) {

            List<GetTransactionResponse> transactionResponse = transactionService.getByUser(request);

            return ResponseEntity.ok(transactionResponse);

    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateTransactionStatus(@PathVariable Long id, @RequestBody UpdateTransactionStatusRequest updateRequest) {
        try {
            // Update the transaction status
            transactionService.updateTransactionStatus(id, updateRequest.getTransactionStatus(),updateRequest.getNotes());
            return ResponseEntity.ok("Transaction status updated successfully");
        } catch (RuntimeException ex) {
            // Handle the exception and return a BAD_REQUEST status with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/adminDashBoard")
    public ResponseEntity<AdminDashboardDTO> countPendingTransaction() {
        int totaluser= userRepo.countAllUsers();
        long activeMemberThisMonth= membershipRepo.countActiveMembershipThisMonth();


        long thisMonthTransaction=transactionRepo.countTransactionThisMonth();
        long pendingTransaction= transactionRepo.countWaitingForApproval();
        long countTransactionSucess=transactionRepo.countTransactionSucess();
        long totaltransaction=transactionRepo.countTotalTransaction();

        // Creating the AdminDashboardDTO object with the fetched data
        AdminDashboardDTO dashboardDTO = AdminDashboardDTO.builder()
                .totalUser(totaluser)
                .activeMemberThisMonth(activeMemberThisMonth)
                .thisMonthTransaction(thisMonthTransaction)
                .pendingTransaction(pendingTransaction)
                .countTransactionSuccess(countTransactionSucess)
                .totalSuccesTransactionRevenue(totaltransaction)
                .build();

        // Returning the response with the AdminDashboardDTO object
        return ResponseEntity.ok(dashboardDTO);
    }
}
