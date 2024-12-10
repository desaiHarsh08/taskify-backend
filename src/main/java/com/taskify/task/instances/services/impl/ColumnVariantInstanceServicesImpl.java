package com.taskify.task.instances.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColTableInstanceDto;
import com.taskify.task.instances.dtos.ColumnVariantInstanceDto;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.ColumnVariantInstanceModel;
import com.taskify.task.instances.repositories.ColTableInstanceRepository;
import com.taskify.task.instances.repositories.ColumnInstanceRepository;
import com.taskify.task.instances.repositories.ColumnVariantInstanceRepository;
import com.taskify.task.instances.services.ColTableInstanceServices;
import com.taskify.task.instances.services.ColumnVariantInstanceServices;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.repositories.ColumnVariantTemplateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnVariantInstanceServicesImpl implements ColumnVariantInstanceServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnVariantInstanceRepository columnVariantInstanceRepository;

    @Autowired
    private ColumnVariantTemplateRepository columnVariantTemplateRepository;

    @Autowired
    private ColumnInstanceRepository columnInstanceRepository;

    @Autowired
    private ColTableInstanceServices colTableInstanceServices;

    @Override
    public ColumnVariantInstanceDto createColumnVariantInstance(ColumnVariantInstanceDto columnVariantInstanceDto) {
        // Step 1: Create the new column_variant_instance
        ColumnVariantInstanceModel newColumnVariantInstanceModel = this.modelMapper.map(columnVariantInstanceDto, ColumnVariantInstanceModel.class);
        // Step 2: Set the column_variant_template
        newColumnVariantInstanceModel.setColumnVariantTemplate(new ColumnVariantTemplateModel(columnVariantInstanceDto.getColumnVariantTemplateId()));
        // Step 3: Set the column_instance
        newColumnVariantInstanceModel.setColumnInstance(new ColumnInstanceModel(columnVariantInstanceDto.getColumnInstanceId()));
        // Step 4: Set the values
        newColumnVariantInstanceModel.setDateValue(columnVariantInstanceDto.getDateValue());
        newColumnVariantInstanceModel.setBooleanValue(columnVariantInstanceDto.getBooleanValue());
        newColumnVariantInstanceModel.setTextValue(columnVariantInstanceDto.getTextValue());
        newColumnVariantInstanceModel.setNumberValue(columnVariantInstanceDto.getNumberValue());
        newColumnVariantInstanceModel.setCreatedAt(LocalDateTime.now());
        newColumnVariantInstanceModel.setUpdatedAt(LocalDateTime.now());
        // Step 5: Save the new column_variant_instance
        newColumnVariantInstanceModel = this.columnVariantInstanceRepository.save(newColumnVariantInstanceModel);


        return this.columnVariantInstanceModelToDto(newColumnVariantInstanceModel);
    }

    @Override
    public PageResponse<ColumnVariantInstanceDto> getAllColumnVariantInstances(int pageNumber, Integer pageSize) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ColumnVariantInstanceModel> pageColumnVariantInstance = this.columnVariantInstanceRepository.findAll(pageable);
        List<ColumnVariantInstanceModel> columnVariantInstanceModels = pageColumnVariantInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageColumnVariantInstance.getTotalPages(),
                pageColumnVariantInstance.getTotalElements(),
                columnVariantInstanceModels.stream().map(this::columnVariantInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ColumnVariantInstanceDto> getColumnVariantInstancesByColumnVariantTemplateById(int pageNumber, Integer pageSize, Long columnVariantTemplateId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ColumnVariantInstanceModel> pageColumnVariantInstance = this.columnVariantInstanceRepository.findByColumnVariantTemplate(pageable, new ColumnVariantTemplateModel(columnVariantTemplateId));
        List<ColumnVariantInstanceModel> columnVariantInstanceModels = pageColumnVariantInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageColumnVariantInstance.getTotalPages(),
                pageColumnVariantInstance.getTotalElements(),
                columnVariantInstanceModels.stream().map(this::columnVariantInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public ColumnVariantInstanceDto getColumnVariantInstanceById(Long id) {
        ColumnVariantInstanceModel foundColumnVariantInstanceModel = this.columnVariantInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", id, false)
        );

        return this.columnVariantInstanceModelToDto(foundColumnVariantInstanceModel);
    }

    @Override
    public ColumnVariantInstanceDto updateColumnVariantInstance(ColumnVariantInstanceDto columnVariantInstanceDto) {
        // Step 1: Check for column_variant_instance does exist
        ColumnVariantInstanceModel foundColumnVariantInstanceModel = this.columnVariantInstanceRepository.findById(columnVariantInstanceDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnVariantInstanceDto.getId(), false)
        );
        // Step 2: Update the attributes
        foundColumnVariantInstanceModel.setDateValue(columnVariantInstanceDto.getDateValue());
        foundColumnVariantInstanceModel.setNumberValue(columnVariantInstanceDto.getNumberValue());
        foundColumnVariantInstanceModel.setTextValue(columnVariantInstanceDto.getTextValue());
        foundColumnVariantInstanceModel.setBooleanValue(columnVariantInstanceDto.getBooleanValue());
        foundColumnVariantInstanceModel.setUpdatedAt(LocalDateTime.now());

        // Step 3: Save the changes
        foundColumnVariantInstanceModel = this.columnVariantInstanceRepository.save(foundColumnVariantInstanceModel);


        return this.columnVariantInstanceModelToDto(foundColumnVariantInstanceModel);
    }

    @Override
    public boolean deleteColumnVariantInstance(Long id) {
        this.getColumnVariantInstanceById(id);
        this.columnInstanceRepository.deleteById(id);
        return false;
    }

    @Override
    public boolean deleteColumnVariantInstancesByColumnVariantTemplateId(Long id) {
        int deleteCount = this.columnVariantInstanceRepository.deleteByColumnVariantTemplateId(id);
        return deleteCount > 0;
    }

    private ColumnVariantInstanceDto columnVariantInstanceModelToDto(ColumnVariantInstanceModel columnVariantInstanceModel) {
        if (columnVariantInstanceModel == null) {
            return null;
        }
        ColumnVariantInstanceDto columnVariantInstanceDto = this.modelMapper.map(columnVariantInstanceModel, ColumnVariantInstanceDto.class);
        columnVariantInstanceDto.setColumnInstanceId(columnVariantInstanceModel.getColumnInstance().getId());
        columnVariantInstanceDto.setColumnVariantTemplateId(columnVariantInstanceModel.getColumnVariantTemplate().getId());

        return columnVariantInstanceDto;
    }
}
