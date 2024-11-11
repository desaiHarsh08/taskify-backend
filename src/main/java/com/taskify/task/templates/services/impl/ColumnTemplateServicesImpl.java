package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.services.ColumnInstanceServices;
import com.taskify.task.templates.dtos.ColumnTemplateDto;
import com.taskify.task.templates.dtos.ColumnVariantTemplateDto;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.dtos.NextFollowUpColumnTemplateDto;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.task.templates.repositories.ColumnMetadataTemplateRepository;
import com.taskify.task.templates.repositories.ColumnTemplateRepository;
import com.taskify.task.templates.repositories.FieldTemplateRepository;
import com.taskify.task.templates.services.ColumnTemplateServices;
import com.taskify.task.templates.services.ColumnVariantTemplateServices;
import com.taskify.task.templates.services.DropdownTemplateServices;
import com.taskify.task.templates.services.NextFollowUpColumnTemplateServices;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnTemplateServicesImpl implements ColumnTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnTemplateRepository columnTemplateRepository;

    @Autowired
    private DropdownTemplateServices dropdownTemplateServices;

    @Autowired
    private FieldTemplateRepository fieldTemplateRepository;

    @Autowired
    private ColumnInstanceServices columnInstanceServices;

    @Autowired
    private ColumnMetadataTemplateRepository columnMetadataTemplateRepository;

    @Autowired
    private ColumnVariantTemplateServices columnVariantTemplateServices;

    @Autowired
    private NextFollowUpColumnTemplateServices nextFollowUpColumnTemplateServices;


    @Override
    public ColumnTemplateDto createColumnTemplate(ColumnTemplateDto columnTemplateDto) {
        // Step 1: Fetch the column_metadata
        ColumnMetadataTemplateModel columnMetadataTemplateModel = this.columnMetadataTemplateRepository.findByTypeAndAcceptMultipleFiles(columnTemplateDto.getColumnMetadataTemplate().getType(), columnTemplateDto.getColumnMetadataTemplate().isAcceptMultipleFiles()).orElse(null);
        if (columnMetadataTemplateModel == null) {
            columnMetadataTemplateModel = this.columnMetadataTemplateRepository.save(columnTemplateDto.getColumnMetadataTemplate());
        }
        // Step 2: Check for column_template already exist
        if (this.columnTemplateRepository.findByNameAndColumnMetadataTemplate(
                columnTemplateDto.getName(), columnMetadataTemplateModel).isPresent()) {
            return null;
        }
        // Step 3: Create new column_template
        ColumnTemplateModel columnTemplateModel = this.modelMapper.map(columnTemplateDto, ColumnTemplateModel.class);
        // Step 4: Set the column_metadata_template
        columnTemplateModel.setColumnMetadataTemplate(columnMetadataTemplateModel);
        // Step 5: Set the field_template
        if (columnTemplateDto.getFieldTemplateId() != null) {
            columnTemplateModel.getFieldTemplates().add(new FieldTemplateModel(columnTemplateDto.getFieldTemplateId()));
        }
        // Step 6: Save the new column_template
        columnTemplateModel = this.columnTemplateRepository.save(columnTemplateModel);
        // Step 7: Set the next follow-up column_template, if exist
        if (!columnTemplateDto.getNextFollowUpColumnTemplates().isEmpty()) {
            for (NextFollowUpColumnTemplateDto nextFollowUpColumnTemplateDto: columnTemplateDto.getNextFollowUpColumnTemplates()) {
                nextFollowUpColumnTemplateDto.setColumnTemplateId(columnTemplateModel.getId());
                this.nextFollowUpColumnTemplateServices.createTemplate(nextFollowUpColumnTemplateDto);
            }
        }
        // Step 8: Create column_variant_template (i.e., for CHECKBOX, TABLE, RADIO), if applicable
        for (ColumnVariantTemplateDto columnVariantTemplateDto: columnTemplateDto.getColumnVariantTemplates()) {
            columnVariantTemplateDto.setColumnTemplateId(columnTemplateModel.getId());
            this.columnVariantTemplateServices.createColumnVariantTemplate(columnVariantTemplateDto);
        }
        // Step 9: Create dropdown_template, if applicable
        for (DropdownTemplateDto dropdownTemplateDto: columnTemplateDto.getDropdownTemplates()) {
            dropdownTemplateDto.setColumnTemplateId(columnTemplateModel.getId());
            this.dropdownTemplateServices.createDropdownTemplate(dropdownTemplateDto);
        }

        return this.columnTemplateModelToDto(columnTemplateModel);
    }

    @Override
    public List<ColumnTemplateDto> getAllColumnTemplates() {
        List<ColumnTemplateModel> columnTemplateModels = this.columnTemplateRepository.findAll();
        if (columnTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return columnTemplateModels.stream().map(this::columnTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<ColumnTemplateDto> getColumnTemplatesByFieldTemplateId(Long fieldTemplateId) {
        List<ColumnTemplateModel> columnTemplateModels = this.columnTemplateRepository.findByFieldTemplates(new FieldTemplateModel(fieldTemplateId));
        System.out.println("columnTemplateModels: " + columnTemplateModels);
        if (columnTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return columnTemplateModels.stream().map(this::columnTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public ColumnTemplateDto getColumnTemplateById(Long id) {
        ColumnTemplateModel foundColumnTemplateModel = this.columnTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", id, true)
        );

        return this.columnTemplateModelToDto(foundColumnTemplateModel);
    }

    @Override
    public ColumnTemplateDto updateColumnTemplate(ColumnTemplateDto columnTemplateDto) {
        // Step 1: Checl for existing column_template
        ColumnTemplateModel foundColumnTemplateModel = this.columnTemplateRepository.findById(columnTemplateDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnTemplateDto.getId(), true)
        );
        // Step 2: Update the attributes
        foundColumnTemplateModel.setName(columnTemplateDto.getName());
        // Step 3: Update the dropdowns_templates
        for (DropdownTemplateDto dropdownTemplateDto: columnTemplateDto.getDropdownTemplates()) {
            this.dropdownTemplateServices.updateDropdownTemplate(dropdownTemplateDto);
        }
        // Step 4: Update the column_variant_templates
        for (ColumnVariantTemplateDto columnVariantTemplateDto: columnTemplateDto.getColumnVariantTemplates()) {
            this.columnVariantTemplateServices.updateColumnVariantTemplate(columnVariantTemplateDto);
        }
        // Save the changes
        foundColumnTemplateModel = this.columnTemplateRepository.save(foundColumnTemplateModel);

        return this.columnTemplateModelToDto(foundColumnTemplateModel);
    }

    @Override
    public boolean unlinkColumnTemplateFromFieldTemplate(Long columnTemplateId, Long fieldTemplateId) {
        // Step 1: Fetch the FieldTemplate
        FieldTemplateModel fieldTemplate = this.fieldTemplateRepository.findById(fieldTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FIELD, "id", fieldTemplateId, true));

        // Step 2: Fetch the ColumnTemplate and check association
        ColumnTemplateModel columnTemplate = this.columnTemplateRepository.findById(columnTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnTemplateId, true));

        if (!fieldTemplate.getColumnTemplates().contains(columnTemplate)) {
            throw new IllegalArgumentException("ColumnTemplate (id: " + columnTemplateId + ") is not associated with FieldTemplate (id: " + fieldTemplateId + ")");
        }

        // Step 3: Unlink the ColumnTemplate from FieldTemplate
        fieldTemplate.getColumnTemplates().remove(columnTemplate);
        columnTemplate.getFieldTemplates().remove(fieldTemplate);

        // Step 4: Save the FieldTemplate after unlinking
        this.fieldTemplateRepository.save(fieldTemplate);

        return true;
    }

    @Override
    public boolean deleteColumnTemplate(Long columnTemplateId) {
        // Step 1: Fetch the ColumnTemplateModel to ensure it exists
        ColumnTemplateModel columnTemplate = this.columnTemplateRepository.findById(columnTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnTemplateId, true));

        // Step 2: Unlink from all associated FieldTemplates
        for (FieldTemplateModel fieldTemplate : columnTemplate.getFieldTemplates()) {
            fieldTemplate.getColumnTemplates().remove(columnTemplate);
            this.fieldTemplateRepository.save(fieldTemplate);  // Save each FieldTemplate after unlinking
        }
        columnTemplate.getFieldTemplates().clear();  // Clear association to prevent errors during deletion

        // Step 3: Remove additional associations, if any (e.g., instances or templates connected to ColumnTemplate)
        if (!this.columnInstanceServices.deleteColumnInstancesByColumnTemplateId(columnTemplateId)) {
            throw new IllegalArgumentException("Unable to delete associated column instances for ColumnTemplate with id: " + columnTemplateId);
        }
        // Step 4: Remove additional associations, if any (e.g., instances or templates connected to ColumnTemplate)
        if (!this.columnVariantTemplateServices.deleteColumnVariantTemplatesByColumnTemplateId(columnTemplateId)) {
            throw new IllegalArgumentException("Unable to delete associated column_variant_templates for ColumnTemplate with id: " + columnTemplateId);
        }
        // Step 5: Delete the next_follow_up_column_templates
        List<NextFollowUpColumnTemplateDto> nextFollowUpColumnTemplateDtos = this.nextFollowUpColumnTemplateServices.getTemplatesByColumnTemplateId(columnTemplateId);
        for (NextFollowUpColumnTemplateDto nextFollowUpColumnTemplateDto: nextFollowUpColumnTemplateDtos) {
            this.nextFollowUpColumnTemplateServices.deleteNextFollowUpColumnTemplateById(nextFollowUpColumnTemplateDto.getId());
        }
        // Step 5: Delete the ColumnTemplateModel from the repository
        this.columnTemplateRepository.delete(columnTemplate);

        return true;
    }

    // This can be used while deleting the field_template
    @Override
    public boolean unlinkColumnTemplatesByFieldTemplateId(Long fieldTemplateId) {
        // Step 1: Fetch the FieldTemplate
        FieldTemplateModel fieldTemplate = this.fieldTemplateRepository.findById(fieldTemplateId).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", fieldTemplateId, true)
        );

        // Step 2: Delete all the associated column_instances
        for (ColumnTemplateModel columnTemplate : fieldTemplate.getColumnTemplates()) {
            // Assuming there's a service to handle deletion of column_instances related to the columnTemplate
            if (!this.columnInstanceServices.deleteColumnInstancesByColumnTemplateId(columnTemplate.getId())) {
                throw new IllegalArgumentException("Unable to delete column_instances while unlinking column_templates (id: " + columnTemplate.getId() + ")");
            }

            // Step 3: Unlink the ColumnTemplate from the FieldTemplate
            fieldTemplate.removeColumnTemplate(columnTemplate);
        }

        // Step 4: Save the updated FieldTemplate to persist changes
        this.fieldTemplateRepository.save(fieldTemplate);

        return true;
    }

    @Override
    @Transactional
    public boolean linkColumnTemplateToFieldTemplate(Long id, Long fieldTemplateId) {
        // Step 1: Fetch the ColumnTemplateModel
        ColumnTemplateModel columnTemplate = columnTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", id, true)
        );

        // Step 2: Fetch the FieldTemplateModel
        FieldTemplateModel fieldTemplate = fieldTemplateRepository.findById(fieldTemplateId).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FIELD, "id", fieldTemplateId, true)
        );

        // Step 3: Add the fieldTemplate to the columnTemplate's list, if not already linked
        if (!columnTemplate.getFieldTemplates().contains(fieldTemplate)) {
            columnTemplate.getFieldTemplates().add(fieldTemplate);
        }

        // Step 4: Add the columnTemplate to the fieldTemplate's list, if not already linked
        if (!fieldTemplate.getColumnTemplates().contains(columnTemplate)) {
            fieldTemplate.getColumnTemplates().add(columnTemplate);
        }

        // Step 5: Save the changes to both entities
        columnTemplateRepository.save(columnTemplate);
        fieldTemplateRepository.save(fieldTemplate);

        return true;
    }


    private ColumnTemplateDto columnTemplateModelToDto(ColumnTemplateModel columnTemplateModel) {
        if (columnTemplateModel == null) {
            return null;
        }
        ColumnTemplateDto columnTemplateDto = this.modelMapper.map(columnTemplateModel, ColumnTemplateDto.class);

        columnTemplateDto.setDropdownTemplates(this.dropdownTemplateServices.getDropdownTemplatesByColumnTemplateId(columnTemplateModel.getId()));
        columnTemplateDto.setColumnVariantTemplates(this.columnVariantTemplateServices.getColumnVariantTemplatesByColumnTemplateId(columnTemplateModel.getId()));
        if (columnTemplateModel.getColumnMetadataTemplate()  != null) {
            columnTemplateDto.setColumnMetadataTemplate(this.columnMetadataTemplateRepository.findById(columnTemplateModel.getColumnMetadataTemplate().getId()).orElseThrow(
                    () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnTemplateModel.getColumnMetadataTemplate().getId(), true)
            ));
        }
        columnTemplateDto.setNextFollowUpColumnTemplates(this.nextFollowUpColumnTemplateServices.getTemplatesByColumnTemplateId(columnTemplateModel.getId()));
        System.out.println("in col_temp services impl model to dto");
        return columnTemplateDto;
    }

    // TODO
    @Override
    public boolean deleteColumnTemplatesByColumnMetadataTemplateId(Long columnMetadataTemplateId) {
        return false;
    }
}
