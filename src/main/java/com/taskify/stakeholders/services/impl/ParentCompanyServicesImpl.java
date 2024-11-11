package com.taskify.stakeholders.services.impl;

import com.taskify.common.utils.Helper;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.dtos.ParentCompanyDto;
import com.taskify.stakeholders.models.ParentCompanyModel;
import com.taskify.stakeholders.repositories.ParentCompanyRepository;
import com.taskify.stakeholders.services.CustomerServices;
import com.taskify.stakeholders.services.ParentCompanyServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.taskify.common.utils.Helper.PAGE_SIZE;

@Service
public class ParentCompanyServicesImpl implements ParentCompanyServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ParentCompanyRepository parentCompanyRepository;

    @Autowired
    private CustomerServices customerServices;

    @Override
    public ParentCompanyDto createParentCompany(ParentCompanyDto parentCompanyDto) {
        ParentCompanyModel parentCompanyModel = this.modelMapper.map(parentCompanyDto, ParentCompanyModel.class);
        parentCompanyModel = this.parentCompanyRepository.save(parentCompanyModel);

        return this.parentCompanyModelToDto(parentCompanyModel);
    }

    @Override
    public PageResponse<ParentCompanyDto> getAllParentCompanies(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findAll(pageable);
        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                parentCompanyModels.stream().map(this::parentCompanyModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ParentCompanyDto> getParentCompaniesByNameStateCityAndPincode(int pageNumber, Integer pageSize, String searchTxt) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findBySearchTxt(pageable, searchTxt);
        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                parentCompanyModels.stream().map(this::parentCompanyModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public ParentCompanyDto getParentCompanyById(Long id) {
        ParentCompanyModel parentCompanyModel = this.parentCompanyRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.PARENT_COMPANY, "id", id, false)
        );

        return this.parentCompanyModelToDto(parentCompanyModel);
    }

    @Override
    public PageResponse<ParentCompanyDto> getParentCompanyByEmail(int pageNumber, Integer pageSize, String email) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findByEmail(pageable, email);
        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                parentCompanyModels.stream().map(this::parentCompanyModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public ParentCompanyDto updateParentCompany(ParentCompanyDto parentCompanyDto) {
        ParentCompanyModel foundParentCompanyModel = this.parentCompanyRepository.findById(parentCompanyDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.PARENT_COMPANY, "id", parentCompanyDto.getId(), false)
        );
        foundParentCompanyModel.setAddress(parentCompanyDto.getAddress());
        foundParentCompanyModel.setCity(parentCompanyDto.getCity());
        foundParentCompanyModel.setState(parentCompanyDto.getState());
        foundParentCompanyModel.setPincode(parentCompanyDto.getPincode());
        foundParentCompanyModel.setPersonOfContact(parentCompanyDto.getPersonOfContact());
        foundParentCompanyModel.setPhone(parentCompanyDto.getPhone());
        foundParentCompanyModel.setBusinessType(parentCompanyDto.getBusinessType());
        foundParentCompanyModel.setHeadOfficeAddress(parentCompanyDto.getHeadOfficeAddress());
        foundParentCompanyModel.setRemark(parentCompanyDto.getRemark());
        foundParentCompanyModel.setEmail(parentCompanyDto.getEmail());

        return this.parentCompanyModelToDto(this.parentCompanyRepository.save(foundParentCompanyModel));
    }

    // TODO: Delete the parent_company with the customers
    @Override
    public boolean deleteParentCompany(Long id) {

        return false;
    }

    @Override
    public PageResponse<ParentCompanyDto> searchParentCompanies(String name, String city, String state,
                                                                String pincode, int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number should be greater than 0.");
        }

        System.out.println(name);
        System.out.println(city);
        System.out.println(state);
        System.out.println(pincode);
        System.out.println(pageNumber);

        // Pageable configuration with sorting by "id" and descending order
        Pageable pageable = Helper.getPageable(pageNumber);

        // Query the repository with null checks and trimmed values
        Page<ParentCompanyModel> pageParentCompanies = parentCompanyRepository.findByNameCityStatePincode(
                name == null || name.trim().isEmpty() ? null : name.trim(),
                city == null || city.trim().isEmpty() ? null : city.trim(),
                state == null || state.trim().isEmpty() ? null : state.trim(),
                pincode == null || pincode.trim().isEmpty() ? null : pincode.trim(),
                pageable);

        // Create PageResponse with pagination data
        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE, // Page number starts from 0, so increment by 1
                pageParentCompanies.getTotalPages(),
                pageParentCompanies.getTotalElements(),
                pageParentCompanies.getContent().stream().map(this::parentCompanyModelToDto).collect(Collectors.toList()));
    }

    private ParentCompanyDto parentCompanyModelToDto(ParentCompanyModel parentCompanyModel) {
        if (parentCompanyModel == null) {
            return null;
        }
        ParentCompanyDto parentCompanyDto = this.modelMapper.map(parentCompanyModel, ParentCompanyDto.class);

        return parentCompanyDto;
    }

}
