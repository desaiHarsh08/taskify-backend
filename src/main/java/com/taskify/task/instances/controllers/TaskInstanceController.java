package com.taskify.task.instances.controllers;

import com.taskify.common.constants.DateParamType;
import com.taskify.common.constants.PriorityType;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.TaskInstanceDto;
import com.taskify.task.instances.dtos.TaskSummaryDto;
import com.taskify.task.instances.services.TaskInstanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<PageResponse<TaskSummaryDto>> getAllTaskInstances(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getAllTaskInstances(pageNumber, pageSize);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/dismantle-due")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getDismantleTasksDue(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getDismantleDueTask(pageNumber, pageSize);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/estimate-due")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getEstimateTasksDue(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getEstimateDueTask(pageNumber, pageSize);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getPendingApprovalTasks(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getPendingApprovalTask(pageNumber, pageSize);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/approval-status")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getPendingApprovalTasks(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam(defaultValue = "false") boolean status
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getApprovalStatusTask(pageNumber, pageSize, status);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/by-assigned-user")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getAssignedToUserTaskInstances(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam Long assignedUserId
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getAssignedTaskInstances(pageNumber, pageSize, assignedUserId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }


    @GetMapping("/is-closed")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTaskInstancesByClosed(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam boolean isClosed
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getTaskInstancesByIsClosed(pageNumber, pageSize, isClosed);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/abbreviation-date")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTasksByAbbreviationAndCreatedDate(
            @RequestParam(name = "page") int pageNumber,
            @RequestParam(required = false) Integer pageSize, // Optional parameter

            @RequestParam(name = "abbreviation") String taskAbbreviation, @RequestParam(name = "date") LocalDate date) {
        return new ResponseEntity<>(
                this.taskInstanceServices.getTaskByAbbreviationAndCreatedDate(pageNumber, pageSize, taskAbbreviation, date),
                HttpStatus.OK);
    }

    @GetMapping("/abbreviation/{abbreviation}")
    public ResponseEntity<TaskInstanceDto> getTasksByAbbreviation(
            @PathVariable String abbreviation) {
        return new ResponseEntity<>(
                this.taskInstanceServices.getTaskInstanceByAbbreviation(abbreviation),
                HttpStatus.OK);
    }


//    @GetMapping("/summary")
//    public ResponseEntity<PageResponse<TaskSummaryDto>> getAllTaskSummary(
//            @RequestParam(name = "page", required = false) Integer pageNumber, // Optional parameter
//            @RequestParam(required = false) Integer pageSize, // Optional parameter
//            @RequestParam(required = false) PriorityType priority, // Optional parameter
//            @RequestParam(required = false) Boolean overdueFlag, // Optional parameter
//            @RequestParam(required = false) Boolean pendingFlag // Optional parameter
//    ) {
//        // Set default values for the parameters if they are null
//        if (pageNumber == null) {
//            pageNumber = 1; // Default page number if not provided
//        }
//        if (pageSize == null) {
//            pageSize = 100; // Default page size if not provided
//        }
//
//        // Ensure overdueFlag and pendingFlag are true if provided, else false
//        if (overdueFlag == null) {
//            overdueFlag = false; // Default overdueFlag if not provided
//        } else {
//            overdueFlag = true; // Set to true if provided
//        }
//
//        if (pendingFlag == null) {
//            pendingFlag = false; // Default pendingFlag if not provided
//        } else {
//            pendingFlag = true; // Set to true if provided
//        }
//
//        // Call the service with the provided (or default) values
//        PageResponse<TaskSummaryDto> taskSummaryDtos = taskInstanceServices.getTasksSummary(pageNumber, pageSize, priority, overdueFlag, pendingFlag);
//        return new ResponseEntity<>(taskSummaryDtos, HttpStatus.OK);
//    }



    @GetMapping("/template/{taskTemplateId}")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTaskInstancesByTaskTemplateById(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long taskTemplateId
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getTaskInstancesByTaskTemplateById(pageNumber, pageSize, taskTemplateId);
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
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTaskInstancesByPriorityType(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable PriorityType priorityType

    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getTaskInstancesByPriorityType(pageNumber, pageSize, priorityType);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/created-by/{createdByUserId}")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTaskInstancesByCreatedByUserId(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long createdByUserId
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getTaskInstancesByCreatedByUserId(pageNumber, pageSize, createdByUserId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/closed-by/{closedByUserId}")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTaskInstancesByClosedByUserId(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @PathVariable Long closedByUserId
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getTaskInstancesByClosedByUserId(pageNumber, pageSize, closedByUserId);
        return new ResponseEntity<>(taskInstances, HttpStatus.OK);
    }

    @GetMapping("/overdue")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getOverdueTaskInstances(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        PageResponse<TaskSummaryDto> overdueTaskInstances = taskInstanceServices.getOverdueTaskInstances(pageNumber, pageSize);
        return new ResponseEntity<>(overdueTaskInstances, HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<PageResponse<TaskSummaryDto>> getTaskInstancesByDate(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam LocalDateTime date,
            @RequestParam DateParamType type
    ) {
        PageResponse<TaskSummaryDto> taskInstances = taskInstanceServices.getTaskInstancesByDate(pageNumber, pageSize, date, type);
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

    @GetMapping("/search")
    public ResponseEntity<?> searchTaskInstance(
            @RequestParam String searchTxt,
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return new ResponseEntity<>(
                this.taskInstanceServices.searchTaskInstance(searchTxt, pageNumber, pageSize),
                HttpStatus.OK
        );
    }

    @GetMapping("/custom-filter")
    public ResponseEntity<?> filterTaskInstance(
            @RequestParam(name = "filterBy", defaultValue = "Dismantle Due") String by,
            @RequestParam(defaultValue = "false") boolean status,
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return new ResponseEntity<>(
                this.taskInstanceServices.customFilters(by, status, pageNumber, pageSize),
                HttpStatus.OK
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskInstance(@PathVariable Long id) {
        boolean isDeleted = taskInstanceServices.deleteTaskInstance(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/task-summary/{id}")
    public ResponseEntity<?> getTaskSummaryById(@PathVariable Long id) {
        TaskSummaryDto taskSummaryDto = taskInstanceServices.getTaskSummaryByTaskId(id);
        return new ResponseEntity<>(taskSummaryDto, HttpStatus.OK);
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
