package com.taskify.task.templates.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.task.templates.dtos.DropdownTemplateDto;

import java.util.List;

public interface DropdownTemplateServices {

    DropdownTemplateDto createDropdownTemplate(DropdownTemplateDto dropdownTemplateDto);

    List<DropdownTemplateDto> getAllDropdownTemplates();

    List<DropdownTemplateDto> getDropdownTemplatesByTaskTemplateId(Long taskTemplateId);

    List<DropdownTemplateDto> getDropdownTemplatesByFunctionTemplateId(Long functionTemplateId);

    List<DropdownTemplateDto> getDropdownTemplatesByColumnTemplateId(Long columnTemplateId);

    DropdownTemplateDto getDropdownTemplateById(Long id);

    DropdownTemplateDto updateDropdownTemplate(DropdownTemplateDto dropdownTemplateDto);

    boolean deleteDropdownTemplate(Long id);

    boolean deleteDropdownTemplatesByTaskTemplateId(Long taskTemplateId);

    boolean deleteDropdownTemplatesByFunctionTemplateId(Long functionTemplateId);

    boolean deleteDropdownTemplatesByColumnTemplateId(Long columnTemplateId);
    
}
