package com.taskify.task.instances.controllers;

import com.taskify.task.instances.dtos.ColTableInstanceDto;
import com.taskify.task.instances.services.ColTableInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/col-tables")
public class ColTableInstanceController {

    private final ColTableInstanceServices colTableInstanceServices;

    @Autowired
    public ColTableInstanceController(ColTableInstanceServices colTableInstanceServices) {
        this.colTableInstanceServices = colTableInstanceServices;
    }

    // Create a new ColTableInstance
    @PostMapping
    public ColTableInstanceDto createColTableInstance(@RequestBody ColTableInstanceDto colTableInstanceDto) {
        return colTableInstanceServices.createColTableInstance(colTableInstanceDto);
    }

    // Update an existing ColTableInstance by ID
    @PutMapping("/{id}")
    public ColTableInstanceDto updateColTableInstance(@PathVariable Long id, @RequestBody ColTableInstanceDto colTableInstanceDto) {
        return colTableInstanceServices.updateColTableInstance(id, colTableInstanceDto);
    }

    // Delete a ColTableInstance by ID
    @DeleteMapping("/{id}")
    public void deleteColTableInstance(@PathVariable Long id) {
        colTableInstanceServices.deleteColTableInstance(id);
    }

    // Get a ColTableInstance by ID
    @GetMapping("/{id}")
    public ColTableInstanceDto getColTableInstanceById(@PathVariable Long id) {
        return colTableInstanceServices.getColTableInstanceById(id);
    }



}
