package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.services.TaskInstanceServices;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.dtos.FunctionTemplateDto;
import com.taskify.task.templates.dtos.TaskTemplateDto;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.task.templates.repositories.TaskTemplateRepository;
import com.taskify.task.templates.services.DropdownTemplateServices;
import com.taskify.task.templates.services.FunctionTemplateServices;
import com.taskify.task.templates.services.TaskTemplateServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskTemplateServicesImpl implements TaskTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private FunctionTemplateServices functionTemplateServices;

    @Autowired
    private DropdownTemplateServices dropdownTemplateServices;

    @Autowired
    private TaskInstanceServices taskInstanceServices;

    @Override
    public TaskTemplateDto createTaskTemplate(TaskTemplateDto taskTemplateDto) {
        TaskTemplateModel foundTaskTemplateModel = this.taskTemplateRepository.findByTitle(taskTemplateDto.getTitle()).orElse(null);
        // Step 1: Return if the task_template already exist
        if (foundTaskTemplateModel != null) {
           return null;
        }
        // Step 2: Create the new task_template
        TaskTemplateModel taskTemplateModel = this.modelMapper.map(taskTemplateDto, TaskTemplateModel.class);
        // Step 3: Create the function_templates
        List<FunctionTemplateModel> functionTemplateModels = new ArrayList<>();
        for (FunctionTemplateDto functionTemplateDto: taskTemplateDto.getFunctionTemplates()) {
            // Creating the function_template
            FunctionTemplateDto savedFunctionTemplateDto = this.functionTemplateServices.createFunctionTemplate(functionTemplateDto);
            if (savedFunctionTemplateDto == null) { // Already exist!
                Long id = this.functionTemplateServices.getFunctionTemplateByTitleAndDepartment(functionTemplateDto.getTitle(), functionTemplateDto.getDepartment()).getId();
                if (functionTemplateModels.stream().noneMatch(fn -> fn.getId().equals(id))) { // Add the function_template if not exist
                    functionTemplateModels.add(new FunctionTemplateModel(id));
                }
            }
            else {
                functionTemplateModels.add(new FunctionTemplateModel(functionTemplateDto.getId()));
            }
        }
        // Step 4: Set the function_templates
        taskTemplateModel.setFunctionTemplates(functionTemplateModels);
        // Step 5: Save the new task_template
        taskTemplateModel = this.taskTemplateRepository.save(taskTemplateModel);
        // Step 6: Create the dropdown_template
        for (DropdownTemplateDto dropdownTemplateDto: taskTemplateDto.getDropdownTemplates()) {
            // Set the task_template_id
            dropdownTemplateDto.setTaskTemplateId(taskTemplateModel.getId());
            // Create the dropdown_template
            this.dropdownTemplateServices.createDropdownTemplate(dropdownTemplateDto);
        }

        return this.taskTemplateModelToDto(taskTemplateModel);
    }

    @Override
    public List<TaskTemplateDto> getAllTaskTemplates() {
        List<TaskTemplateModel> taskTemplateModels = this.taskTemplateRepository.findAll();
        if (taskTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return taskTemplateModels.stream().map(this::taskTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public TaskTemplateDto getTaskTemplateById(Long id) {
        TaskTemplateModel foundTaskTemplateModel = this.taskTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", id, true)
        );

        return this.taskTemplateModelToDto(foundTaskTemplateModel);
    }

    @Override
    public TaskTemplateDto getTaskTemplateByTitle(String title) {
        TaskTemplateModel foundTaskTemplateModel = this.taskTemplateRepository.findByTitle(title).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "title", title, true)
        );

        return this.taskTemplateModelToDto(foundTaskTemplateModel);
    }

    @Override
    public TaskTemplateDto updateTaskTemplate(TaskTemplateDto taskTemplateDto) {
        // Step 1: Fetch the existing task_template
        TaskTemplateModel foundTaskTemplateModel = this.taskTemplateRepository.findById(taskTemplateDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", taskTemplateDto.getId(), true)
        );
        // Step 2: Update the task_template
        foundTaskTemplateModel.setTitle(taskTemplateDto.getTitle());
        // Step 3: Update the function_templates
        List<FunctionTemplateModel> functionTemplateModels = new ArrayList<>();
        for (FunctionTemplateDto functionTemplateDto: taskTemplateDto.getFunctionTemplates()) {
            functionTemplateDto = this.functionTemplateServices.updateFunctionTemplate(functionTemplateDto);
            functionTemplateModels.add(new FunctionTemplateModel(functionTemplateDto.getId()));
        }
        // Step 4: Update the dropdown_templates
        foundTaskTemplateModel.setFunctionTemplates(functionTemplateModels);
        // Step 5: Save the task_template
        foundTaskTemplateModel.setFunctionTemplates(functionTemplateModels);

        return this.taskTemplateModelToDto(foundTaskTemplateModel);
    }

    @Override
    public boolean deleteTaskTemplate(Long id) {
        TaskTemplateDto foundTaskTemplateDto = this.getTaskTemplateById(id);

        // Step 1: Delete all the task_instances
        if (!this.taskInstanceServices.deleteTaskInstancesByTaskTemplateId(id)) {
            throw new IllegalArgumentException(
                    "Unable to delete the task_instances for `" + foundTaskTemplateDto.getTitle() +
                            "` (id: " + id + ")"
            );
        }

        // Step 2: Delete all the function_templates
        if (!this.functionTemplateServices.unlinkFunctionTemplatesByTaskTemplateId(id)) {
            throw new IllegalArgumentException(
                    "Unable to delete the function_templates for `" + foundTaskTemplateDto.getTitle() +
                            "` (id: " + id + ")"
            );
        }

        // Step 3: Delete all the dropdown_templates
        if (!this.dropdownTemplateServices.deleteDropdownTemplatesByTaskTemplateId(id)) {
            throw new IllegalArgumentException(
                    "Unable to delete the dropdown_templates for `" + foundTaskTemplateDto.getTitle() +
                            "` (id: " + id + ")"
            );
        }

        // Step 4: Delete the task_template
        this.taskTemplateRepository.deleteById(id);

        return true;
    }

    private TaskTemplateDto taskTemplateModelToDto(TaskTemplateModel taskTemplateModel) {
        if (taskTemplateModel == null) {
            return null;
        }
        TaskTemplateDto taskTemplateDto = this.modelMapper.map(taskTemplateModel, TaskTemplateDto.class);
        taskTemplateDto.setFunctionTemplates(new ArrayList<>());
        for (FunctionTemplateModel functionTemplateModel: taskTemplateModel.getFunctionTemplates()) {
            FunctionTemplateDto functionTemplateDto = this.functionTemplateServices.getFunctionTemplateById(functionTemplateModel.getId());
            functionTemplateDto.setTaskTemplatesId(taskTemplateDto.getId());
            taskTemplateDto.getFunctionTemplates().add(functionTemplateDto);
        }
        taskTemplateDto.setDropdownTemplates(this.dropdownTemplateServices.getDropdownTemplatesByTaskTemplateId(taskTemplateDto.getId()));

        return taskTemplateDto;
    }

}
