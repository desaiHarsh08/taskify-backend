package com.taskify.analytics.services.impl;

import com.taskify.analytics.dtos.ActivityLogDto;
import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.analytics.repositories.ActivityLogRepository;
import com.taskify.analytics.services.ActivityLogServices;
import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.models.ParentCompanyModel;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.user.models.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogServicesImpl implements ActivityLogServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ActivityLogRepository activityLogRepository;


    @Override
    public ActivityLogDto createActivityLog(ActivityLogDto activityLogDto) {
        ActivityLogModel activityLogModel = this.modelMapper.map(activityLogDto, ActivityLogModel.class);
        activityLogModel.setUser(new UserModel(activityLogDto.getUserId()));
        activityLogModel.setTaskInstance(new TaskInstanceModel(activityLogDto.getTaskInstanceId()));
        activityLogModel.setFunctionInstance(new FunctionInstanceModel(activityLogDto.getFunctionInstanceId()));
        activityLogModel.setFieldInstance(new FieldInstanceModel(activityLogDto.getFieldInstanceId()));
        activityLogModel.setColumnInstance(new ColumnInstanceModel(activityLogDto.getColumnInstanceId()));
        activityLogModel = this.activityLogRepository.save(activityLogModel);

        return this.activityLogModelToDto(activityLogModel);
    }

    @Override
    public ActivityLogDto getActivityLogById(Long id) {
        ActivityLogModel foundActivityLogModel = this.activityLogRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.ACTIVITY_LOG, "id", id, false)
        );

        return this.activityLogModelToDto(foundActivityLogModel);
    }

    @Override
    public PageResponse<ActivityLogDto> getAllActivityLogs(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findAll(pageable);
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getActivityLogsByDate(int pageNumber, Integer pageSize, LocalDateTime date) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByCreatedAt(pageable, date);
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getActivityLogsByYearAndMonth(int pageNumber, Integer pageSize, int year, int month) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByYearAndMonth(pageable, year, month);
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getByResourceType(int pageNumber, Integer pageSize, ResourceType resourceType) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByResourceType(pageable, resourceType);
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getByActionType(int pageNumber, Integer pageSize, ActionType actionType) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByActionType(pageable, actionType);
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }


    @Override
    public PageResponse<ActivityLogDto> getByUser(int pageNumber, Integer pageSize, Long userId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByUser(pageable, new UserModel(userId));
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getByTaskInstance(int pageNumber, Integer pageSize, Long taskInstanceId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByTaskInstance(pageable, new TaskInstanceModel(taskInstanceId));
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getByFunctionInstance(int pageNumber, Integer pageSize, Long functionInstanceId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByFunctionInstance(pageable, new FunctionInstanceModel(functionInstanceId));
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getByFieldInstance(int pageNumber, Integer pageSize, Long fieldInstanceId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByFieldInstance(pageable, new FieldInstanceModel(fieldInstanceId));
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ActivityLogDto> getByColumnInstance(int pageNumber, Integer pageSize, Long columnInstanceId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByColumnInstance(pageable, new ColumnInstanceModel(columnInstanceId));
        List<ActivityLogModel> activityLogModels = pageActivityLog.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageActivityLog.getTotalPages(),
                pageActivityLog.getTotalElements(),
                activityLogModels.stream().map(this::activityLogModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public boolean deleteActivityLog(Long id) {
        this.getActivityLogById(id);
        this.activityLogRepository.deleteById(id);

        return true;
    }

    private ActivityLogDto activityLogModelToDto(ActivityLogModel activityLogModel) {
        if (activityLogModel == null) {
            return null;
        }
        ActivityLogDto activityLogDto = this.modelMapper.map(activityLogModel, ActivityLogDto.class);
        activityLogDto.setUserId(activityLogModel.getUser().getId());
        activityLogDto.setTaskInstanceId(activityLogModel.getTaskInstance().getId());
        activityLogDto.setFunctionInstanceId(activityLogModel.getFunctionInstance().getId());
        activityLogDto.setFieldInstanceId(activityLogModel.getFieldInstance().getId());
        activityLogDto.setColumnInstanceId(activityLogModel.getColumnInstance().getId());

        return activityLogDto;
    }

}
