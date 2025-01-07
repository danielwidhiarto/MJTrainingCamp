package com.project.bookMembership.transaction;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.project.bookMembership.DTO.*;
import com.project.bookMembership.classes.TrainingClass;
import com.project.bookMembership.classes.TrainingClassRepo;
import com.project.bookMembership.membership.MembershipRepo;
import com.project.bookMembership.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final TrainingClassRepo trainingClassRepo;
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
            transactionService.updateTransactionStatus(id, updateRequest.getTransactionStatus(), updateRequest.getNotes());
            return ResponseEntity.ok("Transaction status updated successfully");
        } catch (RuntimeException ex) {
            // Handle the exception and return a BAD_REQUEST status with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/adminDashBoard")
    public ResponseEntity<AdminDashboardDTO> countPendingTransaction() {
        // Fetching the required data from repositories
        int totalUser = userRepo.countAllUsers();
        long activeMemberThisMonth = membershipRepo.countActiveMembershipThisMonth();
        long thisMonthTransaction = transactionRepo.countTransactionThisMonth();
        long pendingTransaction = transactionRepo.countWaitingForApproval();
        long countTransactionSuccess = transactionRepo.countTransactionSucess();
        long totalTransaction = transactionRepo.countTotalTransaction();

        // Fetching attendance for today and the last 6 days
        Date today = new Date();
        long todayAttendance = trainingClassRepo.countTotalAttendanceToday(today);

        // Getting attendance for yesterday (today - 1 day)
        Date yesterday = getPreviousDate(1);
        long dmin1Attendance = trainingClassRepo.countTotalAttendanceToday(yesterday);

        // Getting attendance for today - 2 days
        Date twoDaysAgo = getPreviousDate(2);
        long dmin2Attendance = trainingClassRepo.countTotalAttendanceToday(twoDaysAgo);

        // Getting attendance for today - 3 days
        Date threeDaysAgo = getPreviousDate(3);
        long dmin3Attendance = trainingClassRepo.countTotalAttendanceToday(threeDaysAgo);

        // Getting attendance for today - 4 days
        Date fourDaysAgo = getPreviousDate(4);
        long dmin4Attendance = trainingClassRepo.countTotalAttendanceToday(fourDaysAgo);

        // Getting attendance for today - 5 days
        Date fiveDaysAgo = getPreviousDate(5);
        long dmin5Attendance = trainingClassRepo.countTotalAttendanceToday(fiveDaysAgo);

        // Getting attendance for today - 6 days
        Date sixDaysAgo = getPreviousDate(6);
        long dmin6Attendance = trainingClassRepo.countTotalAttendanceToday(sixDaysAgo);

        // Creating the GetTotalAttendance object with attendance data
        GetTotalAttendance attendance = GetTotalAttendance.builder()
                .today(todayAttendance)
                .dmin1(dmin1Attendance)
                .dmin2(dmin2Attendance)
                .dmin3(dmin3Attendance)
                .dmin4(dmin4Attendance)
                .dmin5(dmin5Attendance)
                .dmin6(dmin6Attendance)
                .build();

        // Creating the AdminDashboardDTO object with the fetched data
        AdminDashboardDTO dashboardDTO = AdminDashboardDTO.builder()
                .totalUser(totalUser)
                .activeMemberThisMonth(activeMemberThisMonth)
                .thisMonthTransaction(thisMonthTransaction)
                .pendingTransaction(pendingTransaction)
                .countTransactionSuccess(countTransactionSuccess)
                .totalSuccesTransactionRevenue(totalTransaction)
                .totalAttendanceList(attendance)
                .build();

        // Returning the response with the AdminDashboardDTO object
        return ResponseEntity.ok(dashboardDTO);
    }

    // Helper method to get the date for today - X days
    private Date getPreviousDate(int daysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);  // Subtract the number of days
        return calendar.getTime();
    }
}