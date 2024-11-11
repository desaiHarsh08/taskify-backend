package com.taskify.task.instances.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldInstanceDto {

    private Long id;

    private Long fieldTemplateId;

    private Long functionInstanceId;

    private Long createdByUserId;

    private Long closedByUserId;

    private List<ColumnInstanceDto> columnInstances = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime closedAt;

}
