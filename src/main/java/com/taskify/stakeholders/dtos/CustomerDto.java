package com.taskify.stakeholders.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {


    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 900, message = "Name must be between 2 and 900 characters")
    private String name;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "State cannot be blank")
    private String state;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
    private String pincode;

    @NotBlank(message = "Person of contact cannot be blank")
    @Size(min = 2, max = 200, message = "Person of contact must be between 2 and 200 characters")
    private String personOfContact;

    @Pattern(regexp = "^[0-9]+$", message = "Phone number can only contain digits")
    private String phone;

    private String residenceAddress;

    private LocalDateTime anniversaryDate = LocalDateTime.now();

    private LocalDateTime birthDate = LocalDateTime.now();

    @NotBlank(message = "Parent Company Id can't be null")
    private Long parentCompanyId;
}
