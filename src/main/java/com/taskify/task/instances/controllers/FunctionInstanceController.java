package com.taskify.task.instances.controllers;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.FunctionInstanceDto;
import com.taskify.task.instances.services.FunctionInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/function-instances")
public class FunctionInstanceController {

    @Autowired
    private FunctionInstanceServices functionInstanceServices;

    @PostMapping
    public ResponseEntity<FunctionInstanceDto> createFunctionInstance(@RequestBody FunctionInstanceDto functionInstanceDto, @RequestParam Long assignedToUserId) {
        System.out.println("functionInstanceDto: " + functionInstanceDto);
        FunctionInstanceDto createdFunctionInstance = functionInstanceServices.createFunctionInstance(functionInstanceDto, assignedToUserId);
        return new ResponseEntity<>(createdFunctionInstance, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload-files", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadFiles(
            @RequestPart("function") FunctionInstanceDto functionInstanceDto,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        return new ResponseEntity<>(
                this.functionInstanceServices.uploadFiles(functionInstanceDto, files),
                HttpStatus.OK);
    }

//    @PostMapping("/{id}/upload-files")
//    public ResponseEntity<Void> uploadFiles(
//            @PathVariable Long id,
//            @RequestPart("files") MultipartFile[] files,
//            @RequestBody FunctionInstanceDto functionInstanceDto
//    ) {
//        functionInstanceDto.setId(id);
//        boolean isUploaded = functionInstanceServices.uploadFiles(functionInstanceDto, files);
//        return isUploaded ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }

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
    public ResponseEntity<PageResponse<FunctionInstanceDto>> getAllFunctionInstances(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<FunctionInstanceDto> functionInstances = functionInstanceServices.getAllFunctionInstances(pageNumber, pageSize);
        return new ResponseEntity<>(functionInstances, HttpStatus.OK);
    }

    @GetMapping("/template/{functionTemplateId}")
    public ResponseEntity<PageResponse<FunctionInstanceDto>> getFunctionInstancesByFunctionTemplateById(
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long functionTemplateId
    ) {
        PageResponse<FunctionInstanceDto> functionInstances = functionInstanceServices.getFunctionInstancesByFunctionTemplateById(pageNumber, pageSize, functionTemplateId);
        return new ResponseEntity<>(functionInstances, HttpStatus.OK);
    }

    @GetMapping("/task-instance/{taskInstanceId}")
    public ResponseEntity<List<FunctionInstanceDto>> getFunctionInstancesByTaskInstanceId(@PathVariable Long taskInstanceId) {
        List<FunctionInstanceDto> functionInstances = functionInstanceServices.getFunctionInstancesByTaskInstanceId(taskInstanceId);
        return new ResponseEntity<>(functionInstances, HttpStatus.OK);
    }

    @GetMapping("/created-by/{createdByUserId}")
    public ResponseEntity<PageResponse<FunctionInstanceDto>> getFunctionInstancesByCreatedByUserId(
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long createdByUserId
    ) {
        PageResponse<FunctionInstanceDto> functionInstances = functionInstanceServices.getFunctionInstancesByCreatedByUserId(pageNumber, pageSize, createdByUserId);
        return new ResponseEntity<>(functionInstances, HttpStatus.OK);
    }

    @GetMapping("/closed-by/{closedByUserId}")
    public ResponseEntity<PageResponse<FunctionInstanceDto>> getFunctionInstancesByClosedByUserId(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long closedByUserId
    ) {
        PageResponse<FunctionInstanceDto> functionInstances = functionInstanceServices.getFunctionInstancesByClosedByUserId(pageNumber, pageSize, closedByUserId);
        return new ResponseEntity<>(functionInstances, HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<PageResponse<FunctionInstanceDto>> getFunctionInstancesByDate(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam LocalDateTime date,
            @RequestParam DateParamType type
    ) {
        PageResponse<FunctionInstanceDto> functionInstances = functionInstanceServices.getFunctionInstancesByDate(pageNumber, pageSize, date, type);
        return new ResponseEntity<>(functionInstances, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunctionInstanceDto> getFunctionInstanceById(@PathVariable Long id) {
        FunctionInstanceDto functionInstance = functionInstanceServices.getFunctionInstanceById(id);
        System.out.println(functionInstance);
        return new ResponseEntity<>(functionInstance, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionInstanceDto> updateFunctionInstance(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody FunctionInstanceDto functionInstanceDto
    ) {
        if (!functionInstanceDto.getId().equals(id)) {
            throw new IllegalArgumentException("ID in path does not match ID in request body");
        }
        FunctionInstanceDto updatedFunctionInstance = functionInstanceServices.updateFunctionInstance(functionInstanceDto, userId);
        return new ResponseEntity<>(updatedFunctionInstance, HttpStatus.OK);
    }

    @GetMapping("/close/{id}")
    public ResponseEntity<?> closeFunctionInstance(@PathVariable Long id, @RequestParam("userId") Long closedByUserId) {
        return new ResponseEntity<>(functionInstanceServices.closeFunction(id, closedByUserId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunctionInstance(@PathVariable Long id) {
        boolean isDeleted = functionInstanceServices.deleteFunctionInstance(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/task-instance/{taskInstanceId}")
    public ResponseEntity<Void> deleteFunctionInstancesByTaskInstanceId(@PathVariable Long taskInstanceId) {
        boolean isDeleted = functionInstanceServices.deleteFunctionInstancesByTaskInstanceId(taskInstanceId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/template/{functionTemplateId}")
    public ResponseEntity<Void> deleteFunctionInstancesByFunctionTemplateId(@PathVariable Long functionTemplateId) {
        boolean isDeleted = functionInstanceServices.deleteFunctionInstancesByFunctionTemplateId(functionTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/dropdown/{dropdownTemplateId}")
    public ResponseEntity<Void> deleteFunctionInstancesByDropdownTemplateId(@PathVariable Long dropdownTemplateId) {
        boolean isDeleted = functionInstanceServices.deleteFunctionInstancesByDropdownTemplateId(dropdownTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
