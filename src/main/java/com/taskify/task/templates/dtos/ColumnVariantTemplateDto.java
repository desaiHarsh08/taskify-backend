package com.taskify.task.templates.dtos;

import com.taskify.common.constants.ColumnType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVariantTemplateDto {

    private Long id;

    @NotBlank(message = "Column template id can't be blank")
    private Long columnTemplateId;

    @NotBlank(message = "Name can't be blank")
    @Size(min = 2, max = 100, message = "Name should be having characters between 2 to 100")
    private String name;

    @NotBlank(message = "Value type can't be blank")
    private ColumnType valueType;

    private String targetedValue;

    private List<NextFollowUpColumnTemplateDto> nextFollowUpColumnTemplates = new ArrayList<>();

}
