package com.taskify.task.instances.controllers;

import com.taskify.task.instances.dtos.RowTableInstanceDto;
import com.taskify.task.instances.services.RowTableInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rowTableInstances")
public class RowTableInstanceController {

    @Autowired
    private RowTableInstanceServices rowTableInstanceService;

    @PostMapping
    public ResponseEntity<RowTableInstanceDto> createRowTableInstance(@RequestBody RowTableInstanceDto rowTableInstanceDto) {
        RowTableInstanceDto createdInstance = rowTableInstanceService.createRowTableInstance(rowTableInstanceDto);
        return ResponseEntity.ok(createdInstance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RowTableInstanceDto> updateRowTableInstance(@PathVariable Long id, @RequestBody RowTableInstanceDto rowTableInstanceDto) {
        RowTableInstanceDto updatedInstance = rowTableInstanceService.updateRowTableInstance(id, rowTableInstanceDto);
        return ResponseEntity.ok(updatedInstance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRowTableInstance(@PathVariable Long id) {
        rowTableInstanceService.deleteRowTableInstance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RowTableInstanceDto> getRowTableInstanceById(@PathVariable Long id) {
        RowTableInstanceDto instance = rowTableInstanceService.getRowTableInstanceById(id);
        return ResponseEntity.ok(instance);
    }

    @GetMapping("/columnInstance/{columnInstanceId}")
    public ResponseEntity<List<RowTableInstanceDto>> getRowTableInstancesByColumnInstanceId(@PathVariable Long columnInstanceId) {
        List<RowTableInstanceDto> instances = rowTableInstanceService.getRowTableInstancesByColumnInstanceId(columnInstanceId);
        return ResponseEntity.ok(instances);
    }
}