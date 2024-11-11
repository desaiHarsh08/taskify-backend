package com.taskify.task.instances.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVariantInstanceDto {

    private Long id;

    private Long columnVariantTemplateId;

    private Long columnInstanceId;

    private LocalDate dateValue;

    private Long numberValue;

    private Boolean booleanValue;

    private String textValue;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
