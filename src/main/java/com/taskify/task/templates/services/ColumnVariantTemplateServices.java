package com.taskify.task.templates.services;

import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.dtos.ColumnVariantTemplateDto;

import java.util.List;

public interface ColumnVariantTemplateServices {

    ColumnVariantTemplateDto createColumnVariantTemplate(ColumnVariantTemplateDto columnVariantTemplateDto);

    List<ColumnVariantTemplateDto> getAllColumnVariantTemplates();

    List<ColumnVariantTemplateDto> getColumnVariantTemplatesByColumnTemplateId(Long columnTemplateId);

    List<ColumnVariantTemplateDto> getColumnVariantTemplatesByValueType(ColumnType valueType);

    ColumnVariantTemplateDto getColumnVariantTemplateById(Long id);

    ColumnVariantTemplateDto updateColumnVariantTemplate(ColumnVariantTemplateDto ColumnVariantTemplateDto);

    boolean deleteColumnVariantTemplate(Long id);

    boolean deleteColumnVariantTemplatesByColumnTemplateId(Long columnTemplateId);
    
}
