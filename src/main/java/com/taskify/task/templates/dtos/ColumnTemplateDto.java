package com.taskify.task.templates.dtos;

import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnTemplateDto {

    private Long id;

    @NotBlank(message = "Name can't be blank")
    private String name;

    private ColumnMetadataTemplateModel columnMetadataTemplate;

    @NotNull(message = "Field template id can't be null")
    private Long fieldTemplateId;

    private List<NextFollowUpColumnTemplateDto> nextFollowUpColumnTemplates = new ArrayList<>();

    private List<DropdownTemplateDto> dropdownTemplates =  new ArrayList<>();

    private List<ColumnVariantTemplateDto> columnVariantTemplates =  new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
