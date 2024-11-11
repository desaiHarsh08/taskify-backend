package com.taskify.stakeholders.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.dtos.CustomerDto;

public interface CustomerServices {

    CustomerDto createCustomer(CustomerDto customerDto);

    PageResponse<CustomerDto> getAllCustomers(int pageNumber, Integer pageSize);

    PageResponse<CustomerDto> getCustomersByParentCompanyId(int pageNumber, Integer pageSize, Long parentCompanyId);

    PageResponse<CustomerDto> getCustomersByNameStateCityAndPincode(int pageNumber, Integer pageSize, String searchTxt);

    PageResponse<CustomerDto> searchCustomers(String customerName, String phone, String pincode, String personOfContact, int pageNumber);


    PageResponse<CustomerDto> getCustomersByEmailOrCityOrState(String email, String city, String state, int pageNumber);


    CustomerDto getCustomerById(Long id);

    PageResponse<CustomerDto> getCustomersByEmail(int pageNumber, Integer pageSize, String email);

    CustomerDto updateCustomer(CustomerDto customerDto);

    boolean deleteCustomer(Long id);
    
}
