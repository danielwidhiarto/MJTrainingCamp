package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTotalAttendance {

    private long today;
    private long dmin1;
    private long dmin2;
    private long dmin3;
    private long dmin4;
    private long dmin5;
    private long dmin6;

}

