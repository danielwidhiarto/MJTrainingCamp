package com.project.bookMembership.trainer;
import java.util.List;

import com.project.bookMembership.DTO.GetClassResponse;
import com.project.bookMembership.DTO.TrainerRequest;
import com.project.bookMembership.DTO.TrainerScheduleRequest;
import com.project.bookMembership.classes.TrainingClass;

public interface TrainerService {
    Trainer save(TrainerRequest request);
    List<GetClassResponse> getTrainerSchedule(TrainerScheduleRequest trainerScheduleRequest);
    Trainer findTrainerById(Long idTrainer);
    List<Trainer> getAll();

} 