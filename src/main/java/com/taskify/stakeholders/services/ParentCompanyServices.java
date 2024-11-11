package com.taskify.stakeholders.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.dtos.ParentCompanyDto;

import java.util.List;

public interface ParentCompanyServices {

    ParentCompanyDto createParentCompany(ParentCompanyDto parentCompanyDto);

    PageResponse<ParentCompanyDto> getAllParentCompanies(int pageNumber, Integer pageSize);

    PageResponse<ParentCompanyDto> getParentCompaniesByNameStateCityAndPincode(int pageNumber, Integer pageSize, String searchTxt);

    ParentCompanyDto getParentCompanyById(Long id);

    PageResponse<ParentCompanyDto> getParentCompanyByEmail(int pageNumber, Integer pageSize,String email);

    ParentCompanyDto updateParentCompany(ParentCompanyDto parentCompanyDto);

    boolean deleteParentCompany(Long id);

    PageResponse<ParentCompanyDto> searchParentCompanies(String name, String city, String state,
                                                         String pincode, int pageNumber);
    
}
