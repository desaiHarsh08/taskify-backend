package com.taskify.task.instances.controllers;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.PriorityType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.TaskInstanceDto;
import com.taskify.task.instances.services.TaskInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/task-instances")
public class TaskInstanceController {

    @Autowired
    private TaskInstanceServices taskInstanceServices;

    @PostMapping
    public ResponseEntity<TaskInstanceDto> createTaskInstance(@RequestBody TaskInstanceDto taskInstanceDto) {
        TaskInstanceDto createdTaskInstance = taskInstanceServices.createTaskInstance(taskInstanceDto);
        return new ResponseEntity<>(createdTaskInstance, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<TaskInstanceDto>> getAllTaskInstances(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskInstanceDto> taskInstances = taskInstanceServices.getAllTaskInstances(pageNumber, pageSize);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/template/{taskTemplateId}")
    public ResponseEntity<PageResponse<TaskInstanceDto>> getTaskInstancesByTaskTemplateById(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long taskTemplateId
    ) {
        PageResponse<TaskInstanceDto> taskInstances = taskInstanceServices.getTaskInstancesByTaskTemplateById(pageNumber, pageSize, taskTemplateId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TaskInstanceDto>> getTaskInstancesByCustomerId(@PathVariable Long customerId) {
        List<TaskInstanceDto> taskInstances = taskInstanceServices.getTaskInstancesByCustomerId(customerId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskInstanceDto> getTaskInstanceById(@PathVariable Long id) {
        TaskInstanceDto taskInstance = taskInstanceServices.getTaskInstanceById(id);
        return new ResponseEntity<>(taskInstance, HttpStatus.OK);
    }


    @GetMapping("/priority/{priorityType}")
    public ResponseEntity<PageResponse<TaskInstanceDto>> getTaskInstancesByPriorityType(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable PriorityType priorityType
    ) {
        PageResponse<TaskInstanceDto> taskInstances = taskInstanceServices.getTaskInstancesByPriorityType(pageNumber, pageSize, priorityType);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/created-by/{createdByUserId}")
    public ResponseEntity<PageResponse<TaskInstanceDto>> getTaskInstancesByCreatedByUserId(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long createdByUserId
    ) {
        PageResponse<TaskInstanceDto> taskInstances = taskInstanceServices.getTaskInstancesByCreatedByUserId(pageNumber, pageSize, createdByUserId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/closed-by/{closedByUserId}")
    public ResponseEntity<PageResponse<TaskInstanceDto>> getTaskInstancesByClosedByUserId(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long closedByUserId
    ) {
        PageResponse<TaskInstanceDto> taskInstances = taskInstanceServices.getTaskInstancesByClosedByUserId(pageNumber, pageSize, closedByUserId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/overdue")
    public ResponseEntity<PageResponse<TaskInstanceDto>> getOverdueTaskInstances(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskInstanceDto> overdueTaskInstances = taskInstanceServices.getOverdueTaskInstances(pageNumber, pageSize);
        return new ResponseEntity<>(overdueTaskInstances, HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<PageResponse<TaskInstanceDto>> getTaskInstancesByDate(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam LocalDateTime date,
            @RequestParam DateParamType type
    ) {
        PageResponse<TaskInstanceDto> taskInstances = taskInstanceServices.getTaskInstancesByDate(pageNumber, pageSize, date, type);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskInstanceDto> updateTaskInstance(
            @PathVariable Long id,
            @RequestBody TaskInstanceDto taskInstanceDto,
            @RequestParam Long userId
    ) {
        if (!taskInstanceDto.getId().equals(id)) {
            throw new IllegalArgumentException("ID in path does not match ID in request body");
        }
        TaskInstanceDto updatedTaskInstance = taskInstanceServices.updateTaskInstance(taskInstanceDto, userId);
        return new ResponseEntity<>(updatedTaskInstance, HttpStatus.OK);
    }

    @GetMapping("/close/{id}")
    public ResponseEntity<?> closeTaskInstance(@PathVariable Long id, @RequestParam("userId") Long closedByUserId) {
        return new ResponseEntity<>(taskInstanceServices.closeTaskInstance(id, closedByUserId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskInstance(@PathVariable Long id) {
        boolean isDeleted = taskInstanceServices.deleteTaskInstance(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/template/{taskTemplateId}")
    public ResponseEntity<Void> deleteTaskInstancesByTaskTemplateId(@PathVariable Long taskTemplateId) {
        boolean isDeleted = taskInstanceServices.deleteTaskInstancesByTaskTemplateId(taskTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/dropdown/{dropdownTemplateId}")
    public ResponseEntity<Void> deleteTaskInstancesByDropdownTemplateId(@PathVariable Long dropdownTemplateId) {
        boolean isDeleted = taskInstanceServices.deleteTaskInstancesByDropdownTemplateId(dropdownTemplateId);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
