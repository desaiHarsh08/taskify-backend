package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ColumnType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.repositories.ColumnVariantInstanceRepository;
import com.taskify.task.instances.services.ColumnVariantInstanceServices;
import com.taskify.task.templates.dtos.ColumnVariantTemplateDto;
import com.taskify.task.templates.dtos.NextFollowUpColumnTemplateDto;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.repositories.ColumnTemplateRepository;
import com.taskify.task.templates.repositories.ColumnVariantTemplateRepository;
import com.taskify.task.templates.repositories.NextFollowUpColumnTemplateRepository;
import com.taskify.task.templates.services.ColumnVariantTemplateServices;
import com.taskify.task.templates.services.NextFollowUpColumnTemplateServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnVariantTemplateServicesImpl implements ColumnVariantTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnVariantTemplateRepository columnVariantTemplateRepository;

    @Autowired
    private ColumnTemplateRepository columnTemplateRepository;

    @Autowired
    private ColumnVariantInstanceServices columnVariantInstanceServices;

    @Autowired
    private NextFollowUpColumnTemplateServices nextFollowUpColumnTemplateServices;

    @Override
    public ColumnVariantTemplateDto createColumnVariantTemplate(ColumnVariantTemplateDto columnVariantTemplateDto) {
        // Step 1: Map DTO to model
        ColumnVariantTemplateModel columnVariantTemplate = this.modelMapper.map(columnVariantTemplateDto, ColumnVariantTemplateModel.class);
        columnVariantTemplate.setColumnTemplate(new ColumnTemplateModel(columnVariantTemplateDto.getColumnTemplateId()));
        // Step 2: Save entity and return DTO
        columnVariantTemplate = columnVariantTemplateRepository.save(columnVariantTemplate);
        // Step 3: Create the next_follow_up_column_template
        for (NextFollowUpColumnTemplateDto nextFollowUpColumnTemplateDto: columnVariantTemplateDto.getNextFollowUpColumnTemplates()) {
            nextFollowUpColumnTemplateDto.setColumnVariantTemplateId(columnVariantTemplate.getId());
            this.nextFollowUpColumnTemplateServices.createTemplate(nextFollowUpColumnTemplateDto);
        }

        return this.columnVariantTemplateModelToDto(columnVariantTemplate);
    }

    @Override
    public List<ColumnVariantTemplateDto> getAllColumnVariantTemplates() {
        List<ColumnVariantTemplateModel> columnVariants = columnVariantTemplateRepository.findAll();
        if (columnVariants.isEmpty()) {
            return new ArrayList<>();
        }

        return columnVariants.stream().map(this::columnVariantTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<ColumnVariantTemplateDto> getColumnVariantTemplatesByColumnTemplateId(Long columnTemplateId) {
        List<ColumnVariantTemplateModel> columnVariants = columnVariantTemplateRepository.findByColumnTemplate(new ColumnTemplateModel(columnTemplateId));
        if (columnVariants.isEmpty()) {
            return new ArrayList<>();
        }

        return columnVariants.stream().map(this::columnVariantTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<ColumnVariantTemplateDto> getColumnVariantTemplatesByValueType(ColumnType valueType) {
        List<ColumnVariantTemplateModel> columnVariants = columnVariantTemplateRepository.findByValueType(valueType);
        if (columnVariants.isEmpty()) {
            return new ArrayList<>();
        }

        return columnVariants.stream().map(this::columnVariantTemplateModelToDto).collect(Collectors.toList());
    }

    @Override
    public ColumnVariantTemplateDto getColumnVariantTemplateById(Long id) {
        ColumnVariantTemplateModel columnVariant = columnVariantTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_VARIANT, "id", id, true)
        );

        return this.columnVariantTemplateModelToDto(columnVariant);
    }

    @Override
    public ColumnVariantTemplateDto updateColumnVariantTemplate(ColumnVariantTemplateDto columnVariantTemplateDto) {
        // Step 1: Check for the column_variant_template exist
        ColumnVariantTemplateModel existingVariant = columnVariantTemplateRepository.findById(columnVariantTemplateDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_VARIANT, "id", columnVariantTemplateDto.getId(), true)
        );
        // Step 2: Update the attributes
        existingVariant.setName(columnVariantTemplateDto.getName());
        existingVariant.setValueType(columnVariantTemplateDto.getValueType());
        // Step 3: Save the changes
        ColumnVariantTemplateModel updatedVariant = columnVariantTemplateRepository.save(existingVariant);

        return this.columnVariantTemplateModelToDto(updatedVariant);
    }

    @Override
    public boolean deleteColumnVariantTemplate(Long id) {
        ColumnVariantTemplateModel columnVariant = columnVariantTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_VARIANT, "id", id, true)
        );

        // Delete any associated columnVariantInstances
        this.columnVariantInstanceServices.deleteColumnVariantInstancesByColumnVariantTemplateId(id);

        // Delete the column variant template
        columnVariantTemplateRepository.delete(columnVariant);
        return true;
    }

    @Override
    public boolean deleteColumnVariantTemplatesByColumnTemplateId(Long columnTemplateId) {
        List<ColumnVariantTemplateModel> columnVariants = columnVariantTemplateRepository.findByColumnTemplate(new ColumnTemplateModel(columnTemplateId));

        for (ColumnVariantTemplateModel columnVariant : columnVariants) {
            // Delete associated instances before deleting the template itself
            this.columnVariantInstanceServices.deleteColumnVariantInstancesByColumnVariantTemplateId(columnVariant.getId());
            columnVariantTemplateRepository.delete(columnVariant);
        }

        return true;
    }

    private ColumnVariantTemplateDto columnVariantTemplateModelToDto(ColumnVariantTemplateModel columnVariantTemplateModel) {
        if (columnVariantTemplateModel == null) {
            return null;
        }
        ColumnVariantTemplateDto columnVariantTemplateDto = this.modelMapper.map(columnVariantTemplateModel, ColumnVariantTemplateDto.class);
        columnVariantTemplateDto.setColumnTemplateId(columnVariantTemplateModel.getColumnTemplate().getId());
        columnVariantTemplateDto.setNextFollowUpColumnTemplates(new ArrayList<>());
        columnVariantTemplateDto.setNextFollowUpColumnTemplates(this.nextFollowUpColumnTemplateServices.getTemplatesByColumnVariantTemplateId(columnVariantTemplateModel.getId()));

        return columnVariantTemplateDto;
    }

}
