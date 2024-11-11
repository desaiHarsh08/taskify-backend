package com.taskify.stakeholders.controllers;

import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.dtos.CustomerDto;
import com.taskify.stakeholders.services.CustomerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerServices customerServices;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = this.customerServices.createCustomer(customerDto);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CustomerDto>> getAllCustomers(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<CustomerDto> response = this.customerServices.getAllCustomers(pageNumber, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/parent-company/{parentCompanyId}")
    public ResponseEntity<PageResponse<CustomerDto>> getCustomersByParentCompanyId(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long parentCompanyId
    ) {
        PageResponse<CustomerDto> response = this.customerServices.getCustomersByParentCompanyId(pageNumber, pageSize, parentCompanyId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getCustomersByNameEmailStateCityAndPincode(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam String email,
            @RequestParam String state,
            @RequestParam String city
    ) {
        return null;
    }

    @GetMapping("/filters")
    public ResponseEntity<?> getCustomersByEmailOrCityOrState(@RequestParam("page") int pageNumber,
                                                              @RequestParam String city, @RequestParam String state, @RequestParam String email) {
        System.out.println(pageNumber + ", " + email + ", " + city + ", " + state);
        return new ResponseEntity<>(
                this.customerServices.getCustomersByEmailOrCityOrState(email, city, state, pageNumber), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCustomer(@RequestParam("page") int pageNumber,
                                            @RequestParam String customerName, @RequestParam String phone, @RequestParam String pincode, @RequestParam String personOfContact) {

        return new ResponseEntity<>(
                this.customerServices.searchCustomers(customerName, phone, pincode, personOfContact, pageNumber), HttpStatus.OK);
    }

    @GetMapping("/searchtext")
    public ResponseEntity<PageResponse<CustomerDto>> getCustomersBySearch(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam String searchTxt) {
        PageResponse<CustomerDto> response = this.customerServices.getCustomersByNameStateCityAndPincode(pageNumber, pageSize, searchTxt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        CustomerDto customerDto = this.customerServices.getCustomerById(id);
        return new ResponseEntity<>(customerDto, HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<PageResponse<CustomerDto>> getCustomersByEmail(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam String email) {
        PageResponse<CustomerDto> response = this.customerServices.getCustomersByEmail(pageNumber, pageSize, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDto customerDto) {
        if (!customerDto.getId().equals(id)) {
            throw new IllegalArgumentException("Please provide the valid customer_id!");
        }
        CustomerDto updatedCustomer = this.customerServices.updateCustomer(customerDto);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean isDeleted = this.customerServices.deleteCustomer(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
