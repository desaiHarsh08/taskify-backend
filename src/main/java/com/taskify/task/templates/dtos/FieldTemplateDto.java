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
public class FieldTemplateDto {

    private Long id;

    @NotNull(message = "Field template title can't be null")
    private String title;

    private String description;

    @NotNull(message = "Function template id can't be null")
    private Long functionTemplateId;

    private List<ColumnTemplateDto> columnTemplates = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
