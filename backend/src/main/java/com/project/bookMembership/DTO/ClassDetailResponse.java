package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailResponse {



    private Long idUser;
    private String PNumber;
    private String name;
    private String email;
//    private Long idClass;
    
}
