package com.taskify.task.templates.controllers;

import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.dtos.ColumnVariantTemplateDto;
import com.taskify.task.templates.services.ColumnVariantTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/column-variant-templates")
public class ColumnVariantTemplateController {

    @Autowired
    private ColumnVariantTemplateServices columnVariantTemplateServices;

    @PostMapping
    public ResponseEntity<ColumnVariantTemplateDto> createColumnVariantTemplate(@RequestBody ColumnVariantTemplateDto columnVariantTemplateDto) {
        ColumnVariantTemplateDto createdTemplate = columnVariantTemplateServices.createColumnVariantTemplate(columnVariantTemplateDto);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ColumnVariantTemplateDto>> getAllColumnVariantTemplates() {
        List<ColumnVariantTemplateDto> templates = columnVariantTemplateServices.getAllColumnVariantTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/column-template/{columnTemplateId}")
    public ResponseEntity<List<ColumnVariantTemplateDto>> getColumnVariantTemplatesByColumnTemplateId(@PathVariable Long columnTemplateId) {
        List<ColumnVariantTemplateDto> templates = columnVariantTemplateServices.getColumnVariantTemplatesByColumnTemplateId(columnTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/value-type/{valueType}")
    public ResponseEntity<List<ColumnVariantTemplateDto>> getColumnVariantTemplatesByValueType(@PathVariable ColumnType valueType) {
        List<ColumnVariantTemplateDto> templates = columnVariantTemplateServices.getColumnVariantTemplatesByValueType(valueType);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColumnVariantTemplateDto> getColumnVariantTemplateById(@PathVariable Long id) {
        ColumnVariantTemplateDto template = columnVariantTemplateServices.getColumnVariantTemplateById(id);
        return new ResponseEntity<>(template, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColumnVariantTemplateDto> updateColumnVariantTemplate(
            @PathVariable Long id,
            @RequestBody ColumnVariantTemplateDto columnVariantTemplateDto
    ) {
        if (!columnVariantTemplateDto.getId().equals(id)) {
            throw new IllegalArgumentException("ID in the path does not match the ID in the request body.");
        }
        ColumnVariantTemplateDto updatedTemplate = columnVariantTemplateServices.updateColumnVariantTemplate(columnVariantTemplateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColumnVariantTemplate(@PathVariable Long id) {
        boolean isDeleted = columnVariantTemplateServices.deleteColumnVariantTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/column-template/{columnTemplateId}")
    public ResponseEntity<Void> deleteColumnVariantTemplatesByColumnTemplateId(@PathVariable Long columnTemplateId) {
        boolean isDeleted = columnVariantTemplateServices.deleteColumnVariantTemplatesByColumnTemplateId(columnTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
