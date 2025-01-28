package com.taskify.user.services.impl;

import com.taskify.common.utils.Helper;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.PageResponse;
import com.taskify.user.dtos.DepartmentDto;
import com.taskify.user.dtos.PermissionDto;
import com.taskify.user.dtos.UserDto;
import com.taskify.user.dtos.ViewTaskDto;
import com.taskify.user.models.PermissionModel;
import com.taskify.user.models.UserModel;
import com.taskify.user.models.ViewTaskModel;
import com.taskify.user.repositories.PermissionRepository;
import com.taskify.user.repositories.UserRepository;
import com.taskify.user.repositories.ViewTaskRepository;
import com.taskify.user.services.DepartmentServices;
import com.taskify.user.services.UserServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentServices departmentServices;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ViewTaskRepository viewTaskRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserModel userModel = this.modelMapper.map(userDto, UserModel.class);
        if (this.userRepository.findByEmail(userModel.getEmail()).orElse(null) != null) {
            return null;
        }
        // Encrypt the raw password
        String encryptedPassword = this.bCryptPasswordEncoder.encode(userDto.getPassword());
        userModel.setPassword(encryptedPassword);
        userModel = this.userRepository.save(userModel);
        for (DepartmentDto departmentDto: userDto.getDepartments()) {
            departmentDto.setUserId(userModel.getId());
            this.departmentServices.createDepartment(departmentDto);
        }

        for (ViewTaskDto viewTaskDto: userDto.getViewTasks()) {
            ViewTaskModel viewTaskModel = new ViewTaskModel();
            viewTaskModel.setTaskType(viewTaskDto.getTaskType());
            viewTaskModel.setUser(userModel);
            viewTaskModel = this.viewTaskRepository.save(viewTaskModel);
            for (PermissionDto permissionDto: viewTaskDto.getPermissions()) {
                PermissionModel permissionModel = new PermissionModel();
                permissionModel.setType(permissionDto.getType());
                permissionModel.setViewTask(viewTaskModel);
                this.permissionRepository.save(permissionModel);
            }
        }

        return this.userModelToDto(userModel);
    }

    @Override
    public PageResponse<UserDto> getAllUsers(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<UserModel> pageUser = this.userRepository.findAll(pageable);
        List<UserModel> userModels = pageUser.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageUser.getTotalPages(),
                pageUser.getTotalElements(),
                userModels.stream().map(this::userModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public UserDto getUserById(Long id) {
        UserModel foundUser = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.USER, "id", id, false)
        );

        return this.userModelToDto(foundUser);
    }

    @Override
    public List<UserDto> getUsersByDepartment(String department) {
        List<UserModel> userModels = this.userRepository.findUsersByDepartment(department);
        if (userModels.isEmpty()) {
            return new ArrayList<>();
        }

        return userModels.stream().map(this::userModelToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserModel foundUser = this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.USER, "email", email, false)
        );

        return this.userModelToDto(foundUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto foundUserDto = this.getUserById(userDto.getId());

        UserModel foundUser = this.modelMapper.map(foundUserDto, UserModel.class);
        foundUser.setName(userDto.getName());
        foundUser.setEmail(userDto.getEmail());
        foundUser.setPhone(userDto.getPhone());
        foundUser.setAdmin(userDto.isAdmin());
        foundUser.setDisabled(userDto.isDisabled());

        foundUser = this.userRepository.save(foundUser);

        for (DepartmentDto departmentDto: userDto.getDepartments()) {
            departmentDto.setUserId(foundUser.getId());
            if (foundUserDto
                    .getDepartments()
                    .stream()
                    .noneMatch(d -> d.getName().equalsIgnoreCase(departmentDto.getName()))
            ) {
                this.departmentServices.createDepartment(departmentDto);
            }
        }

        return this.userModelToDto(foundUser);
    }

    @Override
    public boolean resetPassword(Long id, String rawPassword) {
        UserModel foundUser = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.USER, "id", id, false)
        );
        // Encrypt the password
        String encryptedPassword = this.bCryptPasswordEncoder.encode(rawPassword);
        foundUser.setPassword(encryptedPassword);
        foundUser = this.userRepository.save(foundUser);

        return true;
    }


    private UserDto userModelToDto(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDto userDto = this.modelMapper.map(userModel, UserDto.class);
        userDto.setDepartments(this.departmentServices.getDepartmentsByUserId(userModel.getId()));

        List<ViewTaskModel> viewTaskModels =this.viewTaskRepository.findByUser(userModel);
        if (!viewTaskModels.isEmpty()) {
            List<ViewTaskDto> viewTaskDtos = new ArrayList<>();
            for (ViewTaskModel viewTaskModel: viewTaskModels) {
                ViewTaskDto viewTaskDto = new ViewTaskDto();
                viewTaskDto.setTaskType(viewTaskModel.getTaskType());
                viewTaskDto.setUserId(viewTaskModel.getUser().getId());
                List<PermissionModel> permissionModels = this.permissionRepository.findByViewTask(viewTaskModel);
                if (!permissionModels.isEmpty()) {
                    for (PermissionModel permissionModel: permissionModels) {
                        PermissionDto permissionDto = new PermissionDto();
                        permissionDto.setType(permissionModel.getType());
                        permissionDto.setViewTaskId(viewTaskModel.getId());
                        viewTaskDto.getPermissions().add(permissionDto);
                    }
                }
                viewTaskDtos.add(viewTaskDto);
            }
            userDto.setViewTasks(viewTaskDtos);
        }


        return userDto;
    }
}
