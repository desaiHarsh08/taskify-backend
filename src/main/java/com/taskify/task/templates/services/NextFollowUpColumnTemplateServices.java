package com.taskify.task.templates.services;

import com.taskify.task.templates.dtos.NextFollowUpColumnTemplateDto;
import com.taskify.task.templates.models.NextFollowUpColumnTemplateModel;

import java.util.List;

public interface NextFollowUpColumnTemplateServices {

    NextFollowUpColumnTemplateDto createTemplate(NextFollowUpColumnTemplateDto template);

    List<NextFollowUpColumnTemplateDto> getAllTemplates();

    List<NextFollowUpColumnTemplateDto> getTemplatesByColumnTemplateId(Long columnTemplateId);

    List<NextFollowUpColumnTemplateDto> getTemplatesByColumnVariantTemplateId(Long columnVariantTemplateId);

    NextFollowUpColumnTemplateDto getTemplateById(Long id);

    boolean deleteNextFollowUpColumnTemplateById(Long id);

}
