package com.project.bookMembership.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDetailResponse {
    private Long idMember;          // Assuming id_member is of type Long
    private Long duration;        // Assuming duration is a String (e.g., "1 month", "3 months")
    private String endDate;         // Assuming end_date is a String (format: "yyyy-MM-dd")
    private Long price;           // Assuming price is of type Double
    private String startDate;       // Assuming start_date is a String (format: "yyyy-MM-dd")
    private Long transactionId;     // Assuming transaction_id is of type Long
    private Long userId;            // Assuming user_id is of type Long
}
