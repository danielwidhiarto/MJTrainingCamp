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

public class GetAllUserWithMembershipResponse {

    private List<UserWithMembershipAndVisitPackage> userData;

}
