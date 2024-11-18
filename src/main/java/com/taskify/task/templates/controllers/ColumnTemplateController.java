package com.taskify.task.templates.controllers;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.dtos.ColumnTemplateDto;
import com.taskify.task.templates.services.ColumnTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/column-templates")
public class ColumnTemplateController {

    @Autowired
    private ColumnTemplateServices columnTemplateServices;

    @PostMapping
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.ALL_FIELD_TEMPLATES,
            CacheNames.FIELD_TEMPLATE,
            CacheNames.ALL_COLUMN_TEMPLATES,
            CacheNames.COLUMN_TEMPLATE
    }, allEntries = true)
    public ResponseEntity<ColumnTemplateDto> createColumnTemplate(@RequestBody ColumnTemplateDto columnTemplateDto) {
        ColumnTemplateDto createdTemplate = columnTemplateServices.createColumnTemplate(columnTemplateDto);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    @Cacheable(value = CacheNames.ALL_COLUMN_TEMPLATES)
    public ResponseEntity<List<ColumnTemplateDto>> getAllColumnTemplates() {
        List<ColumnTemplateDto> templates = columnTemplateServices.getAllColumnTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/field-template/{fieldTemplateId}")
    @Cacheable(value = CacheNames.COLUMN_TEMPLATE, key = "#fieldTemplateId")
    public ResponseEntity<List<ColumnTemplateDto>> getColumnTemplatesByFieldTemplateId(@PathVariable Long fieldTemplateId) {
        List<ColumnTemplateDto> templates = columnTemplateServices.getColumnTemplatesByFieldTemplateId(fieldTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(value = CacheNames.COLUMN_TEMPLATE, key = "#id")
    public ResponseEntity<ColumnTemplateDto> getColumnTemplateById(@PathVariable Long id) {
        ColumnTemplateDto columnTemplateDto = columnTemplateServices.getColumnTemplateById(id);
        return new ResponseEntity<>(columnTemplateDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = CacheNames.COLUMN_TEMPLATE, key = "#id")
    public ResponseEntity<ColumnTemplateDto> updateColumnTemplate(
            @PathVariable Long id,
            @RequestBody ColumnTemplateDto columnTemplateDto
    ) {
        if (!columnTemplateDto.getId().equals(id)) {
            throw new IllegalArgumentException("The ID in the path does not match the ID in the request body.");
        }
        ColumnTemplateDto updatedTemplate = columnTemplateServices.updateColumnTemplate(columnTemplateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = CacheNames.COLUMN_TEMPLATE, key = "#id")
    public ResponseEntity<Void> deleteColumnTemplate(@PathVariable Long id) {
        boolean isDeleted = columnTemplateServices.deleteColumnTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/unlink/field-template")
    public ResponseEntity<Void> unlinkColumnTemplateFromFieldTemplate(
            @RequestParam Long id,
            @RequestParam Long fieldTemplateId
    ) {
        boolean isUnlinked = columnTemplateServices.unlinkColumnTemplateFromFieldTemplate(id, fieldTemplateId);
        return isUnlinked ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/unlink/field-template/{fieldTemplateId}")
    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.ALL_FIELD_TEMPLATES,
            CacheNames.FIELD_TEMPLATE,
            CacheNames.ALL_COLUMN_TEMPLATES,
            CacheNames.COLUMN_TEMPLATE
    }, allEntries = true)
    public ResponseEntity<Void> unlinkColumnTemplatesByFieldTemplateId(@PathVariable Long fieldTemplateId) {
        boolean isUnlinked = columnTemplateServices.unlinkColumnTemplatesByFieldTemplateId(fieldTemplateId);
        return isUnlinked ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.ALL_FIELD_TEMPLATES,
            CacheNames.FIELD_TEMPLATE,
            CacheNames.ALL_COLUMN_TEMPLATES,
            CacheNames.COLUMN_TEMPLATE
    }, allEntries = true)
    @DeleteMapping("/column-metadata-template/{columnMetadataTemplateId}")
    public ResponseEntity<Void> deleteColumnTemplatesByColumnMetadataTemplateId(@PathVariable Long columnMetadataTemplateId) {
        boolean isDeleted = columnTemplateServices.deleteColumnTemplatesByColumnMetadataTemplateId(columnMetadataTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CacheEvict(value = {
            CacheNames.ALL_TASK_TEMPLATES,
            CacheNames.TASK_TEMPLATE,
            CacheNames.ALL_FUNCTION_TEMPLATES,
            CacheNames.FUNCTION_TEMPLATE,
            CacheNames.ALL_FIELD_TEMPLATES,
            CacheNames.FIELD_TEMPLATE,
            CacheNames.ALL_COLUMN_TEMPLATES,
            CacheNames.COLUMN_TEMPLATE
    }, allEntries = true)
    @GetMapping("/link-to-field")
    public ResponseEntity<?> linkColumnTemplateToFieldTemplate(
            @RequestParam Long id,
            @RequestParam Long fieldTemplateId
    ) {

        boolean isLinked = this.columnTemplateServices.linkColumnTemplateToFieldTemplate(id, fieldTemplateId);

        if (isLinked) {
            return new ResponseEntity<>("ColumnTemplate linked to FieldTemplate successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to link ColumnTemplate to FieldTemplate.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
