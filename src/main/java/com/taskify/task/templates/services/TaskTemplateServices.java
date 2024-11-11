package com.taskify.task.templates.services;

import com.taskify.task.templates.dtos.TaskTemplateDto;

import java.util.List;

public interface TaskTemplateServices {

    TaskTemplateDto createTaskTemplate(TaskTemplateDto taskTemplateDto);

    List<TaskTemplateDto> getAllTaskTemplates();

    TaskTemplateDto getTaskTemplateById(Long id);

    TaskTemplateDto getTaskTemplateByTitle(String title);

    TaskTemplateDto updateTaskTemplate(TaskTemplateDto taskTemplateDto);

    boolean deleteTaskTemplate(Long id);

}
