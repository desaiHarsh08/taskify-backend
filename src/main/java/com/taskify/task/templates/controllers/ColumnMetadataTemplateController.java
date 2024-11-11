package com.taskify.task.templates.controllers;

import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.services.ColumnMetadataTemplateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/column-metadata-templates")
public class ColumnMetadataTemplateController {

    @Autowired
    private ColumnMetadataTemplateServices columnMetadataTemplateServices;

    @PostMapping
    public ResponseEntity<ColumnMetadataTemplateModel> createColumnMetadataTemplate(@RequestBody ColumnMetadataTemplateModel columnMetadataTemplateModel) {
        ColumnMetadataTemplateModel createdTemplate = columnMetadataTemplateServices.createColumnMetadataTemplate(columnMetadataTemplateModel);
        if (createdTemplate == null) {
            return new ResponseEntity<>(
                    this.columnMetadataTemplateServices.getColumnMetadataTemplateByTypeAndAcceptingMultipleFiles(columnMetadataTemplateModel.getType(), columnMetadataTemplateModel.isAcceptMultipleFiles()),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ColumnMetadataTemplateModel>> getAllColumnMetadataTemplates() {
        List<ColumnMetadataTemplateModel> templates = columnMetadataTemplateServices.getAllColumnMetadataTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ColumnMetadataTemplateModel>> getColumnMetadataTemplatesByType(@PathVariable ColumnType type) {
        List<ColumnMetadataTemplateModel> templates = columnMetadataTemplateServices.getColumnMetadataTemplatesByType(type);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColumnMetadataTemplateModel> getColumnMetadataTemplateById(@PathVariable Long id) {
        ColumnMetadataTemplateModel columnMetadataTemplateModel = columnMetadataTemplateServices.getColumnMetadataTemplateById(id);
        return new ResponseEntity<>(columnMetadataTemplateModel, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColumnMetadataTemplateModel> updateColumnMetadataTemplate(
            @PathVariable Long id,
            @RequestBody ColumnMetadataTemplateModel columnMetadataTemplateModel
    ) {
        if (!columnMetadataTemplateModel.getId().equals(id)) {
            throw new IllegalArgumentException("The ID in the path does not match the ID in the request body.");
        }
        ColumnMetadataTemplateModel updatedTemplate = columnMetadataTemplateServices.updateColumnMetadataTemplate(columnMetadataTemplateModel);
        return new ResponseEntity<>(updatedTemplate, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColumnMetadataTemplate(@PathVariable Long id) {
        boolean isDeleted = columnMetadataTemplateServices.deleteColumnMetadataTemplate(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
