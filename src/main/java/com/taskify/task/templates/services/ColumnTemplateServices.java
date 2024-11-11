package com.taskify.task.templates.services;

import com.taskify.task.templates.dtos.ColumnTemplateDto;

import java.util.List;

public interface ColumnTemplateServices {

    ColumnTemplateDto createColumnTemplate(ColumnTemplateDto columnTemplateDto);

    List<ColumnTemplateDto> getAllColumnTemplates();

    List<ColumnTemplateDto> getColumnTemplatesByFieldTemplateId(Long fieldTemplateId);

    ColumnTemplateDto getColumnTemplateById(Long id);

    ColumnTemplateDto updateColumnTemplate(ColumnTemplateDto columnTemplateDto);

    boolean unlinkColumnTemplateFromFieldTemplate(Long id, Long fieldTemplateId);

    boolean deleteColumnTemplate(Long id);

    boolean deleteColumnTemplatesByColumnMetadataTemplateId(Long columnMetadataTemplateId);

    boolean unlinkColumnTemplatesByFieldTemplateId(Long fieldTemplateId);

    boolean linkColumnTemplateToFieldTemplate(Long id, Long fieldTemplateId);
    
}
