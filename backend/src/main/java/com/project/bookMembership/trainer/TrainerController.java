package com.project.bookMembership.trainer;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

import com.project.bookMembership.DTO.TrainerRequest;
import com.project.bookMembership.DTO.TrainerResponse;
import com.project.bookMembership.DTO.TrainerScheduleRequest;
import com.project.bookMembership.DTO.TrainerScheduleResponse;
import com.project.bookMembership.auth.AuthenticationService;
import com.project.bookMembership.classes.TrainingClass;
import com.project.bookMembership.user.User;
import com.project.bookMembership.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/v1/trainer")
@RequiredArgsConstructor
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
    public ResponseEntity<List<TrainerScheduleResponse>> getSchedule(@RequestBody TrainerScheduleRequest trainerScheduleRequest) {
        List<TrainingClass> trainingClasses = new  ArrayList<>();
        trainingClasses= trainerService.getTrainerSchedule(trainerScheduleRequest); 

        List<TrainerScheduleResponse> responseList = trainingClasses.stream()
        .map(trainingClass -> TrainerScheduleResponse.builder()
                .idClass(trainingClass.getIdClass())
                .className((trainingClass.getClassName()))
                .classRequirement(trainingClass.getClassRequirement())
                .classDate(trainingClass.getClassDate())
                .classTime(trainingClass.getClassTime())
                .classCapasity(trainingClass.getClassCapasity())
                
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
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
                
                User user = userService.getUserByTrainerId(idtrainer)
                        .orElseThrow(() -> new RuntimeException("User not found for trainer: " + trainer.getIdTrainer()));


                return TrainerResponse.builder()
                        .idTrainer(trainer.getIdTrainer())    
                        .trainerName(trainer.getTrainerName()) 
                        .trainerDescription(trainer.getTrainerDescription())
                        .email(user.getEmail())
                        .PNumber(user.getPNumber())
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