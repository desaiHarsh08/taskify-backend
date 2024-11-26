package com.taskify.task.templates.controllers;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.dtos.FieldTemplateDto;
import com.taskify.task.templates.services.FieldTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-templates")
public class FieldTemplateController {

    @Autowired
    private FieldTemplateServices fieldTemplateServices;

    @PostMapping
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.FIELD_TEMPLATE
    }, allEntries = true)
    public ResponseEntity<FieldTemplateDto> createFieldTemplate(@RequestBody FieldTemplateDto fieldTemplateDto) {
        FieldTemplateDto createdTemplate = fieldTemplateServices.createFieldTemplate(fieldTemplateDto);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    @Cacheable(value = CacheNames.ALL_FIELD_TEMPLATES)
    public ResponseEntity<List<FieldTemplateDto>> getAllFieldTemplates() {
        List<FieldTemplateDto> templates = fieldTemplateServices.getAllFieldTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/function-template/{functionTemplateId}")
    @Cacheable(value = CacheNames.FIELD_TEMPLATE, key = "#functionTemplateId")
    public ResponseEntity<List<FieldTemplateDto>> getFieldTemplatesByFunctionTemplateId(@PathVariable Long functionTemplateId) {
        List<FieldTemplateDto> templates = fieldTemplateServices.getFieldTemplatesByFunctionTemplateId(functionTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(value = CacheNames.FIELD_TEMPLATE, key = "#id")
    public ResponseEntity<FieldTemplateDto> getFieldTemplateById(@PathVariable Long id) {
        FieldTemplateDto fieldTemplateDto = fieldTemplateServices.getFieldTemplateById(id);
        return new ResponseEntity<>(fieldTemplateDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = CacheNames.FIELD_TEMPLATE, key = "#id")
    public ResponseEntity<FieldTemplateDto> updateFieldTemplate(
            @PathVariable Long id,
            @RequestBody FieldTemplateDto fieldTemplateDto
    ) {
        if (!fieldTemplateDto.getId().equals(id)) {
            throw new IllegalArgumentException("The template ID in the path does not match the ID in the request body.");
        }
        FieldTemplateDto updatedTemplate = fieldTemplateServices.updateFieldTemplate(fieldTemplateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.FIELD_TEMPLATE
    }, key = "#id")
    public ResponseEntity<Void> deleteFieldTemplate(@PathVariable Long id) {
        boolean isDeleted = fieldTemplateServices.deleteFieldTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/unlink/function-template")
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.FIELD_TEMPLATE
    }, allEntries = true)
    public ResponseEntity<Void> unlinkFieldTemplateFromFunctionTemplate(
            @RequestParam Long id,
            @RequestParam Long functionTemplateId
    ) {
        boolean isUnlinked = fieldTemplateServices.unlinkFieldTemplateFromFunctionTemplate(id, functionTemplateId);
        return isUnlinked ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/unlink/function-template/{functionTemplateId}")
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.FIELD_TEMPLATE
    }, allEntries = true)
    public ResponseEntity<Void> unlinkFieldTemplatesByFunctionTemplateId(@PathVariable Long functionTemplateId) {
        boolean isUnlinked = fieldTemplateServices.unlinkFieldTemplatesByFunctionTemplateId(functionTemplateId);
        return isUnlinked ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/link-to-function")
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.FIELD_TEMPLATE
    }, allEntries = true)
    public ResponseEntity<?> linkFieldTemplateToFunctionTemplate(
            @RequestParam Long id,
            @RequestParam Long functionTemplateId) {

        boolean isLinked = this.fieldTemplateServices.linkFieldTemplateToFunctionTemplate(id, functionTemplateId);

        if (isLinked) {
            return new ResponseEntity<>("FieldTemplate linked to FunctionTemplate successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to link FieldTemplate to FunctionTemplate.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
