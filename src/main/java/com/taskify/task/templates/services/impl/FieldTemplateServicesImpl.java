package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.services.FieldInstanceServices;
import com.taskify.task.templates.dtos.ColumnTemplateDto;
import com.taskify.task.templates.dtos.FieldTemplateDto;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.repositories.FieldTemplateRepository;
import com.taskify.task.templates.repositories.FunctionTemplateRepository;
import com.taskify.task.templates.services.ColumnTemplateServices;
import com.taskify.task.templates.services.FieldTemplateServices;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldTemplateServicesImpl implements FieldTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FieldTemplateRepository fieldTemplateRepository;

    @Autowired
    private FunctionTemplateRepository functionTemplateRepository;

    @Autowired
    private ColumnTemplateServices columnTemplateServices;

    @Autowired
    private FieldInstanceServices fieldInstanceServices;

    @Override
    public FieldTemplateDto createFieldTemplate(FieldTemplateDto fieldTemplateDto) {
        // Step 1: Create the field_template
        FieldTemplateModel fieldTemplateModel = modelMapper.map(fieldTemplateDto, FieldTemplateModel.class);
        // Step 2: Create the column_templates
        List<ColumnTemplateModel> columnTemplateModels = new ArrayList<>();
        for (ColumnTemplateDto columnTemplateDto: fieldTemplateDto.getColumnTemplates()) {
            columnTemplateDto = this.columnTemplateServices.createColumnTemplate(columnTemplateDto);
            columnTemplateModels.add(new ColumnTemplateModel(columnTemplateDto.getId()));
        }
        // Step 3: Set the column_templates
        fieldTemplateModel.setColumnTemplates(columnTemplateModels);
        FieldTemplateModel savedFieldTemplate = fieldTemplateRepository.save(fieldTemplateModel);

        return modelMapper.map(savedFieldTemplate, FieldTemplateDto.class);
    }

    @Override
    public List<FieldTemplateDto> getAllFieldTemplates() {
        List<FieldTemplateModel> fieldTemplateModels = this.fieldTemplateRepository.findAll();
        if (fieldTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return fieldTemplateModels.stream().map(field -> modelMapper.map(field, FieldTemplateDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<FieldTemplateDto> getFieldTemplatesByFunctionTemplateId(Long functionTemplateId) {
        List<FieldTemplateModel> fieldTemplateModels = this.fieldTemplateRepository.findByFunctionTemplates(new FunctionTemplateModel(functionTemplateId));
        if (fieldTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return fieldTemplateModels.stream().map(field -> modelMapper.map(field, FieldTemplateDto.class)).collect(Collectors.toList());
    }

    @Override
    public FieldTemplateDto getFieldTemplateById(Long id) {
        FieldTemplateModel foundFieldTemplate = fieldTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", id, true)
        );

        return this.fieldTemplateModelToDto(foundFieldTemplate);
    }

    @Override
    public FieldTemplateDto updateFieldTemplate(FieldTemplateDto fieldTemplateDto) {
        // Step 1: Check for the field_template does exist
        FieldTemplateModel foundFieldTemplate = fieldTemplateRepository.findById(fieldTemplateDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", fieldTemplateDto.getId(), true)
        );
        // Step 2: Update the attributes
        foundFieldTemplate.setTitle(foundFieldTemplate.getTitle());
        foundFieldTemplate.setDescription(foundFieldTemplate.getDescription());
        // Step 3: Updated the column_templates
        for (ColumnTemplateDto columnTemplateDto: fieldTemplateDto.getColumnTemplates()) {
            this.columnTemplateServices.updateColumnTemplate(columnTemplateDto);
        }
        // Save the changes
        FieldTemplateModel updatedFieldTemplate = this.fieldTemplateRepository.save(foundFieldTemplate);

        return this.fieldTemplateModelToDto(updatedFieldTemplate);
    }

    @Override
    public boolean unlinkFieldTemplateFromFunctionTemplate(Long id, Long functionTemplateId) {
        // Step 1: Check for function_template
        FunctionTemplateModel functionTemplate = functionTemplateRepository.findById(functionTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", functionTemplateId, true));
        // Step 2: Delete all the associated field_instances
        if (!this.fieldInstanceServices.deleteFieldInstancesByFieldTemplateId(id)) {
            throw new IllegalArgumentException("Unable to delete the field_instances while in the process for unlinking all field_templates (having id: " + id + ")");
        }
        // Step 3: Unlink the associated field_template from function_template
        functionTemplate.getFieldTemplates().remove(new FieldTemplateModel(id));
        // Step 4: Save the function_template
        functionTemplateRepository.save(functionTemplate);

        return true;
    }

    @Override
    public boolean deleteFieldTemplate(Long id) {
        // Step 1: Check if the field_template exists
        FieldTemplateModel foundFieldTemplateModel = this.fieldTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", id, true)
        );
        // Step 2: Unlink from related FunctionTemplateModel records
        for (FunctionTemplateModel functionTemplate : foundFieldTemplateModel.getFunctionTemplates()) {
            functionTemplate.removeFieldTemplate(foundFieldTemplateModel);
            functionTemplateRepository.save(functionTemplate);  // Save changes to each FunctionTemplateModel
        }
        // Clear the functionTemplates list in fieldTemplate to prevent errors during deletion
        foundFieldTemplateModel.getFunctionTemplates().clear();

        // Step 3: Remove associations with ColumnTemplateModel
        for (ColumnTemplateModel columnTemplateModel: foundFieldTemplateModel.getColumnTemplates()) {
            foundFieldTemplateModel.removeColumnTemplate(columnTemplateModel);
            this.fieldTemplateRepository.save(foundFieldTemplateModel);
        }
        // Clear the columnTemplates list in fieldTemplate to prevent errors during deletion
        foundFieldTemplateModel.getColumnTemplates().clear();

        // Step 5: Delete the FieldTemplateModel from the repository
        fieldTemplateRepository.deleteById(id);

        return true;
    }


    @Override
    public boolean unlinkFieldTemplatesByFunctionTemplateId(Long functionTemplateId) {
        // Step 1: Fetch the function_template
        FunctionTemplateModel functionTemplateModel = this.functionTemplateRepository.findById(functionTemplateId).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", functionTemplateId, true)
        );
        for (FieldTemplateModel fieldTemplateModel: functionTemplateModel.getFieldTemplates()) {
            // Step 2: Delete all the associated field_instances
            if (!this.fieldInstanceServices.deleteFieldInstancesByFieldTemplateId(fieldTemplateModel.getId())) {
                throw new IllegalArgumentException("Unable to delete the field_instances while in the process for unlinking all field_templates (having id: " + functionTemplateModel.getId() + ")");
            }
            // Step 3: Unlink the FieldTemplate by ID
            functionTemplateModel.removeFieldTemplate(fieldTemplateModel);
        }
        // Step 4: Save the changes to FunctionTemplate
        this.functionTemplateRepository.save(functionTemplateModel);

        return true;
    }

    @Override
    @Transactional
    public boolean linkFieldTemplateToFunctionTemplate(Long id, Long functionTemplateId) {
        // Step 1: Fetch the FieldTemplateModel
        FieldTemplateModel fieldTemplate = fieldTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", id, true)
        );

        // Step 2: Fetch the FunctionTemplateModel
        FunctionTemplateModel functionTemplate = functionTemplateRepository.findById(functionTemplateId).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", functionTemplateId, true)
        );

        // Step 3: Add the functionTemplate to the fieldTemplate's list, if not already linked
        if (!fieldTemplate.getFunctionTemplates().contains(functionTemplate)) {
            fieldTemplate.getFunctionTemplates().add(functionTemplate);
        }

        // Step 4: Add the fieldTemplate to the functionTemplate's list, if not already linked
        if (!functionTemplate.getFieldTemplates().contains(fieldTemplate)) {
            functionTemplate.getFieldTemplates().add(fieldTemplate);
        }

        // Step 5: Save the changes to both entities
        fieldTemplateRepository.save(fieldTemplate);
        functionTemplateRepository.save(functionTemplate);

        return true;
    }


    private FieldTemplateDto fieldTemplateModelToDto(FieldTemplateModel fieldTemplateModel) {
        if (fieldTemplateModel == null) {
            return null;
        }
        FieldTemplateDto fieldTemplateDto = this.modelMapper.map(fieldTemplateModel, FieldTemplateDto.class);
        fieldTemplateDto.setColumnTemplates(new ArrayList<>());
        fieldTemplateDto.setColumnTemplates(this.columnTemplateServices.getColumnTemplatesByFieldTemplateId(fieldTemplateModel.getId()));
        for (ColumnTemplateDto columnTemplateDto: fieldTemplateDto.getColumnTemplates()) {
            columnTemplateDto.setFieldTemplateId(fieldTemplateDto.getId());
        }

        return fieldTemplateDto;
    }

}
