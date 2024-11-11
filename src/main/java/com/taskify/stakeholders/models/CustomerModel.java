package com.taskify.stakeholders.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @Column(nullable = false)
    private String address;

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
    private String residenceAddress;

    private LocalDateTime anniversaryDate = LocalDateTime.now();

    private LocalDateTime birthDate = LocalDateTime.now();

    @ManyToOne(targetEntity = ParentCompanyModel.class)
    @JoinColumn(name = "parent_company_id_fk")
    private ParentCompanyModel parentCompany;

    public CustomerModel(Long id) {
        this.id = id;
    }

}
