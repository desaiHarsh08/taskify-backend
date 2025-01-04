package com.taskify.task.templates.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnSequenceDto {

    private Long id;

    private Long fieldTemplateId;

    private Long columnTemplateId;

    private Integer sequence;

}
