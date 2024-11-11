package com.taskify.task.templates.dtos;

import com.taskify.common.constants.DepartmentType;
import com.taskify.common.constants.FunctionTemplateType;
import com.taskify.user.models.DepartmentModel;
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
public class FunctionTemplateDto {

    private Long id;

    @NotNull(message = "Function template title can't be null")
    private String title;

    private String description;

    private boolean isChoice;

    @NotNull(message = "Function template should be following a certain department")
    private DepartmentType department;

    @NotNull(message = "Task template id can't be null")
    private Long taskTemplatesId;

    private Long nextFollowUpFunctionTemplateId;

    private List<FieldTemplateDto> fieldTemplates = new ArrayList<>();

    private List<DropdownTemplateDto> dropdownTemplates = new ArrayList<>();

    @NotNull(message = "Function template should have a type")
    private FunctionTemplateType type;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
