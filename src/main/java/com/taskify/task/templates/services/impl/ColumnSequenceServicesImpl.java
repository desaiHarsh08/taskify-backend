package com.taskify.task.templates.services.impl;

import com.taskify.task.templates.dtos.ColumnSequenceDto;
import com.taskify.task.templates.models.ColumnSequenceModel;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.task.templates.repositories.ColumnSequenceRepository;
import com.taskify.task.templates.repositories.ColumnTemplateRepository;
import com.taskify.task.templates.repositories.FieldTemplateRepository;
import com.taskify.task.templates.services.ColumnSequenceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnSequenceServicesImpl implements ColumnSequenceServices {

    @Autowired
    private ColumnSequenceRepository columnSequenceRepository;

    @Autowired
    private FieldTemplateRepository fieldTemplateRepository;

    @Autowired
    private ColumnTemplateRepository columnTemplateRepository;

    @Override
    public ColumnSequenceDto createSequence(ColumnSequenceDto columnSequenceDto) {
        FieldTemplateModel fieldTemplate = fieldTemplateRepository.findById(columnSequenceDto.getFieldTemplateId())
                .orElseThrow(() -> new RuntimeException("FieldTemplate not found"));

        ColumnTemplateModel columnTemplate = columnTemplateRepository.findById(columnSequenceDto.getColumnTemplateId())
                .orElseThrow(() -> new RuntimeException("ColumnTemplate not found"));

        ColumnSequenceModel columnSequence = new ColumnSequenceModel();
        columnSequence.setFieldTemplate(fieldTemplate);
        columnSequence.setColumnTemplate(columnTemplate);
        columnSequence.setSequence(columnSequenceDto.getSequence());

        ColumnSequenceModel savedSequence = columnSequenceRepository.save(columnSequence);
        return mapToDto(savedSequence);
    }

    @Override
    public List<ColumnSequenceDto> getSequencesByFieldTemplate(Long fieldTemplateId) {
        FieldTemplateModel fieldTemplate = fieldTemplateRepository.findById(fieldTemplateId)
                .orElseThrow(() -> new RuntimeException("FieldTemplate not found"));

        List<ColumnSequenceModel> columnSequenceModels = columnSequenceRepository.findByFieldTemplate(fieldTemplate);

        if (columnSequenceModels.isEmpty()) {
            return new ArrayList<>();
        }

        return columnSequenceModels.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ColumnSequenceDto updateSequence(Long id, ColumnSequenceDto columnSequenceDto) {
        ColumnSequenceModel columnSequence = columnSequenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ColumnSequence not found"));

        if (columnSequenceDto.getFieldTemplateId() != null) {
            FieldTemplateModel fieldTemplate = fieldTemplateRepository.findById(columnSequenceDto.getFieldTemplateId())
                    .orElseThrow(() -> new RuntimeException("FieldTemplate not found"));
            columnSequence.setFieldTemplate(fieldTemplate);
        }

        if (columnSequenceDto.getColumnTemplateId() != null) {
            ColumnTemplateModel columnTemplate = columnTemplateRepository.findById(columnSequenceDto.getColumnTemplateId())
                    .orElseThrow(() -> new RuntimeException("ColumnTemplate not found"));
            columnSequence.setColumnTemplate(columnTemplate);
        }

        if (columnSequenceDto.getSequence() != null) {
            columnSequence.setSequence(columnSequenceDto.getSequence());
        }

        ColumnSequenceModel updatedSequence = columnSequenceRepository.save(columnSequence);
        return mapToDto(updatedSequence);
    }

    @Override
    public void deleteSequence(Long id) {
        if (!columnSequenceRepository.existsById(id)) {
            throw new RuntimeException("ColumnSequence not found");
        }
        columnSequenceRepository.deleteById(id);
    }

    private ColumnSequenceDto mapToDto(ColumnSequenceModel model) {
        return new ColumnSequenceDto(
                model.getId(),
                model.getFieldTemplate().getId(),
                model.getColumnTemplate().getId(),
                model.getSequence()
        );
    }

}
