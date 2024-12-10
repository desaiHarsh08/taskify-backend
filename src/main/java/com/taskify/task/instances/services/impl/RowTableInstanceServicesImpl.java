package com.taskify.task.instances.services.impl;

import com.taskify.task.instances.dtos.ColTableInstanceDto;
import com.taskify.task.instances.dtos.RowTableInstanceDto;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.RowTableInstanceModel;
import com.taskify.task.instances.repositories.ColTableInstanceRepository;
import com.taskify.task.instances.repositories.ColumnInstanceRepository;
import com.taskify.task.instances.repositories.RowTableInstanceRepository;
import com.taskify.task.instances.services.ColTableInstanceServices;
import com.taskify.task.instances.services.RowTableInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RowTableInstanceServicesImpl implements RowTableInstanceServices {

    @Autowired
    private RowTableInstanceRepository rowTableInstanceRepository;

    @Autowired
    private ColumnInstanceRepository columnInstanceRepository;

    @Autowired
    private ColTableInstanceServices colTableInstanceServices;

    @Override
    public RowTableInstanceDto createRowTableInstance(RowTableInstanceDto rowTableInstanceDto) {
        RowTableInstanceModel rowTableInstanceModel = dtoToModel(rowTableInstanceDto);
        rowTableInstanceModel.setCreatedAt(LocalDateTime.now());
        rowTableInstanceModel.setUpdatedAt(LocalDateTime.now());
        rowTableInstanceModel = this.rowTableInstanceRepository.save(rowTableInstanceModel);
        for (ColTableInstanceDto colTableInstanceDto: rowTableInstanceDto.getColTableInstances()) {
            colTableInstanceDto.setRowTableInstanceId(rowTableInstanceModel.getId());
            this.colTableInstanceServices.createColTableInstance(colTableInstanceDto);
        }
        return modelToDto(rowTableInstanceModel);
    }

    @Override
    public RowTableInstanceDto updateRowTableInstance(Long id, RowTableInstanceDto rowTableInstanceDto) {
        if (id == null) {
            this.createRowTableInstance(rowTableInstanceDto);
        }
        RowTableInstanceModel rowTableInstanceModel = rowTableInstanceRepository.findById(id).orElseThrow(() -> new RuntimeException("RowTableInstance not found"));
        rowTableInstanceModel.setUpdatedAt(LocalDateTime.now());
        for (ColTableInstanceDto colTableInstanceDto: rowTableInstanceDto.getColTableInstances()) {
            colTableInstanceDto.setRowTableInstanceId(id);
            this.colTableInstanceServices.updateColTableInstance(colTableInstanceDto.getId(), colTableInstanceDto);
        }
        return modelToDto(rowTableInstanceRepository.save(rowTableInstanceModel));
    }

    @Override
    public void deleteRowTableInstance(Long id) {
        rowTableInstanceRepository.deleteById(id);
    }

    @Override
    public RowTableInstanceDto getRowTableInstanceById(Long id) {
        return rowTableInstanceRepository.findById(id)
                .map(this::modelToDto)
                .orElseThrow(() -> new RuntimeException("RowTableInstance not found"));
    }

    @Override
    public List<RowTableInstanceDto> getRowTableInstancesByColumnInstanceId(Long columnInstanceId) {
        ColumnInstanceModel columnInstanceModel = columnInstanceRepository.findById(columnInstanceId)
                .orElseThrow(() -> new RuntimeException("ColumnInstance not found"));
        List<RowTableInstanceModel> instances = rowTableInstanceRepository.findByColumnInstance(columnInstanceModel);
        return instances.stream().map(this::modelToDto).collect(Collectors.toList());
    }

    private RowTableInstanceModel dtoToModel(RowTableInstanceDto dto) {
        RowTableInstanceModel model = new RowTableInstanceModel();
        model.setId(dto.getId());
        model.setCreatedAt(dto.getCreatedAt());
        model.setUpdatedAt(dto.getUpdatedAt());
        // Add column instance reference
        return model;
    }

    private RowTableInstanceDto modelToDto(RowTableInstanceModel model) {
        RowTableInstanceDto dto = new RowTableInstanceDto();
        dto.setId(model.getId());
        dto.setColumnInstanceId(model.getColumnInstance().getId());
        dto.setColTableInstances(this.colTableInstanceServices.getColTableInstancesByRowTableById(model.getId()));
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());
        // Add column instance reference
        return dto;
    }

}
