package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageRequest {
    
    private String packageName;
    private Long price;
    private int duration;
    private String status;
    private String type;
    private int visitNumber;
}
