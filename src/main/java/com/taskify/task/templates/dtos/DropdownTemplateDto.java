package com.taskify.task.templates.dtos;

import com.taskify.common.constants.DropdownLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DropdownTemplateDto {

    private Long id;

    @NotBlank(message = "Group can't be empty")
    @NotNull(message = "Group cannot be null for dropdown template")
    @Size(min = 2, max = 100, message = "Group should have characters between 2 and 100")
    private String group;

    private DropdownLevel level = DropdownLevel.COLUMN;

    @NotBlank(message = "Value can't be empty")
    @NotNull(message = "Value can't be null")
    @Size(min = 2, max = 100, message = "Value should have characters between 2 and 100")
    private String value;

    private Long taskTemplateId;

    private Long functionTemplateId;

    private Long columnTemplateId;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
