package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TrainerResponse {
    private Long idTrainer;
    private String trainerName;
    private String trainerDescription;
    private String email; 
    private String pNumber; 
}