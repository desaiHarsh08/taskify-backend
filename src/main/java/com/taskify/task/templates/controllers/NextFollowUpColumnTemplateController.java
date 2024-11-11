package com.taskify.task.templates.controllers;

import com.taskify.task.templates.dtos.NextFollowUpColumnTemplateDto;
import com.taskify.task.templates.services.NextFollowUpColumnTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/next-follow-up-col-templates")
public class NextFollowUpColumnTemplateController {

    @Autowired
    private NextFollowUpColumnTemplateServices templateServices;

    // Create a new template
    @PostMapping
    public ResponseEntity<NextFollowUpColumnTemplateDto> createTemplate(@RequestBody NextFollowUpColumnTemplateDto templateDto) {
        NextFollowUpColumnTemplateDto createdTemplate = templateServices.createTemplate(templateDto);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    // Get all templates
    @GetMapping
    public ResponseEntity<List<NextFollowUpColumnTemplateDto>> getAllTemplates() {
        List<NextFollowUpColumnTemplateDto> templates = templateServices.getAllTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    // Get templates by column template ID
    @GetMapping("/column/{columnTemplateId}")
    public ResponseEntity<List<NextFollowUpColumnTemplateDto>> getTemplatesByColumnTemplateId(@PathVariable Long columnTemplateId) {
        List<NextFollowUpColumnTemplateDto> templates = templateServices.getTemplatesByColumnTemplateId(columnTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    // Get template by ID
    @GetMapping("/{id}")
    public ResponseEntity<NextFollowUpColumnTemplateDto> getTemplateById(@PathVariable Long id) {
        NextFollowUpColumnTemplateDto template = templateServices.getTemplateById(id);
        if (template != null) {
            return new ResponseEntity<>(template, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete template by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplateById(@PathVariable Long id) {
        boolean deleted = templateServices.deleteNextFollowUpColumnTemplateById(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
