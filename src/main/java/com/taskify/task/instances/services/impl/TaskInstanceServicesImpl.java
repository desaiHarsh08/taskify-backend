package com.taskify.task.instances.services.impl;

import com.taskify.Task;
import com.taskify.analytics.dtos.ActivityLogDto;
import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.analytics.repositories.ActivityLogRepository;
import com.taskify.analytics.services.ActivityLogServices;
import com.taskify.common.constants.*;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.notifications.email.services.EmailServices;
import com.taskify.stakeholders.dtos.CustomerDto;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.repositories.CustomerRepository;
import com.taskify.task.instances.dtos.*;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.instances.repositories.FunctionInstanceRepository;
import com.taskify.task.instances.repositories.TaskInstanceRepository;
import com.taskify.task.instances.services.FieldInstanceServices;
import com.taskify.task.instances.services.FunctionInstanceServices;
import com.taskify.task.instances.services.TaskInstanceServices;
import com.taskify.task.templates.models.*;
import com.taskify.task.templates.repositories.*;
import com.taskify.user.models.UserModel;
import com.taskify.user.models.ViewTaskModel;
import com.taskify.user.repositories.UserRepository;
import com.taskify.user.repositories.ViewTaskRepository;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.taskify.common.utils.Helper.PAGE_SIZE;

