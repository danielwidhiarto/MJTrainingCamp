package com.project.bookMembership.membership;

import com.project.bookMembership.DTO.MembershipRequest;

public interface MembershipService {
    Membership save(MembershipRequest request);
}
