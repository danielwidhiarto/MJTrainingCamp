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

import com.project.bookMembership.auth.AuthenticationService;
import com.project.bookMembership.classes.TrainingClass;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/api/v1/trainer")
@RequiredArgsConstructor
public class TrainerController {
    
    private final TrainerService trainerService;
    private final AuthenticationService service;

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
        // Fetch all trainers from the service
        List<Trainer> trainers = trainerService.getAll(); 

        // Map trainers to TrainerResponse (assuming TrainerResponse is defined)
        List<TrainerResponse> responseList = trainers.stream()
            .map(trainer -> TrainerResponse.builder()
                    .idTrainer(trainer.getIdTrainer())     // Assuming idTrainer is a field in Trainer
                    .trainerName(trainer.getTrainerName()) // Assuming trainerName is a field in Trainer
                    .trainerDescription(trainer.getTrainerDescription()) // Assuming trainerDescription is a field in Trainer
                    .build())
            .collect(Collectors.toList());

        // Return the response
        return ResponseEntity.ok(responseList);

    } catch (RuntimeException e) {
        // Handle runtime exceptions and return a 500 response
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.emptyList()); // Or return an error message
    }
}
 
}