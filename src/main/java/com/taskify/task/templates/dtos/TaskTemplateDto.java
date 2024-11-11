package com.taskify.task.templates.dtos;

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
public class TaskTemplateDto {

    private Long id;

    @NotNull(message = "Task template title can't be null")
    private String title;

    private List<FunctionTemplateDto> functionTemplates = new ArrayList<>();

    private List<DropdownTemplateDto> dropdownTemplates = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
