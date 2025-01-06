package com.project.bookMembership.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWithMembershipAndVisitPackage {

    private long idUser;
    private String name;
    private String email;
    private String pNumber;
    private List<MembershipDetailResponse> memberships;
    private List<VisitDetailResponse> visitDetails;

}
