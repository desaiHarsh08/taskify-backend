package com.taskify.analytics.services;

import com.taskify.analytics.dtos.ActivityLogDto;
import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.models.ParentCompanyModel;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.user.models.UserModel;

import java.time.LocalDateTime;

public interface ActivityLogServices {

    ActivityLogDto createActivityLog(ActivityLogDto activityLogModel);

    ActivityLogDto getActivityLogById(Long id);

    PageResponse<ActivityLogDto> getAllActivityLogs(int pageNumber, Integer pageSize);

    PageResponse<ActivityLogDto> getActivityLogsByDate(int pageNumber, Integer pageSize, LocalDateTime date);

    PageResponse<ActivityLogDto> getActivityLogsByYearAndMonth(int pageNumber, Integer pageSize, int year, int month);

    PageResponse<ActivityLogDto> getByResourceType(int pageNumber, Integer pageSize, ResourceType resourceType);

    PageResponse<ActivityLogDto> getByActionType(int pageNumber, Integer pageSize, ActionType actionType);

    PageResponse<ActivityLogDto> getByUser(int pageNumber, Integer pageSize, Long userId);

    PageResponse<ActivityLogDto> getByTaskInstance(int pageNumber, Integer pageSize, Long taskInstanceId);

    PageResponse<ActivityLogDto> getByFunctionInstance(int pageNumber, Integer pageSize, Long functionInstanceId);

    PageResponse<ActivityLogDto> getByFieldInstance(int pageNumber, Integer pageSize, Long fieldInstanceId);

    PageResponse<ActivityLogDto> getByColumnInstance(int pageNumber, Integer pageSize, Long columnInstanceId);

    boolean deleteActivityLog(Long id);

}
