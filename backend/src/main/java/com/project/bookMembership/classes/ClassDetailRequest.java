package com.project.bookMembership.classes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailRequest {
    
    
    private Long idClass;
    private String token;

    private String type;

    
}
