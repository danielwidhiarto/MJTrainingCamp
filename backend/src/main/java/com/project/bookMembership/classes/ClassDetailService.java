package com.project.bookMembership.classes;

import com.project.bookMembership.DTO.*;

public interface ClassDetailService {
    
    ClassDetail book(ClassDetailRequest classDetailRequest);
    int cancel(ClassDetailRequest classDetailRequest);

    ClassBookingStatusResponse checkBookingEligibility(ClassDetailRequest classDetailRequest);
    MembershipStatusResponse checkMembershipStatus(MembershipStatusRequest membershipStatusRequest);
    GetAllUserWithMembershipResponse getAllMembership( );
}
