package com.taskify.task.templates.controllers;

import com.taskify.common.constants.CacheNames;
import com.taskify.common.constants.DepartmentType;
import com.taskify.task.templates.dtos.FunctionTemplateDto;
import com.taskify.task.templates.services.FunctionTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/function-templates")
public class FunctionTemplateController {

    @Autowired
    private FunctionTemplateServices functionTemplateServices;

    @PostMapping
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.FUNCTION_TEMPLATE,
    }, allEntries = true)  // Clear cache after insert
    public ResponseEntity<FunctionTemplateDto> createFunctionTemplate(@RequestBody FunctionTemplateDto functionTemplateDto) {
        FunctionTemplateDto createdTemplate = functionTemplateServices.createFunctionTemplate(functionTemplateDto);
        if (createdTemplate == null) {
            return new ResponseEntity<>(this.functionTemplateServices.getFunctionTemplateByTitle(functionTemplateDto.getTitle()), HttpStatus.OK);
        }

        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    @Cacheable(value = CacheNames.ALL_FUNCTION_TEMPLATES)
    public ResponseEntity<List<FunctionTemplateDto>> getAllFunctionTemplates() {
        List<FunctionTemplateDto> templates = functionTemplateServices.getAllFunctionTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/task-template/{taskTemplateId}")
    @Cacheable(value = CacheNames.FUNCTION_TEMPLATE, key = "#taskTemplateId")
    public ResponseEntity<List<FunctionTemplateDto>> getFunctionTemplatesByTaskTemplateId(@PathVariable Long taskTemplateId) {
        List<FunctionTemplateDto> templates = functionTemplateServices.getFunctionTemplatesByTaskTemplateId(taskTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(value = CacheNames.FUNCTION_TEMPLATE, key = "#id")
    public ResponseEntity<FunctionTemplateDto> getFunctionTemplateById(@PathVariable Long id) {
        FunctionTemplateDto functionTemplateDto = functionTemplateServices.getFunctionTemplateById(id);
        return new ResponseEntity<>(functionTemplateDto, HttpStatus.OK);
    }

    @GetMapping("/title")
    @Cacheable(value = CacheNames.FUNCTION_TEMPLATE, key = "#title")
    public ResponseEntity<FunctionTemplateDto> getFunctionTemplateByTitle(@RequestParam String title) {
        FunctionTemplateDto functionTemplateDto = functionTemplateServices.getFunctionTemplateByTitle(title);
        return new ResponseEntity<>(functionTemplateDto, HttpStatus.OK);
    }

    @GetMapping("/title-department")
    @Cacheable(value = CacheNames.FUNCTION_TEMPLATE, key = "#title + '-' + #department.name()")
    public ResponseEntity<FunctionTemplateDto> getFunctionTemplateByTitleAndDepartment(
            @RequestParam String title,
            @RequestParam DepartmentType department
    ) {
        FunctionTemplateDto functionTemplateDto = functionTemplateServices.getFunctionTemplateByTitleAndDepartment(title, department);
        return new ResponseEntity<>(functionTemplateDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = CacheNames.FUNCTION_TEMPLATE, key = "#id")
    public ResponseEntity<FunctionTemplateDto> updateFunctionTemplate(
            @PathVariable Long id,
            @RequestBody FunctionTemplateDto functionTemplateDto
    ) {
        if (!functionTemplateDto.getId().equals(id)) {
            throw new IllegalArgumentException("The template ID in the path does not match the ID in the request body.");
        }
        FunctionTemplateDto updatedTemplate = functionTemplateServices.updateFunctionTemplate(functionTemplateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = CacheNames.FUNCTION_TEMPLATE, key = "#id")
    public ResponseEntity<Void> deleteFunctionTemplate(@PathVariable Long id) {
        boolean isDeleted = functionTemplateServices.deleteFunctionTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/unlink/task-template")
    @CacheEvict(value = {
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.TASK_TEMPLATE
    }, key = "#taskTemplateId")
    public ResponseEntity<Void> unlinkFunctionTemplateFromTaskTemplate(
            @RequestParam Long id,
            @RequestParam Long taskTemplateId
    ) {
        boolean isUnlinked = functionTemplateServices.unlinkFunctionTemplateFromTaskTemplate(id, taskTemplateId);
        return isUnlinked ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/unlink/task-template/{taskTemplateId}")
    @CacheEvict(value = {"task_template", "task_templates"}, key = "#taskTemplateId")
    public ResponseEntity<Void> unlinkFunctionTemplatesByTaskTemplateId(@PathVariable Long taskTemplateId) {
        boolean isUnlinked = functionTemplateServices.unlinkFunctionTemplatesByTaskTemplateId(taskTemplateId);
        return isUnlinked ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/link-to-task")
    @CacheEvict(value = {
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.ALL_TASK_TEMPLATES
    }, key = "#taskTemplateId")
    public ResponseEntity<?> linkFunctionTemplateToTaskTemplate(
            @RequestParam Long id,
            @RequestParam Long taskTemplateId) {

        boolean isLinked = this.functionTemplateServices.linkFunctionTemplateToTaskTemplate(id, taskTemplateId);

        if (isLinked) {
            return new ResponseEntity<>("FunctionTemplate linked to TaskTemplate successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to link FunctionTemplate to TaskTemplate.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
