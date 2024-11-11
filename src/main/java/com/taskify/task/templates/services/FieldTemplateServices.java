package com.taskify.task.templates.services;

import com.taskify.task.templates.dtos.FieldTemplateDto;

import java.util.List;

public interface FieldTemplateServices {

    FieldTemplateDto createFieldTemplate(FieldTemplateDto fieldTemplateDto);

    List<FieldTemplateDto> getAllFieldTemplates();

    List<FieldTemplateDto> getFieldTemplatesByFunctionTemplateId(Long functionTemplateId);

    FieldTemplateDto getFieldTemplateById(Long id);

    FieldTemplateDto updateFieldTemplate(FieldTemplateDto FieldTemplateDto);

    boolean unlinkFieldTemplateFromFunctionTemplate(Long id, Long functionTemplateId);

    boolean deleteFieldTemplate(Long id);

    boolean unlinkFieldTemplatesByFunctionTemplateId(Long functionTemplateId);

    boolean linkFieldTemplateToFunctionTemplate(Long id, Long functionTemplateId);
    
}
