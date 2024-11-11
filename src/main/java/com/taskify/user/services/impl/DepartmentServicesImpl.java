package com.taskify.user.services.impl;

import com.taskify.common.utils.Helper;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.PageResponse;
import com.taskify.user.dtos.DepartmentDto;
import com.taskify.user.dtos.RoleDto;
import com.taskify.user.models.DepartmentModel;
import com.taskify.user.models.RoleModel;
import com.taskify.user.models.UserModel;
import com.taskify.user.repositories.DepartmentRepository;
import com.taskify.user.repositories.RoleRepository;
import com.taskify.user.services.DepartmentServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServicesImpl implements DepartmentServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        // Map department DTO to model
        DepartmentModel departmentModel = this.modelMapper.map(departmentDto, DepartmentModel.class);

        // Associate User with department
        departmentModel.setUser(new UserModel(departmentDto.getUserId()));

        // Save roles and add them to department
        List<RoleModel> roles = new ArrayList<>();
        for (RoleDto roleDto : departmentDto.getRoles()) {
            RoleModel roleModel = this.modelMapper.map(roleDto, RoleModel.class);
            // Save role first to ensure it's persisted
            roleModel = this.roleRepository.save(roleModel);
            roles.add(roleModel);
        }

        // Set the roles for the department model
        departmentModel.setRoles(roles);

        // Save the department model
        departmentModel = this.departmentRepository.save(departmentModel);

        // Return the department DTO
        return this.departmentModelToDto(departmentModel);
    }


    @Override
    public List<DepartmentDto> getDepartmentsByUserId(Long userId) {
        List<DepartmentModel> departmentModels = this.departmentRepository.findByUser(new UserModel(userId));
        if (departmentModels.isEmpty()) {
            return new ArrayList<>();
        }

        return departmentModels.stream().map(this::departmentModelToDto).collect(Collectors.toList());
    }

    @Override
    public PageResponse<DepartmentDto> getAllDepartments(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<DepartmentModel> pageDepartment = this.departmentRepository.findAll(pageable);

        List<DepartmentModel> departmentModels = pageDepartment.getContent();

        return new PageResponse<DepartmentDto>(
                pageNumber,
                pageSize,
                pageDepartment.getTotalPages(),
                pageDepartment.getTotalElements(),
                departmentModels.stream().map(this::departmentModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public DepartmentDto getDepartmentById(Long id) {
        DepartmentModel departmentModel = this.departmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.DEPARTMENT, "id", id, false)
        );

        return this.departmentModelToDto(departmentModel);
    }

    @Override
    public boolean deleteDepartment(Long id) {
        DepartmentModel departmentModel = this.departmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.DEPARTMENT, "id", id, false)
        );
        // Remove the association between DepartmentModel and RoleModel
        for (RoleModel role : new ArrayList<>(departmentModel.getRoles())) {
            departmentModel.removeRole(role); // This removes the role from the department and vice versa
        }
        // Now delete the department
        this.departmentRepository.delete(departmentModel);
        return true;
    }

    private DepartmentDto departmentModelToDto(DepartmentModel departmentModel) {
        if (departmentModel == null) {
            return null;
        }
        DepartmentDto departmentDto = this.modelMapper.map(departmentModel, DepartmentDto.class);
        departmentDto.setUserId(departmentModel.getUser().getId());
        departmentDto.setRoles(new ArrayList<>());
        for (RoleModel roleModel: departmentModel.getRoles()) {
            roleModel = this.roleRepository.findById(roleModel.getId()).orElse(null);
            if (roleModel != null) {
                RoleDto roleDto = this.modelMapper.map(roleModel, RoleDto.class);
                roleDto.setDepartmentId(roleModel.getId());
                departmentDto.getRoles().add(roleDto);
            }
        }

        return departmentDto;
    }
}
