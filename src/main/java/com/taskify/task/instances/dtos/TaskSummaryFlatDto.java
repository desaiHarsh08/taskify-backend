package com.taskify.task.instances.dtos;

import com.taskify.common.constants.PriorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class TaskSummaryFlatDto {

    private Long id;
    private Long taskTemplateId;
    private String abbreviation;
    private String jobNumber;
    private Long customerId;
    private Long functionId;
    private PriorityType priorityType;
    private LocalDateTime closedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime receiptNoteCreatedAt;

    private String pumpMake;
    private String pumpType;
    private String stage;
    private String serialNumber;
    private String motorMake;
    private String hp;
    private String volts;
    private String phase;
}
