package com.taskify.task.templates.services.impl;

import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.task.templates.dtos.NextFollowUpColumnTemplateDto;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.models.NextFollowUpColumnTemplateModel;
import com.taskify.task.templates.repositories.NextFollowUpColumnTemplateRepository;
import com.taskify.task.templates.services.NextFollowUpColumnTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NextFollowUpColumnTemplateServicesImpl implements NextFollowUpColumnTemplateServices {

    @Autowired
    private NextFollowUpColumnTemplateRepository nextFollowUpColumnTemplateRepository;

    @Override
    public NextFollowUpColumnTemplateDto createTemplate(NextFollowUpColumnTemplateDto template) {
        NextFollowUpColumnTemplateModel newNextFollowUpColumnTemplateModel = new NextFollowUpColumnTemplateModel(
                null,
                template.getColumnTemplateId() == null ? null : new ColumnTemplateModel(template.getColumnTemplateId()),
                new ColumnTemplateModel(template.getNextFollowUpColumnTemplateId()),
                template.getColumnVariantTemplateId() == null ? null : new ColumnVariantTemplateModel(template.getColumnVariantTemplateId())
        );

        newNextFollowUpColumnTemplateModel = this.nextFollowUpColumnTemplateRepository.save(newNextFollowUpColumnTemplateModel);
        return this.convertToDto(newNextFollowUpColumnTemplateModel);
    }
    public List<NextFollowUpColumnTemplateDto> getTemplatesByColumnVariantTemplateId(Long columnVariantTemplateId) {
        List<NextFollowUpColumnTemplateModel> nextFollowUpColumnTemplateModels = this.nextFollowUpColumnTemplateRepository.findByColumnVariantTemplate(new ColumnVariantTemplateModel(columnVariantTemplateId));
        if (nextFollowUpColumnTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return nextFollowUpColumnTemplateModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }


    @Override
    public List<NextFollowUpColumnTemplateDto> getAllTemplates() {
        List<NextFollowUpColumnTemplateModel> nextFollowUpColumnTemplateModels = this.nextFollowUpColumnTemplateRepository.findAll();
        if (nextFollowUpColumnTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }

        return nextFollowUpColumnTemplateModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<NextFollowUpColumnTemplateDto> getTemplatesByColumnTemplateId(Long columnTemplateId) {
        List<NextFollowUpColumnTemplateModel> nextFollowUpColumnTemplateModels = this.nextFollowUpColumnTemplateRepository.findByColumnTemplate(new ColumnTemplateModel(columnTemplateId));
        if (nextFollowUpColumnTemplateModels.isEmpty()) {
            return new ArrayList<>();
        }
        System.out.println("Here got, nextFollowUpColumnTemplateModels: " + nextFollowUpColumnTemplateModels.size());
        return nextFollowUpColumnTemplateModels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public NextFollowUpColumnTemplateDto getTemplateById(Long id) {
        NextFollowUpColumnTemplateModel template = this.nextFollowUpColumnTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.NEXT_FOLLOW_UP_COLUMN, "id", id, true)
        );

        return this.convertToDto(template);
    }

    @Override
    public boolean deleteNextFollowUpColumnTemplateById(Long id) {
        if (this.nextFollowUpColumnTemplateRepository.existsById(id)) {
            this.nextFollowUpColumnTemplateRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Helper method to convert entity to DTO
    private NextFollowUpColumnTemplateDto convertToDto(NextFollowUpColumnTemplateModel model) {
        if (model == null) {
            return null;
        }
        NextFollowUpColumnTemplateDto dto = new NextFollowUpColumnTemplateDto();
        dto.setId(model.getId());
        if (model.getColumnTemplate() != null) {
            dto.setColumnTemplateId(model.getColumnTemplate().getId());
        }
        if (model.getColumnVariantTemplate() != null) {
            dto.setColumnVariantTemplateId(model.getColumnVariantTemplate().getId());
        }
        dto.setNextFollowUpColumnTemplateId(model.getNextFollowUpColumnTemplate().getId());

        return dto;
    }
    
}
