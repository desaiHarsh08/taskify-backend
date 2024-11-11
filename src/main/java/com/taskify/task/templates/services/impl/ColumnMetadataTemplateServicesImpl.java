package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ColumnType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.repositories.ColumnMetadataTemplateRepository;
import com.taskify.task.templates.services.ColumnMetadataTemplateServices;
import com.taskify.task.templates.services.ColumnTemplateServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnMetadataTemplateServicesImpl implements ColumnMetadataTemplateServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnMetadataTemplateRepository columnMetadataTemplateRepository;

    @Autowired
    private ColumnTemplateServices columnTemplateServices;

    @Override
    public ColumnMetadataTemplateModel createColumnMetadataTemplate(ColumnMetadataTemplateModel columnMetadataTemplateModel) {
        // Step 1: Check for column_metadata_template already exist
        ColumnMetadataTemplateModel foundColumnMetadataTemplateModel = this.columnMetadataTemplateRepository.findByTypeAndAcceptMultipleFiles(
                columnMetadataTemplateModel.getType(), columnMetadataTemplateModel.isAcceptMultipleFiles()
        ).orElse(null);
        if (foundColumnMetadataTemplateModel != null) {
            return null;
        }
        // Create the new column_metadata_template
        columnMetadataTemplateModel = this.columnMetadataTemplateRepository.save(columnMetadataTemplateModel);

        return columnMetadataTemplateModel;
    }

    @Override
    public List<ColumnMetadataTemplateModel> getAllColumnMetadataTemplates() {
        List<ColumnMetadataTemplateModel> columnMetadataTemplateModels = this.columnMetadataTemplateRepository.findAll();
        if (columnMetadataTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return columnMetadataTemplateModels;
    }

    @Override
    public List<ColumnMetadataTemplateModel> getColumnMetadataTemplatesByType(ColumnType type) {
        List<ColumnMetadataTemplateModel> columnMetadataTemplateModels = this.columnMetadataTemplateRepository.findByType(type);
        if (columnMetadataTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return columnMetadataTemplateModels;
    }

    @Override
    public ColumnMetadataTemplateModel getColumnMetadataTemplateByTypeAndAcceptingMultipleFiles(ColumnType type, boolean acceptimgMultipleFiles) {
        return this.columnMetadataTemplateRepository.findByTypeAndAcceptMultipleFiles(type, acceptimgMultipleFiles).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_METADATA, "type & multplefiles", type.name() + acceptimgMultipleFiles, true)
        );
    }

    @Override
    public ColumnMetadataTemplateModel getColumnMetadataTemplateById(Long id) {
        return this.columnMetadataTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_METADATA, "id", id, true)
        );
    }

    @Override
    public ColumnMetadataTemplateModel updateColumnMetadataTemplate(ColumnMetadataTemplateModel columnMetadataTemplateModel) {
        // Step 1: Check for column_metadata_template
        ColumnMetadataTemplateModel foundColumnMetadataTemplateModel = this.getColumnMetadataTemplateById(columnMetadataTemplateModel.getId());
        // Step 2: Update the attributes
        foundColumnMetadataTemplateModel.setType(columnMetadataTemplateModel.getType());
        foundColumnMetadataTemplateModel.setAcceptMultipleFiles(columnMetadataTemplateModel.isAcceptMultipleFiles());
        // Step 3: Save the changes
        foundColumnMetadataTemplateModel = this.columnMetadataTemplateRepository.save(foundColumnMetadataTemplateModel);

        return foundColumnMetadataTemplateModel;
    }

    @Override
    public boolean deleteColumnMetadataTemplate(Long id) {
        // Step 1: Check for column_metadata_template
        ColumnMetadataTemplateModel foundColumnMetadataTemplateModel = this.getColumnMetadataTemplateById(id);
        // Step 2: Delete all the column_templates
        this.columnTemplateServices.deleteColumnTemplate(id);
        // Step 3: Delete the column_metadata
        this.columnMetadataTemplateRepository.deleteById(id);
        return false;
    }
}
