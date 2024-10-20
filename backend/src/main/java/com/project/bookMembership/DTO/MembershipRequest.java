package com.project.bookMembership.DTO;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembershipRequest {
    
        private String token;

        //        private Long transactionId;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date startDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date endDate;
        private Long price;
        private Long duration;

        //transaction part
        // private String paymentType;
        private String paymentMethod;
        private String paymentStatus;
        // private Date visitStartDate;
        // private Date visitEndDate;
        private Long transactionPrice;
        private MultipartFile buktiTransfer;

}

