package com.project.bookMembership.trainer;
import java.util.List;
import com.project.bookMembership.classes.TrainingClass;

public interface TrainerService {
    Trainer save(TrainerRequest request);
    List<TrainingClass> getTrainerSchedule(TrainerScheduleRequest trainerScheduleRequest);
    Trainer findTrainerById(Long idTrainer);
    List<Trainer> getAll();

} 