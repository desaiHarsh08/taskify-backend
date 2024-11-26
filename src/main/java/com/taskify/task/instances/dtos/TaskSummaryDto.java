package com.taskify.task.instances.dtos;

import com.taskify.common.constants.DepartmentType;
import com.taskify.common.constants.PriorityType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskSummaryDto {

    private String abbreviation;

    private Long customerId;

    private Long functionId;

    private PriorityType priorityType = PriorityType.NORMAL;

}
