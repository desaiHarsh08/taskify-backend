package com.taskify.task.templates.controllers;

import com.taskify.task.templates.dtos.ColumnSequenceDto;
import com.taskify.task.templates.services.ColumnSequenceServices;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/column-sequences")
public class ColumnSequenceController {

    @Autowired
    private ColumnSequenceServices columnSequenceServices;

    @PostMapping
    public ResponseEntity<ColumnSequenceDto> createSequence(@RequestBody ColumnSequenceDto columnSequenceDto) {
        ColumnSequenceDto createdSequence = columnSequenceServices.createSequence(columnSequenceDto);
        return ResponseEntity.ok(createdSequence);
    }

    @GetMapping("/field-template/{fieldTemplateId}")
    public ResponseEntity<List<ColumnSequenceDto>> getSequencesByFieldTemplate(@PathVariable Long fieldTemplateId) {
        List<ColumnSequenceDto> sequences = columnSequenceServices.getSequencesByFieldTemplate(fieldTemplateId);
        return ResponseEntity.ok(sequences);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColumnSequenceDto> updateSequence(@PathVariable Long id, @RequestBody ColumnSequenceDto columnSequenceDto) {
        ColumnSequenceDto updatedSequence = columnSequenceServices.updateSequence(id, columnSequenceDto);
        return ResponseEntity.ok(updatedSequence);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSequence(@PathVariable Long id) {
        columnSequenceServices.deleteSequence(id);
        return ResponseEntity.noContent().build();
    }
    
}
