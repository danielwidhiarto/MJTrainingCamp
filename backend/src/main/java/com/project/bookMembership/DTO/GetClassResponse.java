package com.project.bookMembership.DTO;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import com.project.bookMembership.trainer.Trainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class GetClassResponse {

    
    private Long idClass;

    private Date classDate;
    private LocalTime classTime;
    private Long classCapasity;
    private String classRequirement;
    private String className;

    private TrainerDetailResponse trainerDetail;
    private String classNote;


     private List<ClassDetailResponse> classMembers;
}
