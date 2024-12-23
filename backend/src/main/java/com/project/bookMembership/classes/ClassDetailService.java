package com.project.bookMembership.classes;

import com.project.bookMembership.DTO.ClassBookingStatusResponse;
import com.project.bookMembership.DTO.ClassDetailRequest;
import com.project.bookMembership.DTO.MembershipStatusRequest;
import com.project.bookMembership.DTO.MembershipStatusResponse;

public interface ClassDetailService {
    
    ClassDetail book(ClassDetailRequest classDetailRequest);
    int cancel(ClassDetailRequest classDetailRequest);
    ClassBookingStatusResponse checkBookingEligibility(ClassDetailRequest classDetailRequest);
    MembershipStatusResponse checkMembershipStatus(MembershipStatusRequest membershipStatusRequest);
}
