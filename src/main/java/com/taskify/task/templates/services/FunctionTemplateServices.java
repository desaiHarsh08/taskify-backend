package com.taskify.task.templates.services;

import com.taskify.common.constants.DepartmentType;
import com.taskify.task.templates.dtos.FunctionTemplateDto;

import java.util.List;

public interface FunctionTemplateServices {

    FunctionTemplateDto createFunctionTemplate(FunctionTemplateDto functionTemplateDto);

    List<FunctionTemplateDto> getAllFunctionTemplates();

    List<FunctionTemplateDto> getFunctionTemplatesByTaskTemplateId(Long taskTemplateId);

    FunctionTemplateDto getFunctionTemplateById(Long id);

    FunctionTemplateDto getFunctionTemplateByTitle(String title);

    FunctionTemplateDto getFunctionTemplateByTitleAndDepartment(String title, DepartmentType department);

    FunctionTemplateDto updateFunctionTemplate(FunctionTemplateDto functionTemplateDto);

    boolean unlinkFunctionTemplateFromTaskTemplate(Long id, Long taskTemplateId);

    boolean deleteFunctionTemplate(Long id);

    boolean unlinkFunctionTemplatesByTaskTemplateId(Long taskTemplateId);

    boolean linkFunctionTemplateToTaskTemplate(Long id, Long taskTemplateId);
    
}
