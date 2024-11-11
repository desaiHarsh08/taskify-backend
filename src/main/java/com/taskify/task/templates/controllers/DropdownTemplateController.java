package com.taskify.task.templates.controllers;

import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.services.DropdownTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dropdown-templates")
public class DropdownTemplateController {

    @Autowired
    private DropdownTemplateServices dropdownTemplateServices;

    @PostMapping
    public ResponseEntity<DropdownTemplateDto> createDropdownTemplate(@RequestBody DropdownTemplateDto dropdownTemplateDto) {
        DropdownTemplateDto createdTemplate = dropdownTemplateServices.createDropdownTemplate(dropdownTemplateDto);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DropdownTemplateDto>> getAllDropdownTemplates() {
        List<DropdownTemplateDto> templates = dropdownTemplateServices.getAllDropdownTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/task-template/{taskTemplateId}")
    public ResponseEntity<List<DropdownTemplateDto>> getDropdownTemplatesByTaskTemplateId(@PathVariable Long taskTemplateId) {
        List<DropdownTemplateDto> templates = dropdownTemplateServices.getDropdownTemplatesByTaskTemplateId(taskTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/function-template/{functionTemplateId}")
    public ResponseEntity<List<DropdownTemplateDto>> getDropdownTemplatesByFunctionTemplateId(@PathVariable Long functionTemplateId) {
        List<DropdownTemplateDto> templates = dropdownTemplateServices.getDropdownTemplatesByFunctionTemplateId(functionTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/column-template/{columnTemplateId}")
    public ResponseEntity<List<DropdownTemplateDto>> getDropdownTemplatesByColumnTemplateId(@PathVariable Long columnTemplateId) {
        List<DropdownTemplateDto> templates = dropdownTemplateServices.getDropdownTemplatesByColumnTemplateId(columnTemplateId);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DropdownTemplateDto> getDropdownTemplateById(@PathVariable Long id) {
        DropdownTemplateDto template = dropdownTemplateServices.getDropdownTemplateById(id);
        return new ResponseEntity<>(template, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DropdownTemplateDto> updateDropdownTemplate(
            @PathVariable Long id,
            @RequestBody DropdownTemplateDto dropdownTemplateDto
    ) {
        if (!dropdownTemplateDto.getId().equals(id)) {
            throw new IllegalArgumentException("ID in the path does not match the ID in the request body.");
        }
        DropdownTemplateDto updatedTemplate = dropdownTemplateServices.updateDropdownTemplate(dropdownTemplateDto);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDropdownTemplate(@PathVariable Long id) {
        boolean isDeleted = dropdownTemplateServices.deleteDropdownTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/task-template/{taskTemplateId}")
    public ResponseEntity<Void> deleteDropdownTemplatesByTaskTemplateId(@PathVariable Long taskTemplateId) {
        boolean isDeleted = dropdownTemplateServices.deleteDropdownTemplatesByTaskTemplateId(taskTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/function-template/{functionTemplateId}")
    public ResponseEntity<Void> deleteDropdownTemplatesByFunctionTemplateId(@PathVariable Long functionTemplateId) {
        boolean isDeleted = dropdownTemplateServices.deleteDropdownTemplatesByFunctionTemplateId(functionTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/column-template/{columnTemplateId}")
    public ResponseEntity<Void> deleteDropdownTemplatesByColumnTemplateId(@PathVariable Long columnTemplateId) {
        boolean isDeleted = dropdownTemplateServices.deleteDropdownTemplatesByColumnTemplateId(columnTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
