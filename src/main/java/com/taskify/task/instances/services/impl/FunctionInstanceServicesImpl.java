package com.taskify.task.instances.services.impl;

import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.analytics.repositories.ActivityLogRepository;
import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.notifications.email.services.EmailServices;
import com.taskify.task.instances.dtos.FieldInstanceDto;
import com.taskify.task.instances.dtos.FunctionInstanceDto;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.instances.repositories.FunctionInstanceRepository;
import com.taskify.task.instances.repositories.TaskInstanceRepository;
import com.taskify.task.instances.services.FieldInstanceServices;
import com.taskify.task.instances.services.FunctionInstanceServices;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.task.templates.repositories.DropdownTemplateRepository;
import com.taskify.task.templates.repositories.FunctionTemplateRepository;
import com.taskify.task.templates.repositories.TaskTemplateRepository;
import com.taskify.user.models.UserModel;
import com.taskify.user.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FunctionInstanceServicesImpl implements FunctionInstanceServices {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FunctionTemplateRepository functionTemplateRepository;

    @Autowired
    private DropdownTemplateRepository dropdownTemplateRepository;

    @Autowired
    private FunctionInstanceRepository functionInstanceRepository;

    @Autowired
    private FieldInstanceServices fieldInstanceServices;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private EmailServices emailServices;

    @Override
    public FunctionInstanceDto createFunctionInstance(FunctionInstanceDto functionInstanceDto, Long assignedToUserId) {
        System.out.println(functionInstanceDto);
        // Step 1: Create the new_function
        FunctionInstanceModel functionInstanceModel = this.modelMapper.map(functionInstanceDto, FunctionInstanceModel.class);
        // Step 2: Fetch the task_instance
        TaskInstanceModel taskInstanceModel = this.taskInstanceRepository.findById(functionInstanceDto.getTaskInstanceId()).orElseThrow(
                () -> new IllegalArgumentException("Please provide the valid task_instance")
        );
        TaskTemplateModel taskTemplateModel = this.taskTemplateRepository.findById(taskInstanceModel.getTaskTemplate().getId()).orElseThrow(
                () -> new IllegalArgumentException("Please provide the valid task_template")
        );
        taskInstanceModel.setTaskTemplate(taskTemplateModel);
        // Step 3: Set task assigned to user
        taskInstanceModel.setAssignedToUser(new UserModel(assignedToUserId));
        // Step 4: Set the task_instance in new_function_instance
        functionInstanceModel.setTaskInstance(taskInstanceModel);
        // Step 5: Set the created by user
        functionInstanceModel.setCreatedByUser(new UserModel(functionInstanceDto.getCreatedByUserId()));
        // Step 6: Set the function_template
        functionInstanceModel.setFunctionTemplate(new FunctionTemplateModel(functionInstanceDto.getFunctionTemplateId()));
        // Step 7: Save the fn
        functionInstanceModel = this.functionInstanceRepository.save(functionInstanceModel);
        // Step 8: Create the field_instances
        for (FieldInstanceDto fieldInstanceDto: functionInstanceDto.getFieldInstances()) {
            fieldInstanceDto.setFunctionInstanceId(functionInstanceModel.getId());
            this.fieldInstanceServices.createFieldInstance(fieldInstanceDto, assignedToUserId);
        }
        // Step 9: Save the task_instance
        this.taskInstanceRepository.save(taskInstanceModel);

        // Notify the user
        this.emailServices.sendFunctionAssignmentEmail(functionInstanceModel);

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.FUNCTION);
        activityLogModel.setActionType(ActionType.CREATE);
        activityLogModel.setUser(new UserModel(functionInstanceDto.getCreatedByUserId()));
        activityLogModel.setFunctionInstance(functionInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.functionInstanceModelToDto(functionInstanceModel);
    }

    @Override
    public FunctionInstanceDto createFunctionAndClose(FunctionInstanceDto functionInstanceDto, Long assignedToUserId) {
        FunctionInstanceDto newFunctionInstanceDto = this.createFunctionInstance(functionInstanceDto, assignedToUserId);
        FunctionInstanceModel functionInstanceModel = this.modelMapper.map(functionInstanceDto, FunctionInstanceModel.class);
        functionInstanceModel.setClosedAt(LocalDateTime.now());
        functionInstanceModel.setClosedByUser(new UserModel(assignedToUserId));

        functionInstanceModel = this.functionInstanceRepository.save(functionInstanceModel);

        return this.functionInstanceModelToDto(functionInstanceModel);
    }

    @Override
    public PageResponse<FunctionInstanceDto> getAllFunctionInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FunctionInstanceModel> pageFunctionInstance = this.functionInstanceRepository.findAll(pageable);
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFunctionInstance.getTotalPages(),
                pageFunctionInstance.getTotalElements(),
                functionInstanceModels.stream().map(this::functionInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<FunctionInstanceDto> getFunctionInstancesByFunctionTemplateById(int pageNumber, Integer pageSize, Long functionTemplateId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FunctionInstanceModel> pageFunctionInstance = this.functionInstanceRepository.findByFunctionTemplate(pageable, new FunctionTemplateModel(functionTemplateId));
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFunctionInstance.getTotalPages(),
                pageFunctionInstance.getTotalElements(),
                functionInstanceModels.stream().map(this::functionInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public List<FunctionInstanceDto> getFunctionInstancesByTaskInstanceId(Long taskInstanceId) {
        List<FunctionInstanceModel> functionInstanceModels = this.functionInstanceRepository.findByTaskInstance(new TaskInstanceModel(taskInstanceId));
        if (functionInstanceModels.isEmpty()) {
            return new ArrayList<>();
        }

        return functionInstanceModels.stream().map(this::functionInstanceModelToDto).collect(Collectors.toList());
    }

    @Override
    public PageResponse<FunctionInstanceDto> getFunctionInstancesByCreatedByUserId(int pageNumber, Integer pageSize, Long createdByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FunctionInstanceModel> pageFunctionInstance = this.functionInstanceRepository.findByCreatedByUser(pageable, new UserModel(createdByUserId));
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFunctionInstance.getTotalPages(),
                pageFunctionInstance.getTotalElements(),
                functionInstanceModels.stream().map(this::functionInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<FunctionInstanceDto> getFunctionInstancesByClosedByUserId(int pageNumber, Integer pageSize, Long closedByUserId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FunctionInstanceModel> pageFunctionInstance = this.functionInstanceRepository.findByClosedByUser(pageable, new UserModel(closedByUserId));
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFunctionInstance.getTotalPages(),
                pageFunctionInstance.getTotalElements(),
                functionInstanceModels.stream().map(this::functionInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<FunctionInstanceDto> getFunctionInstancesByDate(int pageNumber, Integer pageSize, LocalDateTime date, DateParamType type) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<FunctionInstanceModel> pageFunctionInstance;
        if (type.equals(DateParamType.CREATED)) {
            pageFunctionInstance = this.functionInstanceRepository.findByCreatedAt(pageable, date);
        }
        else if (type.equals(DateParamType.UPDATED)) {
            pageFunctionInstance = this.functionInstanceRepository.findByUpdatedAt(pageable, date);
        }
        else {
            pageFunctionInstance = this.functionInstanceRepository.findByClosedAt(pageable, date);
        }
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageFunctionInstance.getTotalPages(),
                pageFunctionInstance.getTotalElements(),
                functionInstanceModels.stream().map(this::functionInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public FunctionInstanceDto getFunctionInstanceById(Long id) {
        FunctionInstanceModel foundFunctionInstanceModel = this.functionInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", id, false)
        );

        return this.functionInstanceModelToDto(foundFunctionInstanceModel);
    }

    @Override
    public FunctionInstanceDto updateFunctionInstance(FunctionInstanceDto functionInstanceDto, Long userId) {
        // Step 1: Check for function_instance does exist
        FunctionInstanceModel foundFunctionInstanceModel = this.functionInstanceRepository.findById(functionInstanceDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", functionInstanceDto.getId(), false)
        );
        // Step 2: Update the attributes
        foundFunctionInstanceModel.setRemarks(functionInstanceDto.getRemarks());
        foundFunctionInstanceModel.setDueDate(functionInstanceDto.getDueDate());
        foundFunctionInstanceModel.setDropdownTemplate(new DropdownTemplateModel(functionInstanceDto.getDropdownTemplateId()));
        // Step 2: Save the changes
        foundFunctionInstanceModel = this.functionInstanceRepository.save(foundFunctionInstanceModel);

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.FUNCTION);
        activityLogModel.setActionType(ActionType.CLOSED);
        activityLogModel.setUser(new UserModel(userId));
        activityLogModel.setFunctionInstance(foundFunctionInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.functionInstanceModelToDto(foundFunctionInstanceModel);
    }

    @Override
    public FunctionInstanceDto closeFunction(Long id, Long closedByUserId) {
        // Step 1: Check for function_instance does exist
        FunctionInstanceModel foundFunctionInstanceModel = this.functionInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", id, false)
        );
        // Step 2: Fetch the by user
        UserModel closedByUserModel = this.userRepository.findById(closedByUserId).orElseThrow(
                () -> new IllegalArgumentException("Please provide the valid user")
        );
        // Step 3: Check if all the field_instances are closed
        List<FieldInstanceDto> fieldInstanceDtos = this.fieldInstanceServices.getFieldInstancesByFunctionInstanceId(id);
        if(fieldInstanceDtos.stream().anyMatch(fld -> fld.getClosedAt() == null)) {
            throw new IllegalArgumentException("Please close all the field_instances");
        }
        // Step 4: Close the function
        foundFunctionInstanceModel.setClosedAt(LocalDateTime.now());
        foundFunctionInstanceModel.setClosedByUser(closedByUserModel);
        // Step 5: Save the changes
        foundFunctionInstanceModel = this.functionInstanceRepository.save(foundFunctionInstanceModel);

        // TODO:Notify the user

        // Log the activity
        ActivityLogModel activityLogModel = new ActivityLogModel();
        activityLogModel.setResourceType(ResourceType.FUNCTION);
        activityLogModel.setActionType(ActionType.CLOSED);
        activityLogModel.setUser(new UserModel(closedByUserId));
        activityLogModel.setFunctionInstance(foundFunctionInstanceModel);
        this.activityLogRepository.save(activityLogModel);

        return this.functionInstanceModelToDto(foundFunctionInstanceModel);

    }

    @Override
    public boolean deleteFunctionInstance(Long id) {
        // Step 1: Check for function_instance does exist
        FunctionInstanceDto foundFunctionInstanceDto = this.getFunctionInstanceById(id);
        // Step 2: Delete all the field_instance
        for (FieldInstanceDto fieldInstanceDto: foundFunctionInstanceDto.getFieldInstances()) {
            if (!this.fieldInstanceServices.deleteFieldInstance(fieldInstanceDto.getId())) {
                throw new IllegalArgumentException("Unable to delete the field_instance(" + fieldInstanceDto.getId() + ")" + " in the process for deleting function_instance (having id: " + " " + id + ").");
            }
        }
        // Step 3: Delete all the activity_logs
        Pageable pageable = PageRequest.of(1, 100);
        Page<ActivityLogModel> pageActivityLog = this.activityLogRepository.findByFunctionInstance(pageable, new FunctionInstanceModel(id));
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

        // Step 4: Delete the function_instance
        this.functionInstanceRepository.deleteById(id);

        return true;
    }

    @Override
    public boolean deleteFunctionInstancesByTaskInstanceId(Long taskInstanceId) {
        List<FunctionInstanceModel> functionInstanceModels = this.functionInstanceRepository.findByTaskInstance(new TaskInstanceModel(taskInstanceId));
        for (FunctionInstanceModel functionInstanceModel: functionInstanceModels) {
            this.deleteFunctionInstance(functionInstanceModel.getId());
        }

        return true;
    }

    @Override
    public boolean deleteFunctionInstancesByFunctionTemplateId(Long functionTemplateId) {
        Pageable pageable = Helper.getPageable(1, null);
        Page<FunctionInstanceModel> pageFunctionInstance = this.functionInstanceRepository.findByFunctionTemplate(pageable, new FunctionTemplateModel(functionTemplateId));
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();
        for (FunctionInstanceModel functionInstanceModel: functionInstanceModels) {
            this.deleteFunctionInstance(functionInstanceModel.getId());
        }
        for (int i = 2; i < pageFunctionInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i, null);
            pageFunctionInstance = this.functionInstanceRepository.findByFunctionTemplate(pageable, new FunctionTemplateModel(functionTemplateId));
            functionInstanceModels = pageFunctionInstance.getContent();
            for (FunctionInstanceModel functionInstanceModel: functionInstanceModels) {
                this.deleteFunctionInstance(functionInstanceModel.getId());
            }
        }

        return true;
    }

    @Override
    public boolean deleteFunctionInstancesByDropdownTemplateId(Long dropdownTemplateId) {
        Pageable pageable = Helper.getPageable(1, null);
        Page<FunctionInstanceModel> pageFunctionInstance = this.functionInstanceRepository.findByDropdownTemplate(pageable, new DropdownTemplateModel(dropdownTemplateId));
        List<FunctionInstanceModel> functionInstanceModels = pageFunctionInstance.getContent();
        for (FunctionInstanceModel functionInstanceModel: functionInstanceModels) {
            this.deleteFunctionInstance(functionInstanceModel.getId());
        }
        for (int i = 2; i < pageFunctionInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i, null);
            pageFunctionInstance = this.functionInstanceRepository.findByDropdownTemplate(pageable, new DropdownTemplateModel(dropdownTemplateId));
            functionInstanceModels = pageFunctionInstance.getContent();
            for (FunctionInstanceModel functionInstanceModel: functionInstanceModels) {
                this.deleteFunctionInstance(functionInstanceModel.getId());
            }
        }

        return true;
    }

    @Override
    public boolean uploadFiles(FunctionInstanceDto functionInstanceDto, MultipartFile[] files) {
        TaskInstanceModel taskInstanceModel = this.taskInstanceRepository.findById(functionInstanceDto.getId()).orElseThrow(
                () -> new IllegalArgumentException("No task_instance exist for id: " + functionInstanceDto.getId())
        );

        LocalDateTime date = LocalDateTime.now();

        // Create the directory path
        String directoryPath = this.getFilePath(functionInstanceDto);

        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        System.out.println("Attempting for Creating directory: -");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + directoryPath);
            }
        }
        for (MultipartFile file : files) {
            // Create filename
            String fileNamePrefix = taskInstanceModel.getAbbreviation() + "_" +
                    functionInstanceDto.getId() + "_" +
                    date.getYear() + "-" + date.getMonth() + 1 + "-" + date.getDayOfMonth() + "-" +
                    (date.getHour() + 1) + "-" + (date.getMinute() + 1) + "-" + (date.getSecond() + 1);

            // Extract the extension from the original file name
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            // Append the extension to the fileNamePrefix

            String fileName = fileNamePrefix + extension;

            System.out.println("saving...");
            this.saveFile(file, directoryPath, fileName);
        }

        return true;
    }

    private void saveFile(MultipartFile file, String fileDirectory, String fileName) {
        System.out.println(fileDirectory);
        // Define the directory path adjacent to the root directory
        try {
            Path directoryPath = Paths.get(fileDirectory);

            // Ensure the directory exists
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            System.out.println("directory exist, and now storing...");

            // Create the path for the file to be stored, using the provided fileName
            Path filePath = directoryPath.resolve(fileName);

            System.out.println("Full file path: " + filePath.toAbsolutePath());

            // Save the file to the defined directory
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException(ResourceType.FILE, "file", fileName, false);
        }
    }

    private String getFilePath(FunctionInstanceDto functionInstanceDto) {
        TaskInstanceModel foundTaskInstanceModel = this.taskInstanceRepository.findById(functionInstanceDto.getTaskInstanceId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", functionInstanceDto.getTaskInstanceId(), false)
        );
        TaskTemplateModel foundTaskTemplateModel = this.taskTemplateRepository.findById(foundTaskInstanceModel.getTaskTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", foundTaskInstanceModel.getTaskTemplate().getId(), true)
        );

        // Create the directory path
        return this.uploadDir + "/" + foundTaskTemplateModel.getTitle() + "/" +
                "TASK-" + foundTaskInstanceModel.getId() + "/FUNCTION-" + functionInstanceDto.getId() + "/";
    }


    private FunctionInstanceDto functionInstanceModelToDto(FunctionInstanceModel functionInstanceModel) {
        // Step 1: Check if the function_instance exist
        if (functionInstanceModel == null) {
            return null;
        }
        // Step 2: Convert to dto
        FunctionInstanceDto functionInstanceDto = this.modelMapper.map(functionInstanceModel, FunctionInstanceDto.class);
        functionInstanceDto.setFunctionTemplateId(functionInstanceModel.getFunctionTemplate().getId());
        functionInstanceDto.setTaskInstanceId(functionInstanceModel.getTaskInstance().getId());
        functionInstanceDto.setCreatedByUserId(functionInstanceModel.getCreatedByUser().getId());
        if (functionInstanceModel.getClosedByUser() != null) {
            functionInstanceDto.setClosedByUserId(functionInstanceModel.getClosedByUser().getId());
        }
        if (functionInstanceModel.getDropdownTemplate() != null) {
            functionInstanceDto.setDropdownTemplateId(functionInstanceModel.getDropdownTemplate().getId());
        }
        functionInstanceDto.setFieldInstances(this.fieldInstanceServices.getFieldInstancesByFunctionInstanceId(functionInstanceModel.getId()));
        // Step 3: Take the filePath, if exist
        // 1. Get the directory path
        String directoryPath = this.getFilePath(functionInstanceDto);
        // 2. Retrieve file names
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            List<String> filePaths = Arrays.stream(files).map(File::getAbsolutePath).toList();
            // Set file paths in DTO
            functionInstanceDto.setFilePaths(filePaths);
        }

        return functionInstanceDto;
    }
}
