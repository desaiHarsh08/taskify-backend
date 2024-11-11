package com.taskify.user.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.user.dtos.DepartmentDto;

import java.util.List;

public interface DepartmentServices {

    DepartmentDto createDepartment(DepartmentDto departmentDto);

    PageResponse<DepartmentDto> getAllDepartments(int pageNumber, Integer pageSize);

    List<DepartmentDto> getDepartmentsByUserId(Long userId);

    DepartmentDto getDepartmentById(Long id);

    boolean deleteDepartment(Long id);
    
}
