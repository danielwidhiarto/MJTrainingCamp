package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassBookingStatusResponse {
    private boolean validMember;
    private boolean validVisit;
    private boolean alreadyBooked;


}
