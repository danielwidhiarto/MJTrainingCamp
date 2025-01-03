package com.project.bookMembership.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddNotesRequest {
    private String token;
    private String notes;
    private long idClass;
}
