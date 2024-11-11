package com.taskify.task.instances.services;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnInstanceDto;
import com.taskify.task.instances.dtos.FunctionInstanceDto;
import com.taskify.task.instances.models.FunctionInstanceModel;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface FunctionInstanceServices {

    FunctionInstanceDto createFunctionInstance(FunctionInstanceDto functionInstanceDto, Long assignedToUserId);

    boolean uploadFiles(FunctionInstanceDto functionInstanceDto, MultipartFile[] files);

    PageResponse<FunctionInstanceDto> getAllFunctionInstances(int pageNumber, Integer pageSize);

    FunctionInstanceDto createFunctionAndClose(FunctionInstanceDto functionInstanceDto, Long assignedToUserId);

    PageResponse<FunctionInstanceDto> getFunctionInstancesByFunctionTemplateById(int pageNumber, Integer pageSize, Long functionTemplateId);

    List<FunctionInstanceDto> getFunctionInstancesByTaskInstanceId(Long taskInstanceId);

    PageResponse<FunctionInstanceDto> getFunctionInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId);

    PageResponse<FunctionInstanceDto> getFunctionInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId);

    PageResponse<FunctionInstanceDto> getFunctionInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type);

    FunctionInstanceDto getFunctionInstanceById(Long id);

    FunctionInstanceDto updateFunctionInstance(FunctionInstanceDto functionInstanceDto, Long userId);

    FunctionInstanceDto closeFunction(Long id, Long closedByUserId);

    boolean deleteFunctionInstance(Long id);

    boolean deleteFunctionInstancesByTaskInstanceId(Long taskInstanceId);

    boolean deleteFunctionInstancesByFunctionTemplateId(Long functionTemplateId);

    boolean deleteFunctionInstancesByDropdownTemplateId(Long dropdownTemplateId);
    
}
