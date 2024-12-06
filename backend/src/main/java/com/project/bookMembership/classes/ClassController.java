package com.project.bookMembership.classes;


import java.util.List;

import java.util.ArrayList;
import java.util.Date;

import com.project.bookMembership.DTO.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/class")
@RequiredArgsConstructor
@CrossOrigin
public class ClassController {

    private final ClassService classService;
    private final ClassDetailService classDetailService;


    @PostMapping("/check-eligibility")
    public ResponseEntity<ClassBookingStatusResponse> checkBookingEligibility(@RequestBody ClassDetailRequest classDetailRequest) {
        ClassBookingStatusResponse response = classDetailService.checkBookingEligibility(classDetailRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/checkMembership")
    public ResponseEntity<MembershipStatusResponse> checkMembership(@RequestBody MembershipStatusRequest membershipStatusRequest) {
        MembershipStatusResponse response = classDetailService.checkMembershipStatus(membershipStatusRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClass(@RequestBody ClassRequest classRequest) {

        try {
            classService.save(classRequest);
            return ResponseEntity.ok("Class added successfully");
            } catch (RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); 
            }
    }

    @PostMapping("/book")
    public ResponseEntity<String> book(@RequestBody ClassDetailRequest classDetailRequest) {

        try {
        classDetailService.book(classDetailRequest);
        return ResponseEntity.ok("Class booked successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); 
        }
    }


    @GetMapping("/getClasses")
    public ResponseEntity<List<GetClassResponse>> getClasses(
            @RequestParam(required = false) Long id,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date date) {

        List<GetClassResponse> classResponses = new ArrayList<>();

        if (id != null) {
            classResponses = classService.getTrainingClassById(id);

        } else if (date != null) {
            classResponses = classService.getTrainingClassByDate(date);

        } else {
            classResponses = classService.getTrainingClass(); //
        }

        return ResponseEntity.ok(classResponses);
    }

    
    @PostMapping("/getHistory")
    public ResponseEntity<List<GetClassResponse>> getHistory(@RequestBody ClassHistoryRequest classHistoryRequest) {


        List<GetClassResponse> classResponses = new ArrayList<>();
        classResponses = classService.getClassHistory(classHistoryRequest);


        return ResponseEntity.ok(classResponses);

        }

    }

    


//    @GetMapping("/getClasses")
//    public ResponseEntity<List<GetClassResponse>> getClasses(@RequestParam(required = false) Long id,
//                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date date) {
//
//        List<TrainingClass> trainingClasses = new ArrayList<>();
//
//        if (id != null) {
//            trainingClasses = classService.getTrainingClassById(id);
//        } else if (date != null) {
//            trainingClasses = classService.getTrainingClassByDate(date);
//        } else {
//            trainingClasses = classService.getTrainingClass();
//        }
//
//
//
//        List<GetClassResponse> responseList = trainingClasses.stream()
//            .map(trainingClass -> GetClassResponse.builder()
//                .idClass(trainingClass.getIdClass())
//                .className((trainingClass.getClassName()))
//                .classRequirement(trainingClass.getClassRequirement())
//                .classDate(trainingClass.getClassDate())
//                .classTime(trainingClass.getClassTime())
//                .classCapasity(trainingClass.getClassCapasity())
//
//                .build())
//            .collect(Collectors.toList());
//
//
//        return ResponseEntity.ok(responseList);
//    }
