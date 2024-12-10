package com.taskify.task.instances.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.instances.dtos.ColTableInstanceDto;
import com.taskify.task.instances.models.ColTableInstanceModel;
import com.taskify.task.instances.models.ColumnVariantInstanceModel;
import com.taskify.task.instances.models.RowTableInstanceModel;
import com.taskify.task.instances.repositories.ColTableInstanceRepository;
import com.taskify.task.instances.services.ColTableInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColTableInstanceServicesImpl implements ColTableInstanceServices {

    private final ColTableInstanceRepository colTableInstanceRepository;

    @Autowired
    public ColTableInstanceServicesImpl(ColTableInstanceRepository colTableInstanceRepository) {
        this.colTableInstanceRepository = colTableInstanceRepository;
    }

    @Override
    public ColTableInstanceDto createColTableInstance(ColTableInstanceDto colTableInstanceDto) {
        ColTableInstanceModel model = mapToModel(colTableInstanceDto);
        model.setRowTableInstance(new RowTableInstanceModel(colTableInstanceDto.getRowTableInstanceId()));
        model.setCreatedAt(LocalDateTime.now());
        model.setUpdatedAt(LocalDateTime.now());
        ColTableInstanceModel savedModel = colTableInstanceRepository.save(model);
        return mapToDto(savedModel);
    }

    @Override
    public ColTableInstanceDto updateColTableInstance(Long id, ColTableInstanceDto colTableInstanceDto) {
        if (id == null) {
            return this.createColTableInstance(colTableInstanceDto);
        }
        ColTableInstanceModel colTableInstanceModel = colTableInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COL_TABLE, "id", id, false)
        );
        colTableInstanceModel.setTextValue(colTableInstanceDto.getTextValue());
        colTableInstanceModel.setBooleanValue(colTableInstanceDto.isBooleanValue());
        colTableInstanceModel.setDateValue(colTableInstanceDto.getDateValue());
        colTableInstanceModel.setNumberValue(colTableInstanceDto.getNumberValue());
        colTableInstanceModel.setUpdatedAt(LocalDateTime.now());
        // Assuming ColumnVariantInstance is set similarly.
        ColTableInstanceModel updatedModel = colTableInstanceRepository.save(colTableInstanceModel);

        return mapToDto(updatedModel);
    }

    @Override
    public void deleteColTableInstance(Long id) {
        colTableInstanceRepository.deleteById(id);
    }

    @Override
    public ColTableInstanceDto getColTableInstanceById(Long id) {
        ColTableInstanceModel model = colTableInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ColTableInstance with id " + id + " not found."));
        return mapToDto(model);
    }

    @Override
    public List<ColTableInstanceDto> getColTableInstancesByRowTableById(Long rowTableInstanceId) {
        List<ColTableInstanceModel> models = colTableInstanceRepository.findByRowTableInstanceId(new RowTableInstanceModel(rowTableInstanceId));
        return models.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ColTableInstanceModel mapToModel(ColTableInstanceDto dto) {
        ColTableInstanceModel model = new ColTableInstanceModel();
        model.setTextValue(dto.getTextValue());
        model.setBooleanValue(dto.isBooleanValue());
        model.setDateValue(dto.getDateValue());
        model.setNumberValue(dto.getNumberValue());
        // Set the ColumnVariantInstance based on DTO data if available
        return model;
    }

    private ColTableInstanceDto mapToDto(ColTableInstanceModel model) {
        ColTableInstanceDto dto = new ColTableInstanceDto();
        dto.setRowTableInstanceId(model.getRowTableInstance().getId());
        dto.setTextValue(model.getTextValue());
        dto.setBooleanValue(model.isBooleanValue());
        dto.setDateValue(model.getDateValue());
        dto.setNumberValue(model.getNumberValue());
        // Populate any other necessary fields
        return dto;
    }
}