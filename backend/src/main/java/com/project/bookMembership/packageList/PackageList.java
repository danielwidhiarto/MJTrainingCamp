package com.project.bookMembership.packageList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PackageList")
public class PackageList {

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idPackage;

    private String packageName;
    private Long price;
    private int duration;
    private String status;
    private String type;
    private int visitNumber;

}
