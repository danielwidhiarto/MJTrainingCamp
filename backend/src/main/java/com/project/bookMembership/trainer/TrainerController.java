package com.project.bookMembership.trainer;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.project.bookMembership.DTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import com.project.bookMembership.auth.AuthenticationService;

import com.project.bookMembership.user.User;
import com.project.bookMembership.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/v1/trainer")
@RequiredArgsConstructor
@CrossOrigin
public class TrainerController {
    
    private final TrainerService trainerService;
    private final AuthenticationService service;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addClass(@RequestBody TrainerRequest trainerRequest) {
        
       Trainer trainer = trainerService.save(trainerRequest); 
       Long idTrainer = trainer.getIdTrainer();
       trainerRequest.setIdTrainer(idTrainer);
       
       service.registerTrainer(trainerRequest);
       
        return ResponseEntity.ok("Trainer added successfully");
    }

    @PostMapping("/getschedule")
    public ResponseEntity<List<GetClassResponse>> getSchedule(@RequestBody TrainerScheduleRequest trainerScheduleRequest) {
        List<GetClassResponse> classResponses = new ArrayList<>();
        classResponses= trainerService.getTrainerSchedule(trainerScheduleRequest);


        return ResponseEntity.ok(classResponses);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<TrainerResponse>> getAllTrainers() {
    try {
        // Get all trainers
        List<Trainer> trainers = trainerService.getAll(); 

        // Create a response list
        List<TrainerResponse> responseList = trainers.stream()
                .map(trainer -> {
                    Long idtrainer = trainer.getIdTrainer();
                    System.out.println("Fetching user for trainer ID: " + idtrainer);

                    User user = userService.getUserByTrainerId(idtrainer).orElse(null);

                    return TrainerResponse.builder()
                            .idTrainer(trainer.getIdTrainer())
                            .trainerName(trainer.getTrainerName())
                            .trainerDescription(trainer.getTrainerDescription())
                            .email(user != null ? user.getEmail() : "N/A")
                            .PNumber(user != null ? user.getPNumber() : "N/A")
                            .build();
                })
                .collect(Collectors.toList());

        // Return the response
        return ResponseEntity.ok(responseList);

    } catch (RuntimeException e) {

        e.printStackTrace(); // Log the exception for debugging
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.emptyList()); // Or return an error message
    }
 }
}