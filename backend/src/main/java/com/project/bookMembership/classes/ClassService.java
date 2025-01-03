package com.project.bookMembership.classes;

import java.util.List;

import com.project.bookMembership.DTO.AddNotesRequest;
import com.project.bookMembership.DTO.ClassHistoryRequest;
import com.project.bookMembership.DTO.ClassRequest;
import com.project.bookMembership.DTO.GetClassResponse;

import java.util.Date;


public interface ClassService {
    TrainingClass save(ClassRequest request);
    List<GetClassResponse> getTrainingClass();
    List<GetClassResponse> getTrainingClassById(Long id);
    List<GetClassResponse> getTrainingClassByDate(Date date);
    List<GetClassResponse> getClassHistory(ClassHistoryRequest classHistoryRequest);
    String addNotes(AddNotesRequest request);
    
} 