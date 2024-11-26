package com.taskify.task.instances.services;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.PriorityType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.TaskInstanceDto;
import com.taskify.task.instances.dtos.TaskSummaryDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskInstanceServices {

    PageResponse<TaskSummaryDto> getTasksSummary(int pageNumber, Integer pageSize, PriorityType priorityType, Boolean overdueFlag, Boolean pendingFlag);

    TaskInstanceDto createTaskInstance(TaskInstanceDto taskInstanceDto);

    PageResponse<TaskInstanceDto> getAllTaskInstances(int pageNumber, Integer pageSize);

    PageResponse<TaskInstanceDto> getTaskInstancesByTaskTemplateById(int pageNumber, Integer pageSize, Long taskTemplateId);

    List<TaskInstanceDto> getTaskInstancesByCustomerId(Long customerId);

    TaskInstanceDto getTaskInstanceById(Long id);

    TaskInstanceDto getTaskInstanceByAbbreviation(String abbreviation);

    TaskInstanceDto closeTaskInstance(Long id, Long closedByUserId);

    PageResponse<TaskInstanceDto> getTaskByAbbreviationAndCreatedDate(int pageNumber, Integer pageSize, String taskAbbreviation,
                                                                     LocalDate date);

    PageResponse<TaskInstanceDto> getTaskInstancesByPriorityType(int pageNumber, Integer pageSize, PriorityType priorityType);

    PageResponse<TaskInstanceDto> getTaskInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId);

    PageResponse<TaskInstanceDto> getTaskInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId);

    PageResponse<TaskInstanceDto> getOverdueTaskInstances(int pageNumber, Integer pageSize);

    PageResponse<TaskInstanceDto> getTaskInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type);

    TaskInstanceDto updateTaskInstance(TaskInstanceDto taskInstanceDto, Long userId);

    boolean deleteTaskInstance(Long id);

    boolean deleteTaskInstancesByTaskTemplateId(Long taskTemplateId);

    boolean deleteTaskInstancesByDropdownTemplateId(Long dropdownTemplateId);
    
}
