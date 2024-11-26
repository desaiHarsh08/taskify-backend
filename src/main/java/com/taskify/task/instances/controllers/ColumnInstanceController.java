package com.taskify.task.instances.controllers;

import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnInstanceDto;
import com.taskify.task.instances.services.ColumnInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/column-instances")
public class ColumnInstanceController {

    @Autowired
    private ColumnInstanceServices columnInstanceServices;

    @PostMapping
    public ResponseEntity<ColumnInstanceDto> createColumnInstance(@RequestBody ColumnInstanceDto columnInstanceDto) {
        ColumnInstanceDto createdColumnInstance = columnInstanceServices.createColumnInstance(columnInstanceDto);
        return new ResponseEntity<>(createdColumnInstance, HttpStatus.CREATED);
    }

//    @PostMapping("/{id}/upload-files")
//    public ResponseEntity<Boolean> uploadFiles(
//            @PathVariable Long id,
//            @RequestParam("files") MultipartFile[] files,
//            @RequestBody ColumnInstanceDto columnInstanceDto) {
//        boolean uploaded = columnInstanceServices.uploadFiles(columnInstanceDto, files);
//        return new ResponseEntity<>(uploaded, HttpStatus.OK);
//    }


    @PostMapping(value = "/upload-files", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadFiles(
            @RequestPart("column") ColumnInstanceDto columnDto,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        return new ResponseEntity<>(
                this.columnInstanceServices.uploadFiles(columnDto, files),
                HttpStatus.OK);
    }

    @GetMapping("/get-files")
    public ResponseEntity<byte[]> getFile(@RequestParam String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] fileContent = inputStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            String mimeType = Files.probeContentType(file.toPath()); // Automatically determine MIME type
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // Default to binary if MIME type can't be determined
            }
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(file.getName()).build());

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<PageResponse<ColumnInstanceDto>> getAllColumnInstances(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<ColumnInstanceDto> columnInstances = columnInstanceServices.getAllColumnInstances(pageNumber, pageSize);
        return new ResponseEntity<>(columnInstances, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColumnInstanceDto> getColumnInstanceById(@PathVariable Long id) {
        ColumnInstanceDto columnInstance = columnInstanceServices.getColumnInstanceById(id);
        return new ResponseEntity<>(columnInstance, HttpStatus.OK);
    }

    @GetMapping("/template/{columnTemplateId}")
    public ResponseEntity<PageResponse<ColumnInstanceDto>> getColumnInstancesByColumnTemplateById(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long columnTemplateId
    ) {
        PageResponse<ColumnInstanceDto> columnInstances = columnInstanceServices.getColumnInstancesByColumnTemplateById(pageNumber, pageSize, columnTemplateId);
        return new ResponseEntity<>(columnInstances, HttpStatus.OK);
    }

    @GetMapping("/field-instance/{fieldInstanceId}")
    public ResponseEntity<List<ColumnInstanceDto>> getColumnInstancesByFieldInstanceId(@PathVariable Long fieldInstanceId) {
        List<ColumnInstanceDto> columnInstances = columnInstanceServices.getColumnInstancesByFieldInstanceId(fieldInstanceId);
        return new ResponseEntity<>(columnInstances, HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> readFileAsBytes(@RequestParam String filePath) {
        byte[] fileData = columnInstanceServices.readFileAsBytes(filePath);
        return fileData != null ? new ResponseEntity<>(fileData, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/file")
    public ResponseEntity<Boolean> deleteFile(@RequestParam String filePath) {
        boolean deleted = columnInstanceServices.deleteFile(filePath);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColumnInstanceDto> updateColumnInstance(@RequestBody ColumnInstanceDto columnInstanceDto) {
        ColumnInstanceDto updatedColumnInstance = columnInstanceServices.updateColumnInstance(columnInstanceDto);
        return new ResponseEntity<>(updatedColumnInstance, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteColumnInstance(@PathVariable Long id) {
        boolean deleted = columnInstanceServices.deleteColumnInstance(id);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/template/{columnTemplateId}")
    public ResponseEntity<Boolean> deleteColumnInstancesByColumnTemplateId(@PathVariable Long columnTemplateId) {
        boolean deleted = columnInstanceServices.deleteColumnInstancesByColumnTemplateId(columnTemplateId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/dropdown-template/{dropdownTemplateId}")
    public ResponseEntity<Boolean> deleteColumnInstancesByDropdownTemplateId(@PathVariable Long dropdownTemplateId) {
        boolean deleted = columnInstanceServices.deleteColumnInstancesByDropdownTemplateId(dropdownTemplateId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

}
