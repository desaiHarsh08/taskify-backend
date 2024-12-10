package com.taskify.task.instances.services.impl;

import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.analytics.repositories.ActivityLogRepository;
import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnInstanceDto;
import com.taskify.task.instances.dtos.FieldInstanceDto;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.instances.repositories.FieldInstanceRepository;
import com.taskify.task.instances.repositories.FunctionInstanceRepository;
import com.taskify.task.instances.services.ColumnInstanceServices;
import com.taskify.task.instances.services.FieldInstanceServices;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.user.models.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldInstanceServicesImpl implements FieldInstanceServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FieldInstanceRepository fieldInstanceRepository;

    @Autowired
    private FunctionInstanceRepository functionInstanceRepository;

    @Autowired
    private ColumnInstanceServices columnInstanceServices;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public FieldInstanceDto createFieldInstance(FieldInstanceDto fieldInstanceDto, Long userId) {
        // Step 1: Create the new field_instance
        FieldInstanceModel newFieldInstanceModel = new FieldInstanceModel();
        newFieldInstanceModel.setFieldTemplate(new FieldTemplateModel(fieldInstanceDto.getFieldTemplateId()));
        newFieldInstanceModel.setCreatedByUser(new UserModel(fieldInstanceDto.getCreatedByUserId()));
        newFieldInstanceModel.setFunctionInstance(new FunctionInstanceModel(fieldInstanceDto.getFunctionInstanceId()));
        newFieldInstanceModel.setCreatedAt(LocalDateTime.now());
        newFieldInstanceModel.setUpdatedAt(LocalDateTime.now());
        // Step 2: Save the new field_instance
        newFieldInstanceModel = this.fieldInstanceRepository.save(newFieldInstanceModel);
        // Step 3: Create the column_instance
        for (ColumnInstanceDto columnInstanceDto: fieldInstanceDto.getColumnInstances()) {
            columnInstanceDto.setFieldInstanceId(newFieldInstanceModel.getId());
            this.columnInstanceServices.createColumnInstance(columnInstanceDto);
        }

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.FIELD);
        activityLogModel.setActionType(ActionType.CREATE);
        activityLogModel.setUser(new UserModel(userId));
        activityLogModel.setFieldInstance(newFieldInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.fieldInstanceModelToDto(newFieldInstanceModel);
    }

    @Override
    public PageResponse<FieldInstanceDto> getAllFieldInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FieldInstanceModel> pageFieldInstance;
        pageFieldInstance = this.fieldInstanceRepository.findAll(pageable);
        List<FieldInstanceModel> fieldInstanceModels = pageFieldInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFieldInstance.getTotalPages(),
                pageFieldInstance.getTotalElements(),
                fieldInstanceModels.stream().map(this::fieldInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<FieldInstanceDto> getFieldInstancesByFieldTemplateById(int pageNumber, Integer pageSize, Long fieldTemplateId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FieldInstanceModel> pageFieldInstance;
        pageFieldInstance = this.fieldInstanceRepository.findByFieldTemplate(pageable, new FieldTemplateModel(fieldTemplateId));
        List<FieldInstanceModel> fieldInstanceModels = pageFieldInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFieldInstance.getTotalPages(),
                pageFieldInstance.getTotalElements(),
                fieldInstanceModels.stream().map(this::fieldInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public List<FieldInstanceDto> getFieldInstancesByFunctionInstanceId(Long functionInstanceId) {
        List<FieldInstanceModel> fieldInstanceModels = this.fieldInstanceRepository.findByFunctionInstance(new FunctionInstanceModel(functionInstanceId));
        if (fieldInstanceModels.isEmpty()) {
            return new ArrayList<>();
        }

        return fieldInstanceModels.stream().map(this::fieldInstanceModelToDto).collect(Collectors.toList());
    }

    @Override
    public PageResponse<FieldInstanceDto> getFieldInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FieldInstanceModel> pageFieldInstance;
        pageFieldInstance = this.fieldInstanceRepository.findByCreatedByUser(pageable, new UserModel(createdByUserId));
        List<FieldInstanceModel> fieldInstanceModels = pageFieldInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFieldInstance.getTotalPages(),
                pageFieldInstance.getTotalElements(),
                fieldInstanceModels.stream().map(this::fieldInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<FieldInstanceDto> getFieldInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FieldInstanceModel> pageFieldInstance;
        pageFieldInstance = this.fieldInstanceRepository.findByClosedByUser(pageable, new UserModel(closedByUserId));
        List<FieldInstanceModel> fieldInstanceModels = pageFieldInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFieldInstance.getTotalPages(),
                pageFieldInstance.getTotalElements(),
                fieldInstanceModels.stream().map(this::fieldInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<FieldInstanceDto> getFieldInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FieldInstanceModel> pageFieldInstance;
        if (type.equals(DateParamType.CREATED)) {
            pageFieldInstance = this.fieldInstanceRepository.findByCreatedAt(pageable, date);
        }
        else if (type.equals(DateParamType.UPDATED)) {
            pageFieldInstance = this.fieldInstanceRepository.findByUpdatedAt(pageable, date);
        }
        else {
            pageFieldInstance = this.fieldInstanceRepository.findByClosedAt(pageable, date);
        }
        List<FieldInstanceModel> fieldInstanceModels = pageFieldInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFieldInstance.getTotalPages(),
                pageFieldInstance.getTotalElements(),
                fieldInstanceModels.stream().map(this::fieldInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public FieldInstanceDto getFieldInstanceById(Long id) {
        FieldInstanceModel foundFieldInstanceModel = this.fieldInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", id, false)
        );

        return this.fieldInstanceModelToDto(foundFieldInstanceModel);
    }

    @Override
    public FieldInstanceDto closeFieldInstance(Long id, Long closedByUserId) {
        // Step 1: Check for field_instance does exist
        FieldInstanceModel foundFieldInstanceModel = this.fieldInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", id, false)
        );
        // Step 2: Close the field_instance
        foundFieldInstanceModel.setClosedAt(LocalDateTime.now());
        foundFieldInstanceModel.setClosedByUser(new UserModel(closedByUserId));
        foundFieldInstanceModel.setUpdatedAt(LocalDateTime.now());
        // Step 3: Save the changes
        foundFieldInstanceModel = this.fieldInstanceRepository.save(foundFieldInstanceModel);

        // TODO: Notify the closed by user

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.FIELD);
        activityLogModel.setActionType(ActionType.CLOSED);
        activityLogModel.setUser(new UserModel(closedByUserId));
        activityLogModel.setFieldInstance(foundFieldInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.fieldInstanceModelToDto(foundFieldInstanceModel);
    }

    @Override
    public boolean deleteFieldInstance(Long id) {
        // Step 1: Check for field_instance does exist
        FieldInstanceDto foundFieldInstanceDto = this.getFieldInstanceById(id);
        System.out.println(foundFieldInstanceDto);
        // Step 2: Delete all the column_instances
        if (foundFieldInstanceDto.getColumnInstances() != null) {
            for (ColumnInstanceDto columnInstanceDto: foundFieldInstanceDto.getColumnInstances()) {
                if (!this.columnInstanceServices.deleteColumnInstance(columnInstanceDto.getId())) {
                    throw new IllegalArgumentException("Unable to delete the column_instance in the process for deleting field_instance (having id: " + " " + id + ").");
                }
            }
        }
        // Step 3: Delete the activity_logs
        Pageable pageable = PageRequest.of(0, 100);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByFieldInstance(pageable, new FieldInstanceModel(id));
        for (ActivityLogModel activityLogModel: pageActivityLog.getContent()) {
            this.activityLogRepository.deleteById(activityLogModel.getId());
        }
        for (int i = 1; i < pageActivityLog.getTotalPages(); i++) {
            pageable = PageRequest.of(i, 100);
            pageActivityLog = this.activityLogRepository.findByFieldInstance(pageable, new FieldInstanceModel(id));
            for (ActivityLogModel activityLogModel: pageActivityLog.getContent()) {
                this.activityLogRepository.deleteById(activityLogModel.getId());
            }
        }

        // Step 4: Delete the field_instance
        this.fieldInstanceRepository.deleteById(id);

        return true;
    }

    @Override
    public boolean deleteFieldInstancesByFieldTemplateId(Long fieldTemplateId) {
        Pageable pageable = Helper.getPageable(1, null);
        Page<FieldInstanceModel> pageFieldInstance = this.fieldInstanceRepository.findByFieldTemplate(pageable, new FieldTemplateModel(fieldTemplateId));
        for (FieldInstanceModel fieldInstanceModel: pageFieldInstance.getContent()) {
            this.deleteFieldInstance(fieldInstanceModel.getId());
        }
        for (int i = 2; i < pageFieldInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i, null);
            pageFieldInstance = this.fieldInstanceRepository.findByFieldTemplate(pageable, new FieldTemplateModel(fieldTemplateId));
            for (FieldInstanceModel fieldInstanceModel: pageFieldInstance.getContent()) {
                this.deleteFieldInstance(fieldInstanceModel.getId());
            }
        }
        return true;
    }

    private FieldInstanceDto fieldInstanceModelToDto(FieldInstanceModel fieldInstanceModel) {
        if (fieldInstanceModel == null) {
            return null;
        }
        FieldInstanceDto fieldInstanceDto = this.modelMapper.map(fieldInstanceModel, FieldInstanceDto.class);
        fieldInstanceDto.setFieldTemplateId(fieldInstanceModel.getFieldTemplate().getId());
        fieldInstanceDto.setFunctionInstanceId(fieldInstanceModel.getFunctionInstance().getId());
        fieldInstanceDto.setColumnInstances(this.columnInstanceServices.getColumnInstancesByFieldInstanceId(fieldInstanceDto.getId()));
        fieldInstanceDto.setCreatedByUserId(fieldInstanceModel.getCreatedByUser().getId());
        if (fieldInstanceModel.getClosedByUser() != null) {
            fieldInstanceDto.setClosedByUserId(fieldInstanceModel.getClosedByUser().getId());
        }

        return fieldInstanceDto;
    }
}
