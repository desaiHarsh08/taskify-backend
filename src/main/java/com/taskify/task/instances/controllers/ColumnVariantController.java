package com.taskify.task.instances.controllers;

import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnVariantInstanceDto;
import com.taskify.task.instances.services.ColumnVariantInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/column-variant-instances")
public class ColumnVariantController {

    @Autowired
    private ColumnVariantInstanceServices columnVariantInstanceServices;

    @PostMapping
    public ResponseEntity<ColumnVariantInstanceDto> createColumnVariantInstance(@RequestBody ColumnVariantInstanceDto columnVariantInstanceDto) {
        ColumnVariantInstanceDto createdInstance = columnVariantInstanceServices.createColumnVariantInstance(columnVariantInstanceDto);
        return new ResponseEntity<>(createdInstance, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ColumnVariantInstanceDto>> getAllColumnVariantInstances(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<ColumnVariantInstanceDto> columnVariants = columnVariantInstanceServices.getAllColumnVariantInstances(pageNumber, pageSize);
        return new ResponseEntity<>(columnVariants, HttpStatus.OK);
    }

    @GetMapping("/template/{columnVariantTemplateId}")
    public ResponseEntity<PageResponse<ColumnVariantInstanceDto>> getColumnVariantInstancesByColumnVariantTemplateById(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long columnVariantTemplateId
    ) {
        PageResponse<ColumnVariantInstanceDto> columnVariants = columnVariantInstanceServices.getColumnVariantInstancesByColumnVariantTemplateById(pageNumber, pageSize, columnVariantTemplateId);
        return new ResponseEntity<>(columnVariants, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColumnVariantInstanceDto> getColumnVariantInstanceById(@PathVariable Long id) {
        ColumnVariantInstanceDto columnVariantInstance = columnVariantInstanceServices.getColumnVariantInstanceById(id);
        return new ResponseEntity<>(columnVariantInstance, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColumnVariantInstanceDto> updateColumnVariantInstance(@PathVariable Long id, @RequestBody ColumnVariantInstanceDto columnVariantInstanceDto) {
        columnVariantInstanceDto.setId(id); // Ensure the ID matches the path variable
        ColumnVariantInstanceDto updatedInstance = columnVariantInstanceServices.updateColumnVariantInstance(columnVariantInstanceDto);
        return new ResponseEntity<>(updatedInstance, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteColumnVariantInstance(@PathVariable Long id) {
        boolean deleted = columnVariantInstanceServices.deleteColumnVariantInstance(id);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/template/{columnVariantTemplateId}")
    public ResponseEntity<Boolean> deleteColumnVariantInstancesByColumnVariantTemplateId(@PathVariable Long columnVariantTemplateId) {
        boolean deleted = columnVariantInstanceServices.deleteColumnVariantInstancesByColumnVariantTemplateId(columnVariantTemplateId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

}
