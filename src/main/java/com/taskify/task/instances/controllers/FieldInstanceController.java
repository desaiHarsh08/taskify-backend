package com.taskify.task.instances.controllers;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.FieldInstanceDto;
import com.taskify.task.instances.services.FieldInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/field-instances")
public class FieldInstanceController {

    @Autowired
    private FieldInstanceServices fieldInstanceServices;

    @PostMapping
    public ResponseEntity<FieldInstanceDto> createFieldInstance(@RequestBody FieldInstanceDto fieldInstanceDto, @RequestParam Long userId) {
        FieldInstanceDto createdFieldInstance = fieldInstanceServices.createFieldInstance(fieldInstanceDto, userId);
        return new ResponseEntity<>(createdFieldInstance, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<FieldInstanceDto>> getAllFieldInstances(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<FieldInstanceDto> fieldInstances = fieldInstanceServices.getAllFieldInstances(pageNumber, pageSize);
        return new ResponseEntity<>(fieldInstances, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldInstanceDto> getFieldInstanceById(@PathVariable Long id) {
        FieldInstanceDto fieldInstance = fieldInstanceServices.getFieldInstanceById(id);
        return new ResponseEntity<>(fieldInstance, HttpStatus.OK);
    }

    @GetMapping("/template/{fieldTemplateId}")
    public ResponseEntity<PageResponse<FieldInstanceDto>> getFieldInstancesByFieldTemplateById(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long fieldTemplateId
    ) {
        PageResponse<FieldInstanceDto> fieldInstances = fieldInstanceServices.getFieldInstancesByFieldTemplateById(pageNumber, pageSize, fieldTemplateId);
        return new ResponseEntity<>(fieldInstances, HttpStatus.OK);
    }

    @GetMapping("/function-instance/{functionInstanceId}")
    public ResponseEntity<List<FieldInstanceDto>> getFieldInstancesByFunctionInstanceId(@PathVariable Long functionInstanceId) {
        List<FieldInstanceDto> fieldInstances = fieldInstanceServices.getFieldInstancesByFunctionInstanceId(functionInstanceId);
        return new ResponseEntity<>(fieldInstances, HttpStatus.OK);
    }

    @GetMapping("/created-by/{createdByUserId}")
    public ResponseEntity<PageResponse<FieldInstanceDto>> getFieldInstancesByCreatedByUserId(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long createdByUserId
    ) {
        PageResponse<FieldInstanceDto> fieldInstances = fieldInstanceServices.getFieldInstancesByCreatedByUserId(pageNumber, pageSize, createdByUserId);
        return new ResponseEntity<>(fieldInstances, HttpStatus.OK);
    }

    @GetMapping("/closed-by/{closedByUserId}")
    public ResponseEntity<PageResponse<FieldInstanceDto>> getFieldInstancesByClosedByUserId(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long closedByUserId
    ) {
        PageResponse<FieldInstanceDto> fieldInstances = fieldInstanceServices.getFieldInstancesByClosedByUserId(pageNumber, pageSize, closedByUserId);
        return new ResponseEntity<>(fieldInstances, HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<PageResponse<FieldInstanceDto>> getFieldInstancesByDate(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam LocalDateTime date,
            @RequestParam DateParamType type
    ) {
        PageResponse<FieldInstanceDto> fieldInstances = fieldInstanceServices.getFieldInstancesByDate(pageNumber, pageSize, date, type);
        return new ResponseEntity<>(fieldInstances, HttpStatus.OK);
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<FieldInstanceDto> closeFieldInstance(
            @PathVariable Long id,
            @RequestParam Long closedByUserId
    ) {
        FieldInstanceDto closedFieldInstance = fieldInstanceServices.closeFieldInstance(id, closedByUserId);
        return new ResponseEntity<>(closedFieldInstance, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFieldInstance(@PathVariable Long id) {
        boolean isDeleted = fieldInstanceServices.deleteFieldInstance(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/template/{fieldTemplateId}")
    public ResponseEntity<Void> deleteFieldInstancesByFieldTemplateId(@PathVariable Long fieldTemplateId) {
        boolean isDeleted = fieldInstanceServices.deleteFieldInstancesByFieldTemplateId(fieldTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
