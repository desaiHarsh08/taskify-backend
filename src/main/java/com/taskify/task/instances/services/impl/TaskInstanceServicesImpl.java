package com.taskify.task.instances.services.impl;

import com.taskify.analytics.dtos.ActivityLogDto;
import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.analytics.repositories.ActivityLogRepository;
import com.taskify.analytics.services.ActivityLogServices;
import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.PriorityType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.notifications.email.services.EmailServices;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.repositories.CustomerRepository;
import com.taskify.task.instances.dtos.FunctionInstanceDto;
import com.taskify.task.instances.dtos.TaskInstanceDto;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.instances.repositories.TaskInstanceRepository;
import com.taskify.task.instances.services.FunctionInstanceServices;
import com.taskify.task.instances.services.TaskInstanceServices;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.task.templates.repositories.DropdownTemplateRepository;
import com.taskify.task.templates.repositories.TaskTemplateRepository;
import com.taskify.user.models.UserModel;
import com.taskify.user.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskInstanceServicesImpl implements TaskInstanceServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private FunctionInstanceServices functionInstanceServices;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private DropdownTemplateRepository dropdownTemplateRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TaskInstanceDto createTaskInstance(TaskInstanceDto taskInstanceDto) {

        System.out.println(taskInstanceDto);

        // Step 1: Create the new task_instance from task_instance_dto
        TaskInstanceModel newTaskInstanceModel = this.modelMapper.map(taskInstanceDto, TaskInstanceModel.class);
        // Step 2: Set the customer
        newTaskInstanceModel.setCustomer(new CustomerModel(taskInstanceDto.getCustomerId()));
        // Step 3: Set the priority
        newTaskInstanceModel.setPriorityType(taskInstanceDto.getPriorityType());
        // Step 4: Set the task_template
        TaskTemplateModel taskTemplateModel = this.taskTemplateRepository.findById(taskInstanceDto.getTaskTemplateId()).orElseThrow(
                () -> new IllegalArgumentException("Please provide the valid task_template")
        );
        newTaskInstanceModel.setTaskTemplate(taskTemplateModel);
        // Step 5: Generate the abbreviation
        newTaskInstanceModel.setAbbreviation(this.generateTaskAbbreviation(taskTemplateModel, newTaskInstanceModel));
        // Step 6: Set the dropdown_template
        if (taskInstanceDto.getDropdownTemplateId() != null) {
            DropdownTemplateModel dropdownTemplateModel = this.dropdownTemplateRepository.findByIdAndTaskTemplate(
                            taskInstanceDto.getDropdownTemplateId(), taskTemplateModel)
                    .orElseThrow(() -> new IllegalArgumentException("Please provide a valid dropdown_template for the specified task_template"));

            newTaskInstanceModel.setDropdownTemplate(dropdownTemplateModel);
        }
        // Step 7: Set the created by user
        newTaskInstanceModel.setCreatedByUser(new UserModel(taskInstanceDto.getCreatedByUserId()));
        // Step 8: Set the assigned to user
        newTaskInstanceModel.setAssignedToUser(new UserModel(taskInstanceDto.getAssignedToUserId()));
        // Step 9: Save the new task_instances
        newTaskInstanceModel = this.taskInstanceRepository.save(newTaskInstanceModel);

        // Notify both the users (Created user and assigned user)
        this.emailServices.sendTaskAssignmentEmail(newTaskInstanceModel);

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.TASK);
        activityLogModel.setActionType(ActionType.CREATE);
        activityLogModel.setUser(new UserModel(taskInstanceDto.getCreatedByUserId()));
        activityLogModel.setTaskInstance(newTaskInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.taskInstanceModelToDto(newTaskInstanceModel);
    }

    @Override
    public PageResponse<TaskInstanceDto> getAllTaskInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findAll(pageable);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<TaskInstanceDto> getTaskInstancesByTaskTemplateById(int pageNumber, Integer pageSize, Long taskTemplateId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByTaskTemplate(pageable, new TaskTemplateModel(taskTemplateId));
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public List<TaskInstanceDto> getTaskInstancesByCustomerId(Long customerId) {
        List<TaskInstanceModel> taskInstanceModels = this.taskInstanceRepository.findByCustomer(new CustomerModel(customerId));
        if (taskInstanceModels.isEmpty()) {
            return new ArrayList<>();
        }

        return taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList());
    }

    @Override
    public PageResponse<TaskInstanceDto> getTaskInstancesByPriorityType(int pageNumber, Integer pageSize, PriorityType priorityType) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByPriorityType(pageable, priorityType);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<TaskInstanceDto> getTaskInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByCreatedByUser(pageable, new UserModel(createdByUserId));
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<TaskInstanceDto> getTaskInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByClosedByUser(pageable, new UserModel(closedByUserId));
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<TaskInstanceDto> getOverdueTaskInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findTaskInstancesByOverdue(pageable);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();
        System.out.println(pageTaskInstance.getTotalElements());

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<TaskInstanceDto> getTaskInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance;
        if (type.equals(DateParamType.CREATED)) {
            pageTaskInstance = this.taskInstanceRepository.findByCreatedAt(pageable, date);
        }
        else if (type.equals(DateParamType.UPDATED)) {
            pageTaskInstance = this.taskInstanceRepository.findByUpdatedAt(pageable, date);
        }
        else {
            pageTaskInstance = this.taskInstanceRepository.findByClosedAt(pageable, date);
        }

        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public TaskInstanceDto getTaskInstanceById(Long id) {
        TaskInstanceModel foundTaskInstanceModel = this.taskInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", id, false)
        );

        return this.taskInstanceModelToDto(foundTaskInstanceModel);
    }

    @Override
    public TaskInstanceDto updateTaskInstance(TaskInstanceDto taskInstanceDto, Long userId) {
        // Step 1: Check for task_instance does exist
        TaskInstanceModel foundTaskInstanceModel = this.taskInstanceRepository.findById(taskInstanceDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", taskInstanceDto.getId(), false)
        );
        // Step 2: Update the attributes
        foundTaskInstanceModel.setPriorityType(taskInstanceDto.getPriorityType());
        foundTaskInstanceModel.setPumpType(taskInstanceDto.getPumpType());
        foundTaskInstanceModel.setPumpManufacturer(taskInstanceDto.getPumpManufacturer());
        foundTaskInstanceModel.setRequirements(taskInstanceDto.getRequirements());
        foundTaskInstanceModel.setSpecifications(taskInstanceDto.getSpecifications());
        foundTaskInstanceModel.setProblemDescription(taskInstanceDto.getProblemDescription());
        foundTaskInstanceModel.setDropdownTemplate(new DropdownTemplateModel(taskInstanceDto.getDropdownTemplateId()));
        foundTaskInstanceModel.setAssignedToUser(new UserModel(taskInstanceDto.getAssignedToUserId()));
        foundTaskInstanceModel.setArchived(taskInstanceDto.isArchived());
        // Step 3: Save the changes
        foundTaskInstanceModel = this.taskInstanceRepository.save(foundTaskInstanceModel);

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.TASK);
        activityLogModel.setActionType(ActionType.UPDATE);
        activityLogModel.setUser(new UserModel(userId));
        activityLogModel.setTaskInstance(foundTaskInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.taskInstanceModelToDto(foundTaskInstanceModel);
    }

    @Override
    public TaskInstanceDto closeTaskInstance(Long id, Long closedByUserId) {
        // Step 1: Check for task_instance does exist
        TaskInstanceModel foundTaskInstanceModel = this.taskInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", id, false)
        );
        // Step 2: Check whether all functions are closed before closing a task
        List<FunctionInstanceDto> functionInstanceDtos = this.functionInstanceServices.getFunctionInstancesByTaskInstanceId(id);
        if (functionInstanceDtos.stream().anyMatch(fn -> fn.getClosedAt() == null)) {
            throw new IllegalArgumentException("Please close all the functions before closing a task");
        }
        // Step 3: Close the task
        foundTaskInstanceModel.setClosedAt(LocalDateTime.now());
        foundTaskInstanceModel.setClosedByUser(new UserModel(closedByUserId));
        // Step 4: Save the changes
        foundTaskInstanceModel = this.taskInstanceRepository.save(foundTaskInstanceModel);

        // Notify the closed by user
        this.emailServices.sendCloseTaskEmail(foundTaskInstanceModel);

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.TASK);
        activityLogModel.setActionType(ActionType.CLOSED);
        activityLogModel.setUser(new UserModel(closedByUserId));
        activityLogModel.setTaskInstance(foundTaskInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.taskInstanceModelToDto(foundTaskInstanceModel);
    }

    @Override
    public boolean deleteTaskInstance(Long id) {
        // Step 1: Check for task_instance does exist
        TaskInstanceDto foundTaskInstanceDto = this.getTaskInstanceById(id);
        // Step 2: Delete the function_instances
        this.functionInstanceServices.deleteFunctionInstancesByTaskInstanceId(id);
        // Step 3: Delete the activity_logs
        Pageable pageable = PageRequest.of(1, 100);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByTaskInstance(pageable, new TaskInstanceModel(id));
        for (ActivityLogModel activityLogModel: pageActivityLog.getContent()) {
            this.activityLogRepository.deleteById(activityLogModel.getId());
        }
        for (int i = 2; i < pageActivityLog.getTotalPages(); i++) {
            pageable = PageRequest.of(i, 100);
            pageActivityLog = this.activityLogRepository.findByTaskInstance(pageable, new TaskInstanceModel(id));
            for (ActivityLogModel activityLogModel: pageActivityLog.getContent()) {
                this.activityLogRepository.deleteById(activityLogModel.getId());
            }
        }
        // Step 4: Delete the task_instance
        this.taskInstanceRepository.deleteById(id);

        // TODO: Notify the by user
        // this.emailServices.sendCloseTaskEmail(foundTaskInstanceModel);

        return true;
    }

    // TODO
    @Override
    public boolean deleteTaskInstancesByTaskTemplateId(Long taskTemplateId) {
        Pageable pageable = Helper.getPageable(1);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByTaskTemplate(pageable, new TaskTemplateModel(taskTemplateId));
        for (TaskInstanceModel taskInstanceModel: pageTaskInstance.getContent()) {
            this.deleteTaskInstance(taskInstanceModel.getId());
        }
        for (int i = 1; i < pageTaskInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i);
            pageTaskInstance = this.taskInstanceRepository.findByTaskTemplate(pageable, new TaskTemplateModel(taskTemplateId));
            for (TaskInstanceModel taskInstanceModel: pageTaskInstance.getContent()) {
                this.deleteTaskInstance(taskInstanceModel.getId());
            }
        }
        return true;
    }

    // TODO
    @Override
    public boolean deleteTaskInstancesByDropdownTemplateId(Long dropdownTemplateId) {
        Pageable pageable = Helper.getPageable(1);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByDropdownTemplate(pageable, new DropdownTemplateModel(dropdownTemplateId));
        for (TaskInstanceModel taskInstanceModel: pageTaskInstance.getContent()) {
            this.deleteTaskInstance(taskInstanceModel.getId());
        }
        for (int i = 1; i < pageTaskInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i);
            pageTaskInstance = this.taskInstanceRepository.findByDropdownTemplate(pageable, new DropdownTemplateModel(dropdownTemplateId));
            for (TaskInstanceModel taskInstanceModel: pageTaskInstance.getContent()) {
                this.deleteTaskInstance(taskInstanceModel.getId());
            }
        }
        return true;
    }

    private String generateTaskAbbreviation(TaskTemplateModel taskTemplateModel, TaskInstanceModel taskInstanceModel) {
        // Get the first character of the task type
        String taskTemplateFirstCharacter = taskTemplateModel.getTitle().substring(0, 1);
        // Use LocalDate from LocalDateTime
        LocalDate createdDate = taskInstanceModel.getCreatedAt().toLocalDate();
        // Get the last two digits of the year
        int yearLastTwoDigits = createdDate.getYear() % 100;
        // Get the month value (already 1-based, no need to add 1)
        String month = String.format("%02d", createdDate.getMonthValue());

        List<TaskInstanceModel> taskInstanceModels = this.taskInstanceRepository.findTasksByYearAndMonth(createdDate.getYear(), createdDate.getMonthValue());

        String taskCount = "";
        String taskAbbreviation = "";
        for (TaskInstanceModel t : taskInstanceModels) {
            if (taskInstanceModel.getId() != null && t.getId().equals(taskInstanceModel.getId())) {
                taskCount = t.getAbbreviation().substring(5);
                taskAbbreviation = taskTemplateFirstCharacter + t.getAbbreviation().substring(1);
            }
        }

        if (!taskInstanceModels.isEmpty()) {
            if (taskCount.isEmpty()) { // New task
                int count = Integer.parseInt(taskInstanceModels.get(0).getAbbreviation().substring(5));
                taskCount = String.format("%03d", ++count);
            }
        } else {
            taskCount = String.format("%03d", 1);
        }

        taskAbbreviation = taskTemplateFirstCharacter + yearLastTwoDigits + month + taskCount;

        // System.out.println("taskCount: " + taskCount);

        // System.out.println("taskAbbreviation: " + taskAbbreviation);

        return taskAbbreviation;
    }

    private TaskInstanceDto taskInstanceModelToDto(TaskInstanceModel taskInstanceModel) {
        if (taskInstanceModel == null) {
            return null;
        }
        TaskInstanceDto taskInstanceDto = this.modelMapper.map(taskInstanceModel, TaskInstanceDto.class);
        taskInstanceDto.setTaskTemplateId(taskInstanceModel.getTaskTemplate().getId());
        taskInstanceDto.setCustomerId(taskInstanceModel.getCustomer().getId());
        if (taskInstanceModel.getDropdownTemplate() != null) {
            taskInstanceDto.setDropdownTemplateId(taskInstanceModel.getDropdownTemplate().getId());
        }
        taskInstanceDto.setClosedByUserId(taskInstanceModel.getCreatedByUser().getId());
        taskInstanceDto.setAssignedToUserId(taskInstanceModel.getAssignedToUser().getId());
        if (taskInstanceModel.getClosedByUser() != null) {
            taskInstanceDto.setClosedByUserId(taskInstanceModel.getClosedByUser().getId());
        }
//        taskInstanceDto.setFunctionInstances(this.functionInstanceServices.getFunctionInstancesByTaskInstanceId(taskInstanceDto.getId()));

        return taskInstanceDto;
    }
}
