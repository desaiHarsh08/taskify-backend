package com.taskify.task.instances.dtos;

import com.taskify.common.constants.PriorityType;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.user.models.UserModel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceDto {

    private Long id;

    @NotNull(message = "Please provide the task template...")
    private Long taskTemplateId;

    @NotNull(message = "Please provide the customer...")
    private Long customerId;

    @NotNull(message = "Please provide the task priority...")
    private PriorityType priorityType;

    private String pumpType;

    private String pumpManufacturer;

    private String abbreviation;

    private String requirements;

    private String specifications;

    private String problemDescription;

    private Long dropdownTemplateId;

    private Long createdByUserId;

    private Long assignedToUserId;

    private Long closedByUserId;

    private boolean isArchived;

    List<FunctionInstanceDto> functionInstances = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime closedAt;


}
