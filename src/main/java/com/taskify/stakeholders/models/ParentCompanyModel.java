package com.taskify.stakeholders.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_companies")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParentCompanyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private String personOfContact;

    @Column(nullable = false)
    private  String phone;

    @Column(nullable = false)
    private String businessType;

    private String headOfficeAddress;

    private String remark;

    public ParentCompanyModel(Long id) {
        this.id = id;
    }

}