@Service
public class TaskInstanceServicesImpl implements TaskInstanceServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FieldTemplateRepository fieldTemplateRepository;

    @Autowired
    private ColumnTemplateRepository columnTemplateRepository;

    @Autowired
    private ViewTaskRepository viewTaskRepository;

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

    @Autowired
    private FunctionInstanceRepository functionInstanceRepository;

    @Autowired
    private FunctionTemplateRepository functionTemplateRepository;

    @Autowired
    private FieldInstanceServices fieldInstanceServices;

    @Override
    public List<TaskSummaryDto> getTasksSummary(List<TaskInstanceModel> taskInstanceModels) {
        List<TaskSummaryDto> taskSummaryDtos = new ArrayList<>();

        for (TaskInstanceModel taskInstanceModel : taskInstanceModels) {
            // Skip if the task is archived
            if (taskInstanceModel.isArchived()) {
                continue;
            }

            // Retrieve the function instances associated with the task instance
            List<FunctionInstanceModel> functionInstanceModels = functionInstanceRepository.findByTaskInstanceOrderByIdDesc(taskInstanceModel);

            // If no function instances, add basic TaskSummaryDto and continue
            if (functionInstanceModels.isEmpty()) {

                TaskSummaryDto taskSummaryDto = createTaskSummaryDto(taskInstanceModel, null, null);
                taskSummaryDtos.add(taskSummaryDto);
                continue;
            }


            // Try to find the specific FunctionInstanceModel
            FunctionInstanceModel functionInstanceModel = functionInstanceModels.stream()
                    .filter(fn -> fn.getFunctionTemplate().getId().equals(30L))
                    .findFirst()
                    .orElse(null);

            // If not found, add basic TaskSummaryDto with first functionInstanceModel ID
            if (functionInstanceModel == null) {
                TaskSummaryDto taskSummaryDto = createTaskSummaryDto(taskInstanceModel, functionInstanceModels.get(0).getId(), null);
                taskSummaryDtos.add(taskSummaryDto);
                continue;
            }

            // Retrieve the associated FunctionTemplateModel
            FunctionTemplateModel functionTemplateModel = functionTemplateRepository.findById(functionInstanceModel.getFunctionTemplate().getId()).orElse(null);
            if (functionTemplateModel == null || !functionTemplateModel.getTitle().equals("Receipt Note")) {
                continue;
            }

            // Process field templates
            List<FieldTemplateModel> fieldTemplateModels = fieldTemplateRepository.findByFunctionTemplates(functionTemplateModel);
            FieldTemplateModel fieldTemplateModel = fieldTemplateModels.stream()
                    .filter(f -> f.getTitle().equals("Job Information"))
                    .findFirst()
                    .orElse(null);

            if (fieldTemplateModel == null) {
                continue;
            }

            // Process column templates
            List<ColumnTemplateModel> columnTemplateModels = columnTemplateRepository.findByFieldTemplates(fieldTemplateModel);
            ColumnTemplateModel columnTemplateModel = columnTemplateModels.stream()
                    .filter(c -> c.getName().equals("Job Number"))
                    .findFirst()
                    .orElse(null);

            if (columnTemplateModel == null) {
                continue;
            }

            // Retrieve field instances and column instances
            List<FieldInstanceDto> fieldInstanceDtos = fieldInstanceServices.getFieldInstancesByFunctionInstanceId(functionInstanceModel.getId());
            FieldInstanceDto fieldInstanceDto = fieldInstanceDtos.stream()
                    .filter(fi -> fi.getFieldTemplateId().equals(fieldTemplateModel.getId()))
                    .findFirst()
                    .orElse(null);

            if (fieldInstanceDto == null) {
                continue;
            }

            ColumnInstanceDto columnInstanceDto = fieldInstanceDto.getColumnInstances().stream()
                    .filter(ci -> ci.getColumnTemplateId().equals(columnTemplateModel.getId()))
                    .findFirst()
                    .orElse(null);

            if (columnInstanceDto != null) {
                TaskSummaryDto taskSummaryDto = createTaskSummaryDto(taskInstanceModel, functionInstanceModels.get(0).getId(), columnInstanceDto.getTextValue());
                if (taskSummaryDto != null) {
                    taskSummaryDtos.add(taskSummaryDto);
                }
            }
        }

        return taskSummaryDtos;
    }

    private TaskSummaryDto createTaskSummaryDto(TaskInstanceModel taskInstanceModel, Long functionInstanceId, String jobNumber) {
        return new TaskSummaryDto(
                taskInstanceModel.getId(),
                taskInstanceModel.getTaskTemplate().getId(),
                taskInstanceModel.getAbbreviation(),
                jobNumber,
                taskInstanceModel.getCustomer().getId(),
                functionInstanceId,
                taskInstanceModel.getPriorityType(),
                taskInstanceModel.getClosedAt(),
                taskInstanceModel.getUpdatedAt()
        );
    }


    @Override
    public PageResponse<TaskSummaryDto> getTaskInstancesByIsClosed(int pageNumber, Integer pageSize, boolean isClosed) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstanceModels = this.taskInstanceRepository.findByIsClosedAndIsArchived(pageable, isClosed, false);

        return new PageResponse<>(
                pageNumber,
                pageTaskInstanceModels.getSize(),
                pageTaskInstanceModels.getTotalPages(),
                pageTaskInstanceModels.getTotalElements(),
                this.getTasksSummary(pageTaskInstanceModels.getContent())
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getDismantleDueTask(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstanceModels = this.taskInstanceRepository.findTaskInstancesByFunctionConditions(pageable, false);

        return new PageResponse<>(
                pageNumber,
                pageTaskInstanceModels.getSize(),
                pageTaskInstanceModels.getTotalPages(),
                pageTaskInstanceModels.getTotalElements(),
                this.getTasksSummary(pageTaskInstanceModels.getContent())
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getEstimateDueTask(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstanceModels = this.taskInstanceRepository.findTaskInstancesByLastFunctionConditions(pageable, false);

        return new PageResponse<>(
                pageNumber,
                pageTaskInstanceModels.getSize(),
                pageTaskInstanceModels.getTotalPages(),
                pageTaskInstanceModels.getTotalElements(),
                this.getTasksSummary(pageTaskInstanceModels.getContent())
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getPendingApprovalTask(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstanceModels = this.taskInstanceRepository.findTaskInstancesByLastFunctionTemplate50(pageable, false);

        return new PageResponse<>(
                pageNumber,
                pageTaskInstanceModels.getSize(),
                pageTaskInstanceModels.getTotalPages(),
                pageTaskInstanceModels.getTotalElements(),
                this.getTasksSummary(pageTaskInstanceModels.getContent())
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getApprovalStatusTask(int pageNumber, Integer pageSize, boolean status) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstanceModels = this.taskInstanceRepository.findTaskInstancesByFunctionFieldColumnConditions(pageable, status, false);

        return new PageResponse<>(
                pageNumber,
                pageTaskInstanceModels.getSize(),
                pageTaskInstanceModels.getTotalPages(),
                pageTaskInstanceModels.getTotalElements(),
                this.getTasksSummary(pageTaskInstanceModels.getContent())
        );
    }


    @Override
    public PageResponse<TaskSummaryDto> getAssignedTaskInstances(int pageNumber, Integer pageSize, Long assignedUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstanceModels = this.taskInstanceRepository.findByAssignedToUserAndIsArchived(pageable, new UserModel(assignedUserId), false);

        return new PageResponse<>(
                pageNumber,
                pageTaskInstanceModels.getSize(),
                pageTaskInstanceModels.getTotalPages(),
                pageTaskInstanceModels.getTotalElements(),
                this.getTasksSummary(pageTaskInstanceModels.getContent())
        );
    }

    public List<CustomerModel> searchCustomers(String customerName) {

        List<CustomerModel> customers = customerRepository.findByNamePhonePincodePersonOfContact(
                customerName.trim(),
                null,
                null,
                null
        );

        return customers;
    }

    @Override
    public PageResponse<TaskSummaryDto> searchTaskInstance(String searchTxt, int pageNumber, int pageSize) {
        System.out.println("Search Text: " + searchTxt);

        // Step 1: Load the matching customers by given `searchText`
        List<CustomerModel> matchingCustomers = this.searchCustomers(searchTxt);

        List<Long> matchingCustomerIds = matchingCustomers.stream()
                .map(CustomerModel::getId)
                .toList();

        System.out.println(matchingCustomers);
        System.out.println(matchingCustomerIds);

        // Step 2: Load all the taskSummary
        List<TaskSummaryDto> allTaskSummaryDtos = new ArrayList<>();
        PageResponse<TaskSummaryDto> taskSummaryDtoPageResponse = this.getAllTaskInstances(1, pageSize);
        allTaskSummaryDtos.addAll(taskSummaryDtoPageResponse.getContent());
        for (int i = 2; i <= taskSummaryDtoPageResponse.getTotalPages(); i++) {
            taskSummaryDtoPageResponse = this.getAllTaskInstances(i, pageSize);
            allTaskSummaryDtos.addAll(taskSummaryDtoPageResponse.getContent());
        }

        System.out.println("Total tasks_summary: " + allTaskSummaryDtos.size());

        // Step 3: Perform the filter
        Collection<TaskSummaryDto> filteredContent = allTaskSummaryDtos.stream()
        .filter(t -> (t.getJobNumber() != null && t.getJobNumber().toUpperCase().contains(searchTxt.toUpperCase())) ||
                (matchingCustomerIds.contains(t.getCustomerId())) || // Match by customer id
                (t.getAbbreviation() != null && t.getAbbreviation().toUpperCase().contains(searchTxt.toUpperCase())))
        .toList();

        // Convert filteredContent to a List
        List<TaskSummaryDto> filteredList = new ArrayList<>(filteredContent);

        // Calculate total records and paginate results
        if (!filteredList.isEmpty()) {
            int totalRecords = filteredList.size();
            int fromIndex = Math.min((pageNumber - 1) * pageSize, totalRecords);
            int toIndex = Math.min(fromIndex + pageSize, totalRecords);

            System.out.println("Searched task found: " + filteredList.size());
            System.out.println("pageNumber: " + pageNumber);
            System.out.println("pageSize: " + pageSize);

            System.out.println(fromIndex + ", " + toIndex + ", " + totalRecords);

            // If the fromIndex is greater than or equal to the totalRecords, no results should be returned
            if (fromIndex == toIndex) {
                return new PageResponse<>(
                        pageNumber,
                        pageSize,
                        (int) Math.ceil((double) totalRecords / pageSize),
                        totalRecords,
                        filteredList
                );
            }
            if (fromIndex > totalRecords) {
                System.out.println("No tasks found for the requested page.");
            }

            List<TaskSummaryDto> paginatedTasks = filteredList.subList(fromIndex, toIndex);
            return new PageResponse<>(
                    pageNumber,
                    pageSize,
                    (int) Math.ceil((double) totalRecords / pageSize),
                    totalRecords,
                    paginatedTasks
            );
        }

        return new PageResponse<>(
                pageNumber,
                pageSize,
                (int) 0,
                (int) 0,
                new ArrayList<TaskSummaryDto>()
        );

    }

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

        newTaskInstanceModel.setCreatedAt(LocalDateTime.now());
        newTaskInstanceModel.setUpdatedAt(LocalDateTime.now());

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
    public TaskInstanceDto getTaskInstanceByAbbreviation(String abbreviation) {
        TaskInstanceModel foundTaskInstanceModel = this.taskInstanceRepository.findByAbbreviation(abbreviation).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "abbreviation", abbreviation, false)
        );

        return this.taskInstanceModelToDto(foundTaskInstanceModel);
    }

    @Override
    public PageResponse<TaskSummaryDto> getTaskByAbbreviationAndCreatedDate(int pageNumber, Integer pageSize, String taskAbbreviation,
                                                                    LocalDate date) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = Helper.getPageable(pageNumber);

        Page<TaskInstanceModel> pageTask = this.taskInstanceRepository.findByAbbreviationAndCreatedDate(taskAbbreviation,
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(), pageable);

        List<TaskInstanceModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.getTasksSummary(pageTask.getContent())
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getAllTaskInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByIsArchived(false, pageable);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getTaskInstancesByTaskTemplateById(int pageNumber, Integer pageSize, Long taskTemplateId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByTaskTemplateAndIsArchived(pageable, new TaskTemplateModel(taskTemplateId), false);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
        );
    }

    @Override
    public List<TaskInstanceDto> getTaskInstancesByCustomerId(Long customerId) {
        List<TaskInstanceModel> taskInstanceModels = this.taskInstanceRepository.findByCustomerAndIsArchived(new CustomerModel(customerId), false);
        if (taskInstanceModels.isEmpty()) {
            return new ArrayList<>();
        }

        return taskInstanceModels.stream().map(this::taskInstanceModelToDto).collect(Collectors.toList());
    }

    @Override
    public PageResponse<TaskSummaryDto> getTaskInstancesByPriorityType(int pageNumber, Integer pageSize, PriorityType priorityType) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByPriorityTypeAndIsArchived(pageable, priorityType, false);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getTaskInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByCreatedByUserAndIsArchived(pageable, new UserModel(createdByUserId), false);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getTaskInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByClosedByUserAndIsArchived(pageable, new UserModel(closedByUserId), false);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getOverdueTaskInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findTaskInstancesByOverdueAndIsArchived(pageable, false);
        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();
        System.out.println(pageTaskInstance.getTotalElements());

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
        );
    }

    @Override
    public PageResponse<TaskSummaryDto> getTaskInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize, SortingType.DESC, "updatedAt");
        Page<TaskInstanceModel> pageTaskInstance;
        if (type.equals(DateParamType.CREATED)) {
            pageTaskInstance = this.taskInstanceRepository.findByCreatedAtAndIsArchived(pageable, date, false);
        }
        else if (type.equals(DateParamType.UPDATED)) {
            pageTaskInstance = this.taskInstanceRepository.findByUpdatedAtAndIsArchived(pageable, date, false);
        }
        else {
            pageTaskInstance = this.taskInstanceRepository.findByClosedAtAndIsArchived(pageable, date, false);
        }

        List<TaskInstanceModel> taskInstanceModels = pageTaskInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageTaskInstance.getTotalPages(),
                pageTaskInstance.getTotalElements(),
                this.getTasksSummary(taskInstanceModels)
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
        foundTaskInstanceModel.setUpdatedAt(LocalDateTime.now());

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
        foundTaskInstanceModel.setUpdatedAt(LocalDateTime.now());
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
        Pageable pageable = PageRequest.of(0, 100);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByTaskInstance(pageable, new TaskInstanceModel(id));
        for (ActivityLogModel activityLogModel: pageActivityLog.getContent()) {
            this.activityLogRepository.deleteById(activityLogModel.getId());
        }
        for (int i = 1; i < pageActivityLog.getTotalPages(); i++) {
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
        Page<TaskInstanceModel> pageTaskInstance = this.taskInstanceRepository.findByTaskTemplateAndIsArchived(pageable, new TaskTemplateModel(taskTemplateId), false);
        for (TaskInstanceModel taskInstanceModel: pageTaskInstance.getContent()) {
            this.deleteTaskInstance(taskInstanceModel.getId());
        }
        for (int i = 1; i < pageTaskInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i);
            pageTaskInstance = this.taskInstanceRepository.findByTaskTemplateAndIsArchived(pageable, new TaskTemplateModel(taskTemplateId), false);
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
        // Fetch all the task created in current month
        List<TaskInstanceModel> taskInstanceModels = this.taskInstanceRepository.findTasksByYearAndMonthAndIsArchived(createdDate.getYear(), createdDate.getMonthValue(), false);

        int taskCount = 0;
        if (!taskInstanceModels.isEmpty()) {
            taskCount = taskInstanceModels.size();
        }

        String taskAbbreviation = "";
//        for (TaskInstanceModel t : taskInstanceModels) {
//            if (taskInstanceModel.getId() != null && t.getId().equals(taskInstanceModel.getId())) {
//                taskCount = t.getAbbreviation().substring(5);
//                taskAbbreviation = taskTemplateFirstCharacter + t.getAbbreviation().substring(1);
//            }
//        }
//
//        if (!taskInstanceModels.isEmpty()) {
//            if (taskCount.isEmpty()) { // New task
//                int count = Integer.parseInt(taskInstanceModels.get(0).getAbbreviation().substring(5));
//                taskCount = String.format("%03d", ++count);
//            }
//        } else {
//            taskCount = String.format("%03d", 1);
//        }

        taskAbbreviation = taskTemplateFirstCharacter + yearLastTwoDigits + month + String.format("%03d", ++taskCount);

         System.out.println("taskCount: " + taskCount);

         System.out.println("taskAbbreviation: " + taskAbbreviation);

//        throw new IllegalArgumentException("Throw error!");

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
