package com.taskify.task.instances.services;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.PriorityType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.TaskInstanceDto;
import com.taskify.task.instances.dtos.TaskSummaryDto;
import com.taskify.task.instances.models.TaskInstanceModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskInstanceServices {

    List<TaskSummaryDto> getTasksSummary(List<TaskInstanceModel> taskInstanceModels);

    TaskInstanceDto createTaskInstance(TaskInstanceDto taskInstanceDto);

    PageResponse<TaskSummaryDto> getAllTaskInstances(int pageNumber, Integer pageSize);

    PageResponse<TaskSummaryDto> getTaskInstancesByTaskTemplateById(int pageNumber, Integer pageSize, Long taskTemplateId);

    List<TaskInstanceDto> getTaskInstancesByCustomerId(Long customerId);

    TaskInstanceDto getTaskInstanceById(Long id);

    TaskInstanceDto getTaskInstanceByAbbreviation(String abbreviation);

    TaskInstanceDto closeTaskInstance(Long id, Long closedByUserId);

    PageResponse<TaskSummaryDto> getTaskByAbbreviationAndCreatedDate(int pageNumber, Integer pageSize, String taskAbbreviation,
                                                                     LocalDate date);

    PageResponse<TaskSummaryDto> getTaskInstancesByPriorityType(int pageNumber, Integer pageSize, PriorityType priorityType);

    PageResponse<TaskSummaryDto> getTaskInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId);

    PageResponse<TaskSummaryDto> getTaskInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId);

    PageResponse<TaskSummaryDto> getTaskInstancesByIsClosed(int pageNumber, Integer pageSize, boolean isClosed);

    PageResponse<TaskSummaryDto> getOverdueTaskInstances(int pageNumber, Integer pageSize);

    PageResponse<TaskSummaryDto> getTaskInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type);

    TaskInstanceDto updateTaskInstance(TaskInstanceDto taskInstanceDto, Long userId);

    boolean deleteTaskInstance(Long id);

    boolean deleteTaskInstancesByTaskTemplateId(Long taskTemplateId);

    boolean deleteTaskInstancesByDropdownTemplateId(Long dropdownTemplateId);

    TaskSummaryDto searchTaskInstance(String searchTxt);
    
}
