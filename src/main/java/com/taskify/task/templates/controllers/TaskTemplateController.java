package com.taskify.task.templates.controllers;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.dtos.TaskTemplateDto;
import com.taskify.task.templates.services.TaskTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-templates")
public class TaskTemplateController {

    @Autowired
    private TaskTemplateServices taskTemplateServices;

    @PostMapping
    @CacheEvict(value = CacheNames.ALL_TASK_TEMPLATES, allEntries = true)
    public ResponseEntity<TaskTemplateDto> createTaskTemplate(@RequestBody TaskTemplateDto taskTemplateDto) {
        TaskTemplateDto createdTemplate = taskTemplateServices.createTaskTemplate(taskTemplateDto);
        if (createdTemplate == null) {
            return new ResponseEntity<>(this.taskTemplateServices.getTaskTemplateByTitle(taskTemplateDto.getTitle()), HttpStatus.OK);
        }

        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    @Cacheable(value = CacheNames.ALL_TASK_TEMPLATES)
    public ResponseEntity<List<TaskTemplateDto>> getAllTaskTemplates() {
        List<TaskTemplateDto> templates = taskTemplateServices.getAllTaskTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(value = CacheNames.TASK_TEMPLATE, key = "#id")
    public ResponseEntity<TaskTemplateDto> getTaskTemplateById(@PathVariable Long id) {
        TaskTemplateDto taskTemplateDto = taskTemplateServices.getTaskTemplateById(id);
        return new ResponseEntity<>(taskTemplateDto, HttpStatus.OK);
    }

    @GetMapping("/title")
    @Cacheable(value = CacheNames.TASK_TEMPLATE, key = "#title")
    public ResponseEntity<TaskTemplateDto> getTaskTemplateByTitle(@RequestParam String title) {
        TaskTemplateDto taskTemplateDto = taskTemplateServices.getTaskTemplateByTitle(title);
        return new ResponseEntity<>(taskTemplateDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = {CacheNames.TASK_TEMPLATE}, key = "#id")
    public ResponseEntity<TaskTemplateDto> updateTaskTemplate(
            @PathVariable Long id,
            @RequestBody TaskTemplateDto taskTemplateDto
    ) {
        if (!taskTemplateDto.getId().equals(id)) {
            throw new IllegalArgumentException("The template ID in the path does not match the ID in the request body.");
        }
        TaskTemplateDto updatedTemplate = taskTemplateServices.updateTaskTemplate(taskTemplateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = CacheNames.TASK_TEMPLATE, key = "#id")
    public ResponseEntity<Void> deleteTaskTemplate(@PathVariable Long id) {
        boolean isDeleted = taskTemplateServices.deleteTaskTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
