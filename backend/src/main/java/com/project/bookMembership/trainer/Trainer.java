package com.project.bookMembership.trainer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainer")
public class Trainer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idTrainer;

    private String trainerName;
    private String trainerDescription;
         


}
