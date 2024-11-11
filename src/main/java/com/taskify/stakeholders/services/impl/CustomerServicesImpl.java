package com.taskify.stakeholders.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.dtos.CustomerDto;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.models.ParentCompanyModel;
import com.taskify.stakeholders.repositories.CustomerRepository;
import com.taskify.stakeholders.services.CustomerServices;
import com.taskify.task.instances.services.TaskInstanceServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.taskify.common.utils.Helper.PAGE_SIZE;

@Repository
public class CustomerServicesImpl implements CustomerServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TaskInstanceServices taskInstanceServices;

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        CustomerModel customerModel = this.modelMapper.map(customerDto, CustomerModel.class);
        if (customerDto.getParentCompanyId() != null) {
            customerModel.setParentCompany(new ParentCompanyModel(customerDto.getParentCompanyId()));
        }
        customerModel = this.customerRepository.save(customerModel);

        return this.customerModelToDto(customerModel);
    }

    @Override
    public PageResponse<CustomerDto> getAllCustomers(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<CustomerModel> pageCustomer = this.customerRepository.findAll(pageable);
        List<CustomerModel> customerModels = pageCustomer.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                customerModels.stream().map(this::customerModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<CustomerDto> getCustomersByParentCompanyId(int pageNumber, Integer pageSize, Long parentCompanyId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<CustomerModel> pageCustomer = this.customerRepository.findByParentCompany(pageable, new ParentCompanyModel(parentCompanyId));
        List<CustomerModel> customerModels = pageCustomer.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                customerModels.stream().map(this::customerModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<CustomerDto> getCustomersByNameStateCityAndPincode(int pageNumber, Integer pageSize, String searchTxt) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<CustomerModel> pageCustomer = this.customerRepository.findBySearchTxt(pageable, searchTxt);
        List<CustomerModel> customerModels = pageCustomer.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                customerModels.stream().map(this::customerModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        CustomerModel foundCustomer = this.customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.CUSTOMER, "id", id, false)
        );

        return this.customerModelToDto(foundCustomer);
    }

    @Override
    public PageResponse<CustomerDto> getCustomersByEmail(int pageNumber, Integer pageSize, String email) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<CustomerModel> pageCustomer = this.customerRepository.findByEmail(pageable, email);
        List<CustomerModel> customerModels = pageCustomer.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                customerModels.stream().map(this::customerModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        CustomerModel foundCustomer = this.customerRepository.findById(customerDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.CUSTOMER, "id", customerDto.getId(), false)
        );

        return null;
    }

    // TODO: Delete the customer
    @Override
    public boolean deleteCustomer(Long id) {
        this.getCustomerById(id);
        // Delete the task_instances
        return false;
    }

    @Override
    public PageResponse<CustomerDto> getCustomersByEmailOrCityOrState(String email, String city, String state,
                                                                      int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number should always be greater than 0");
        }

        if (email.isEmpty()) {
            email = null;
        }
        if (state.isEmpty()) {
            state = null;
        }
        if (city.isEmpty()) {
            city = null;
        }
        Pageable pageable = Helper.getPageable(pageNumber);

        System.out.println(email);
        System.out.println(city);
        System.out.println(state);

        // Fetch the customers using the repository
        Page<CustomerModel> pageCustomer = this.customerRepository.findByEmailCityOrState(email, city, state, pageable);

        // Apply pagination to the fetched customers list
        List<CustomerModel> customers = pageCustomer.getContent();

        // Build and return the PageResponse
        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                customers.stream().map(this::customerModelToDto).collect(Collectors.toList()));
    }

    @Override
    public PageResponse<CustomerDto> searchCustomers(String customerName, String phone, String pincode, String personOfContact, int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0.");
        }
        System.out.println(customerName);
        System.out.println(phone);
        System.out.println(pincode);
        System.out.println(personOfContact);
        System.out.println(pageNumber);

        Pageable pageable = Helper.getPageable(pageNumber);

        Page<CustomerModel> pageCustomer = customerRepository.findByNamePhonePincodePersonOfContact(
                customerName == null || customerName.trim().isEmpty() ? null : customerName.trim(),
                phone == null || phone.trim().isEmpty() ? null : phone.trim(),
                pincode == null || pincode.trim().isEmpty() ? null : pincode.trim(),
                personOfContact == null || personOfContact.trim().isEmpty() ? null : personOfContact.trim(),
                pageable
        );

        List<CustomerModel> customerModels = pageCustomer.getContent();

        return new PageResponse(
                pageNumber,
                PAGE_SIZE,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                customerModels.stream().map(this::customerModelToDto).collect(Collectors.toList())
        );
    }

    private CustomerDto customerModelToDto(CustomerModel customerModel) {
        if (customerModel == null) {
            return null;
        }
        CustomerDto customerDto = this.modelMapper.map(customerModel, CustomerDto.class);
        customerDto.setParentCompanyId(customerModel.getId());

        return customerDto;
    }

}
