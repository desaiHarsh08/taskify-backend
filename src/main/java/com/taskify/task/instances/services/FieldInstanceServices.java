package com.taskify.task.instances.services;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.FieldInstanceDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FieldInstanceServices {

    FieldInstanceDto createFieldInstance(FieldInstanceDto fieldInstanceDto, Long userId);

    PageResponse<FieldInstanceDto> getAllFieldInstances(int pageNumber, Integer pageSize);

    FieldInstanceDto getFieldInstanceById(Long id);

    PageResponse<FieldInstanceDto> getFieldInstancesByFieldTemplateById(int pageNumber, Integer pageSize, Long fieldTemplateId);

    List<FieldInstanceDto> getFieldInstancesByFunctionInstanceId(Long functionInstanceId);

    PageResponse<FieldInstanceDto> getFieldInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId);

    PageResponse<FieldInstanceDto> getFieldInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId);

    PageResponse<FieldInstanceDto> getFieldInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type);

    FieldInstanceDto closeFieldInstance(Long id, Long closedByUserId);

    boolean deleteFieldInstance(Long id);

    boolean deleteFieldInstancesByFieldTemplateId(Long fieldTemplateId);
    
}
