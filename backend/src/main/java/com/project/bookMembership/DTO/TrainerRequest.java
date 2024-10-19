package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerRequest {

    private String trainerName;
    private String trainerDescription;


    private String name;
    private String email;
    private String password;
  

    private Long idTrainer;
    private String pNumber;

    public String getpNumber() {
        return this.pNumber;
    }
}