package com.project.bookMembership.classes;

import java.util.List;

import com.project.bookMembership.DTO.ClassHistoryRequest;
import com.project.bookMembership.DTO.ClassRequest;

import java.util.Date;


public interface ClassService {
    TrainingClass save(ClassRequest request);
    List<TrainingClass> getTrainingClass();
    List<TrainingClass> getTrainingClassById(Long id);
    List<TrainingClass> getTrainingClassByDate(Date date);
    List<TrainingClass> getClassHistory(ClassHistoryRequest classHistoryRequest);
    
    
} 