package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.services.ColumnInstanceServices;
import com.taskify.task.instances.services.FunctionInstanceServices;
import com.taskify.task.instances.services.TaskInstanceServices;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.task.templates.repositories.DropdownTemplateRepository;
import com.taskify.task.templates.services.DropdownTemplateServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DropdownTemplateServicesImpl implements DropdownTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DropdownTemplateRepository dropdownTemplateRepository;

    @Autowired
    private TaskInstanceServices taskInstanceServices;

    @Autowired
    private FunctionInstanceServices functionInstanceServices;

    @Autowired
    private ColumnInstanceServices columnInstanceServices;

    @Override
    public DropdownTemplateDto createDropdownTemplate(DropdownTemplateDto dropdownTemplateDto) {
        // Step 1: Return if the dropdown_template already exist
        switch (dropdownTemplateDto.getLevel()) {
            case TASK -> {
                if (this.dropdownTemplateRepository.findByGroupAndLevelAndTaskTemplateAndValue(
                        dropdownTemplateDto.getGroup(),
                        dropdownTemplateDto.getLevel(),
                        new TaskTemplateModel(dropdownTemplateDto.getTaskTemplateId()),
                        dropdownTemplateDto.getValue()
                ) != null) {
                    return null;
                }
            }
            case FUNCTION -> {
                if (this.dropdownTemplateRepository.findByGroupAndLevelAndFunctionTemplateAndValue(
                        dropdownTemplateDto.getGroup(),
                        dropdownTemplateDto.getLevel(),
                        new FunctionTemplateModel(dropdownTemplateDto.getFunctionTemplateId()),
                        dropdownTemplateDto.getValue()
                ) != null) {
                    return null;
                }
            }
            case COLUMN -> {
                if (this.dropdownTemplateRepository.findByGroupAndLevelAndColumnTemplateAndValue(
                        dropdownTemplateDto.getGroup(),
                        dropdownTemplateDto.getLevel(),
                        new ColumnTemplateModel(dropdownTemplateDto.getColumnTemplateId()),
                        dropdownTemplateDto.getValue()
                ) != null) {
                    return null;
                }
            }
        }
        // Step 2: Create the new dropdown_template
        DropdownTemplateModel dropdownTemplateModel = this.modelMapper.map(dropdownTemplateDto, DropdownTemplateModel.class);
        // Step 3: Set the level
        dropdownTemplateModel.setLevel(dropdownTemplateDto.getLevel());
        // Step 4: Set the association
        dropdownTemplateModel.setAssociatedTemplateId(dropdownTemplateDto);
        // Step 5: Save the dropdown_template
        dropdownTemplateModel = this.dropdownTemplateRepository.save(dropdownTemplateModel);

        return this.dropdownTemplateModelToDto(dropdownTemplateModel);
    }

    @Override
    public List<DropdownTemplateDto> getAllDropdownTemplates() {
        List<DropdownTemplateModel> dropdownTemplateModels = this.dropdownTemplateRepository.findAll();
        if (dropdownTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return dropdownTemplateModels.stream().map(this::dropdownTemplateModelToDto).collect(Collectors.toList());
    }

    public DropdownTemplateDto getDropdownTemplateByAssociation(DropdownTemplateDto dropdownTemplateDto) {
        DropdownTemplateModel foundDropdownTemplate = new DropdownTemplateModel();
        switch (dropdownTemplateDto.getLevel()) {
            case TASK -> {
                foundDropdownTemplate = this.dropdownTemplateRepository.findByGroupAndLevelAndTaskTemplateAndValue(
                        dropdownTemplateDto.getGroup(),
                        dropdownTemplateDto.getLevel(),
                        new TaskTemplateModel(dropdownTemplateDto.getTaskTemplateId()),
                        dropdownTemplateDto.getValue()
                );

                return this.dropdownTemplateModelToDto(foundDropdownTemplate);
            }
            case FUNCTION -> {
                foundDropdownTemplate = this.dropdownTemplateRepository.findByGroupAndLevelAndFunctionTemplateAndValue(
                        dropdownTemplateDto.getGroup(),
                        dropdownTemplateDto.getLevel(),
                        new FunctionTemplateModel(dropdownTemplateDto.getFunctionTemplateId()),
                        dropdownTemplateDto.getValue()
                );

                return this.dropdownTemplateModelToDto(foundDropdownTemplate);
            }
            case COLUMN -> {
                foundDropdownTemplate = this.dropdownTemplateRepository.findByGroupAndLevelAndColumnTemplateAndValue(
                        dropdownTemplateDto.getGroup(),
                        dropdownTemplateDto.getLevel(),
                        new ColumnTemplateModel(dropdownTemplateDto.getColumnTemplateId()),
                        dropdownTemplateDto.getValue()
                );

                return this.dropdownTemplateModelToDto(foundDropdownTemplate);
            }
        }

        throw new IllegalArgumentException("Unable to process the dropdown_template");
    }

    @Override
    public List<DropdownTemplateDto> getDropdownTemplatesByTaskTemplateId(Long taskTemplateId) {
        List<DropdownTemplateModel> dropdownTemplateModels = this.dropdownTemplateRepository.findByTaskTemplate(new TaskTemplateModel(taskTemplateId));
        if (dropdownTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return dropdownTemplateModels.stream().map(this::dropdownTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<DropdownTemplateDto> getDropdownTemplatesByFunctionTemplateId(Long functionTemplateId) {
        List<DropdownTemplateModel> dropdownTemplateModels = this.dropdownTemplateRepository.findByFunctionTemplate(new FunctionTemplateModel(functionTemplateId));
        if (dropdownTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return dropdownTemplateModels.stream().map(this::dropdownTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<DropdownTemplateDto> getDropdownTemplatesByColumnTemplateId(Long columnTemplateId) {
        List<DropdownTemplateModel> dropdownTemplateModels = this.dropdownTemplateRepository.findByColumnTemplate(new ColumnTemplateModel(columnTemplateId));
        if (dropdownTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return dropdownTemplateModels.stream().map(this::dropdownTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public DropdownTemplateDto getDropdownTemplateById(Long id) {
        DropdownTemplateModel foundDropdownTemplateModel = this.dropdownTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.DROPDOWN, "id", id, true)
        );

        return this.dropdownTemplateModelToDto(foundDropdownTemplateModel);
    }

    @Override
    public DropdownTemplateDto updateDropdownTemplate(DropdownTemplateDto dropdownTemplateDto) {
        // Step 1: Fetch the existing dropdown_template
        DropdownTemplateModel foundDropdownTemplateModel = this.dropdownTemplateRepository.findById(dropdownTemplateDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.DROPDOWN, "id", dropdownTemplateDto.getId(), true)
        );
        // Step 2: Update the attributes
        foundDropdownTemplateModel.setGroup(dropdownTemplateDto.getGroup().trim().toUpperCase());
        foundDropdownTemplateModel.setValue(dropdownTemplateDto.getValue());
        // Step 3: Save the changes
        foundDropdownTemplateModel = this.dropdownTemplateRepository.save(foundDropdownTemplateModel);

        return this.dropdownTemplateModelToDto(foundDropdownTemplateModel);
    }

    @Override
    public boolean deleteDropdownTemplate(Long id) {
        DropdownTemplateDto foundDropdownTemplateDto = this.getDropdownTemplateById(id);

        // Step 1: Delete all the associations based on the level
        boolean isDeleted = false;

        switch (foundDropdownTemplateDto.getLevel()) {
            case TASK:
                isDeleted = this.taskInstanceServices.deleteTaskInstancesByDropdownTemplateId(id);
                if (!isDeleted) {
                    throw new IllegalArgumentException(
                            "Unable to delete the task_instances for `" + foundDropdownTemplateDto.getGroup() +
                                    "` (id: " + id + ")"
                    );
                }
                break;

            case FUNCTION:
                isDeleted = this.functionInstanceServices.deleteFunctionInstancesByDropdownTemplateId(id);
                if (!isDeleted) {
                    throw new IllegalArgumentException(
                            "Unable to delete the function_instances for `" + foundDropdownTemplateDto.getGroup() +
                                    "` (id: " + id + ")"
                    );
                }
                break;

            case COLUMN:
                isDeleted = this.columnInstanceServices.deleteColumnInstancesByDropdownTemplateId(id);
                if (!isDeleted) {
                    throw new IllegalArgumentException(
                            "Unable to delete the column_instances for `" + foundDropdownTemplateDto.getGroup() +
                                    "` (id: " + id + ")"
                    );
                }
                break;

            default:
                throw new IllegalStateException("Unexpected level: " + foundDropdownTemplateDto.getLevel());
        }

        // Step 2: Delete the dropdown template
        this.dropdownTemplateRepository.deleteById(id);

        return true;  // Return true to indicate successful deletion
    }

    @Override
    public boolean deleteDropdownTemplatesByTaskTemplateId(Long taskTemplateId) {
        // Step 1: Delete associated task instances
        if (!this.taskInstanceServices.deleteTaskInstancesByTaskTemplateId(taskTemplateId)) {
            throw new IllegalArgumentException("Unable to delete task_instances for task_template (id: " + taskTemplateId + ")");
        }

        // Step 2: Delete dropdown_templates by task_template_id
        int deletedCount = this.dropdownTemplateRepository.deleteByTaskTemplateId(taskTemplateId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No dropdown_templates found to delete for task_template (id: " + taskTemplateId + ")");
        }

        return true;
    }


    @Override
    public boolean deleteDropdownTemplatesByFunctionTemplateId(Long functionTemplateId) {
        // Step 1: Delete the associated function_instances
        if (!this.functionInstanceServices.deleteFunctionInstancesByFunctionTemplateId(functionTemplateId)) {
            throw new IllegalArgumentException("Unable to delete function_instances for function_template (id: " + functionTemplateId + ")");
        }

        // Step 2: Delete dropdown_templates by function_template_id
        int deletedCount = this.dropdownTemplateRepository.deleteByFunctionTemplateId(functionTemplateId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No dropdown_templates found to delete for function_template (id: " + functionTemplateId + ")");
        }
        return true;
    }

    @Override
    public boolean deleteDropdownTemplatesByColumnTemplateId(Long columnTemplateId) {
        // Step 1: Delete the associated function_instances
        if (!this.columnInstanceServices.deleteColumnInstancesByColumnTemplateId(columnTemplateId)) {
            throw new IllegalArgumentException("Unable to delete column_instances for column_template (id: " + columnTemplateId + ")");
        }

        // Step 2: Delete dropdown_templates by column_template_id
        int deletedCount = this.dropdownTemplateRepository.deleteByColumnTemplateId(columnTemplateId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No dropdown_templates found to delete for column_template (id: " + columnTemplateId + ")");
        }
        return true;
    }


    private DropdownTemplateDto dropdownTemplateModelToDto(DropdownTemplateModel dropdownTemplateModel) {
        if (dropdownTemplateModel == null) {
            return null;
        }

        DropdownTemplateDto dropdownTemplateDto = this.modelMapper.map(dropdownTemplateModel, DropdownTemplateDto.class);
        if (dropdownTemplateModel.getTaskTemplate() != null) {
            dropdownTemplateDto.setTaskTemplateId(dropdownTemplateModel.getTaskTemplate().getId());
        }
        if (dropdownTemplateModel.getFunctionTemplate() != null) {
            dropdownTemplateDto.setFunctionTemplateId(dropdownTemplateModel.getFunctionTemplate().getId());
        }
        if (dropdownTemplateModel.getColumnTemplate() != null) {
            dropdownTemplateDto.setColumnTemplateId(dropdownTemplateModel.getColumnTemplate().getId());
        }

        return dropdownTemplateDto;
    }

}
