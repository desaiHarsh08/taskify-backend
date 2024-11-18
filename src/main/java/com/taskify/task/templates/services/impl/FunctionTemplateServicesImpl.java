package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.DepartmentType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.services.FunctionInstanceServices;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.dtos.FieldTemplateDto;
import com.taskify.task.templates.dtos.FunctionTemplateDto;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.task.templates.repositories.FieldTemplateRepository;
import com.taskify.task.templates.repositories.FunctionTemplateRepository;
import com.taskify.task.templates.repositories.TaskTemplateRepository;
import com.taskify.task.templates.services.ColumnTemplateServices;
import com.taskify.task.templates.services.DropdownTemplateServices;
import com.taskify.task.templates.services.FieldTemplateServices;
import com.taskify.task.templates.services.FunctionTemplateServices;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FunctionTemplateServicesImpl implements FunctionTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FunctionTemplateRepository functionTemplateRepository;

    @Autowired
    private FieldTemplateServices fieldTemplateServices;

    @Autowired
    private ColumnTemplateServices columnTemplateServices;

    @Autowired
    private FunctionInstanceServices functionInstanceServices;

    @Autowired
    private DropdownTemplateServices dropdownTemplateServices;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private FieldTemplateRepository fieldTemplateRepository;

    @Override
    public FunctionTemplateDto createFunctionTemplate(FunctionTemplateDto functionTemplateDto) {
        FunctionTemplateModel foundDepartment = this.functionTemplateRepository.findByTitleAndDepartment(functionTemplateDto.getTitle(), functionTemplateDto.getDepartment()).orElse(null);
        // Step 1: Return if the function_template already exist
        if (foundDepartment != null) {
            return null;
        }
        // Step 2: Create the new function_template
        FunctionTemplateModel functionTemplateModel = this.modelMapper.map(functionTemplateDto, FunctionTemplateModel.class);
        if (functionTemplateDto.getTaskTemplatesId() != null) {
            functionTemplateModel.getTaskTemplates().add(new TaskTemplateModel(functionTemplateDto.getTaskTemplatesId()));
        }
        if (functionTemplateDto.getNextFollowUpFunctionTemplateId() != null) {
            functionTemplateModel.setNextFollowUpFunctionTemplateModel(new FunctionTemplateModel(functionTemplateDto.getNextFollowUpFunctionTemplateId()));
        }
        functionTemplateModel.setDepartment(functionTemplateDto.getDepartment());
        // Step 3: Create the all the field_templates
        List<FieldTemplateModel> fieldTemplateModels = new ArrayList<>();
        for (FieldTemplateDto fieldTemplateDto: functionTemplateDto.getFieldTemplates()) {
            // Create the field_template
            fieldTemplateDto = this.fieldTemplateServices.createFieldTemplate(fieldTemplateDto);
            fieldTemplateModels.add(new FieldTemplateModel(fieldTemplateDto.getId()));
        }
        // Step 4: Set the field_templates
        functionTemplateModel.setFieldTemplates(fieldTemplateModels);
        // Step 5: Save the function_template
        functionTemplateModel = this.functionTemplateRepository.save(functionTemplateModel);
        // Step 6: Create the dropdown if exist
        for (DropdownTemplateDto dropdownTemplateDto: functionTemplateDto.getDropdownTemplates()) {
            dropdownTemplateDto.setFunctionTemplateId(functionTemplateModel.getId());
            this.dropdownTemplateServices.createDropdownTemplate(dropdownTemplateDto);
        }

        return this.functionTemplateModelToDto(functionTemplateModel);
    }

    @Override
    public List<FunctionTemplateDto> getAllFunctionTemplates() {
        List<FunctionTemplateModel> functionTemplateModels = this.functionTemplateRepository.findAll();
        if (functionTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return functionTemplateModels.stream().map(this::functionTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<FunctionTemplateDto> getFunctionTemplatesByTaskTemplateId(Long taskTemplateId) {
        List<FunctionTemplateModel> functionTemplateModels = this.functionTemplateRepository.findByTaskTemplates(new TaskTemplateModel(taskTemplateId));
        if (functionTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return functionTemplateModels.stream().map(this::functionTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public FunctionTemplateDto getFunctionTemplateById(Long id) {
        FunctionTemplateModel functionTemplateModel = this.functionTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", id, true)
        );

        return this.functionTemplateModelToDto(functionTemplateModel);
    }

    @Override
    public FunctionTemplateDto getFunctionTemplateByTitle(String title) {
        FunctionTemplateModel functionTemplateModel = this.functionTemplateRepository.findByTitle(title).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "title", title, true)
        );

        return this.functionTemplateModelToDto(functionTemplateModel);
    }

    @Override
    public FunctionTemplateDto getFunctionTemplateByTitleAndDepartment(String title, DepartmentType department) {
        FunctionTemplateModel functionTemplateModel = this.functionTemplateRepository.findByTitleAndDepartment(title, department).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "title and department", title + ", " + department, true)
        );

        return this.functionTemplateModelToDto(functionTemplateModel);
    }

    @Override
    public FunctionTemplateDto updateFunctionTemplate(FunctionTemplateDto functionTemplateDto) {
        FunctionTemplateModel foundFunctionTemplateModel = this.functionTemplateRepository.findById(functionTemplateDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", functionTemplateDto.getId(), true)
        );
        // Update the function_template attributes
        foundFunctionTemplateModel.setTitle(functionTemplateDto.getTitle());
        foundFunctionTemplateModel.setDescription(functionTemplateDto.getDescription());
        foundFunctionTemplateModel.setDepartment(functionTemplateDto.getDepartment());
        foundFunctionTemplateModel.setDepartment(functionTemplateDto.getDepartment());
        foundFunctionTemplateModel.setChoice(functionTemplateDto.isChoice());
        if (functionTemplateDto.getNextFollowUpFunctionTemplateId() != null) {
            foundFunctionTemplateModel.setNextFollowUpFunctionTemplateModel(new FunctionTemplateModel(functionTemplateDto.getNextFollowUpFunctionTemplateId()));
        }
        // Update the field_templates
        for (FieldTemplateDto fieldTemplateDto: functionTemplateDto.getFieldTemplates()) {
            this.fieldTemplateServices.updateFieldTemplate(fieldTemplateDto);
        }
        // Save the changes
        foundFunctionTemplateModel = this.functionTemplateRepository.save(foundFunctionTemplateModel);

        return this.functionTemplateModelToDto(foundFunctionTemplateModel);
    }

    @Override
    public boolean unlinkFunctionTemplateFromTaskTemplate(Long id, Long taskTemplateId) {
        // Step 1: Fetch the TaskTemplate
        TaskTemplateModel taskTemplate = this.taskTemplateRepository.findById(taskTemplateId).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", taskTemplateId, true)
        );
        // Step 2: Delete all the associated function_instances
        if (!this.functionInstanceServices.deleteFunctionInstancesByFunctionTemplateId(id)) {
            throw new IllegalArgumentException("Unable to delete the function_instances in the process of deleting the function_template (having id: " + id + ")");
        }
        // Step 3: Fetch the FunctionTemplate
        FunctionTemplateModel functionTemplate = this.functionTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", id, true)
        );
        // Step 4: Unlink the FunctionTemplate
        functionTemplate.getTaskTemplates().remove(taskTemplate);
        taskTemplate.removeFunctionTemplate(functionTemplate);
        // Step 5: Save the changes
        this.taskTemplateRepository.save(taskTemplate);
        this.functionTemplateRepository.save(functionTemplate);

        return true; // Return true to indicate success
    }

    // This deletes the function_template
    @Override
    public boolean deleteFunctionTemplate(Long id) {
        // Step 1: Fetch the FunctionTemplateModel
        FunctionTemplateModel functionTemplate = functionTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", id, true)
        );
        // Step 2: Remove associations with TaskTemplateModel
        for (TaskTemplateModel taskTemplate : functionTemplate.getTaskTemplates()) {
            taskTemplate.removeFunctionTemplate(functionTemplate);
            this.taskTemplateRepository.save(taskTemplate); // Save each TaskTemplate after removal
        }
        // Clear the taskTemplates list in functionTemplate to prevent errors during deletion
        functionTemplate.getTaskTemplates().clear();

        // Step 3: Remove associations with FieldTemplateModel
        for (FieldTemplateModel fieldTemplate : functionTemplate.getFieldTemplates()) {
            functionTemplate.removeFieldTemplate(fieldTemplate);
            this.fieldTemplateRepository.save(fieldTemplate); // Save each FieldTemplate after removal
        }
        // Clear the fieldTemplates list in functionTemplate to prevent errors during deletion
        functionTemplate.getFieldTemplates().clear();

        // Step 4: Delete the FunctionTemplateModel from the repository
        functionTemplateRepository.delete(functionTemplate);

        return true;
    }

    // This is used when we are deleting a task_template
    @Override
    public boolean unlinkFunctionTemplatesByTaskTemplateId(Long taskTemplateId) {
        // Step 1: Fetch the TaskTemplate
        TaskTemplateModel taskTemplate = this.taskTemplateRepository.findById(taskTemplateId).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", taskTemplateId, true)
        );

        // Step 2: Delete all the associated function_instances and unlink FunctionTemplates
        for (FunctionTemplateModel functionTemplateModel : taskTemplate.getFunctionTemplates()) {
            // Delete all the function_instances for each FunctionTemplateModel
            if (!this.functionInstanceServices.deleteFunctionInstancesByFunctionTemplateId(functionTemplateModel.getId())) {
                throw new IllegalArgumentException("Unable to delete the function_instances while unlinking function_templates (id: " + functionTemplateModel.getId() + ")");
            }
            // Step 3: Unlink the FunctionTemplate from the TaskTemplate
            taskTemplate.removeFunctionTemplate(functionTemplateModel);
        }

        // Step 4: Save the TaskTemplate changes to the repository
        this.taskTemplateRepository.save(taskTemplate);

        return true;
    }

    @Override
    @Transactional
    public boolean linkFunctionTemplateToTaskTemplate(Long id, Long taskTemplateId) {
        // Fetch FunctionTemplateModel and TaskTemplateModel with error handling
        FunctionTemplateModel functionTemplate = functionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", id, true));
        TaskTemplateModel taskTemplate = taskTemplateRepository.findById(taskTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.TASK, "id", taskTemplateId, true));

        // Link TaskTemplate to FunctionTemplate if not already linked
        if (functionTemplate.getTaskTemplates().stream().noneMatch(t -> t.getId().equals(taskTemplateId))) {
            functionTemplate.getTaskTemplates().add(taskTemplate);
        }
        functionTemplateRepository.save(functionTemplate);

        // Link FunctionTemplate to TaskTemplate if not already linked
        if (taskTemplate.getFunctionTemplates().stream().noneMatch(fn -> fn.getId().equals(functionTemplate.getId()))) {
            taskTemplate.getFunctionTemplates().add(functionTemplate);
        }
        taskTemplateRepository.save(taskTemplate);

        return true;
    }



    private FunctionTemplateDto functionTemplateModelToDto(FunctionTemplateModel functionTemplateModel) {
        if (functionTemplateModel == null) {
            return null;
        }
        FunctionTemplateDto functionTemplateDto = this.modelMapper.map(functionTemplateModel, FunctionTemplateDto.class);
        if (functionTemplateModel.getNextFollowUpFunctionTemplateModel() != null) {
            functionTemplateDto.setNextFollowUpFunctionTemplateId(functionTemplateModel.getNextFollowUpFunctionTemplateModel().getId());
        }

        functionTemplateDto.setFieldTemplates(new ArrayList<>());
        for (FieldTemplateModel fieldTemplateModel: functionTemplateModel.getFieldTemplates()) {
            functionTemplateDto.getFieldTemplates().add(this.fieldTemplateServices.getFieldTemplateById(fieldTemplateModel.getId()));
        }

        functionTemplateDto.setDropdownTemplates(this.dropdownTemplateServices.getDropdownTemplatesByFunctionTemplateId(functionTemplateDto.getId()));

        return functionTemplateDto;
    }
}
