package com.taskify.task.templates.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NextFollowUpColumnTemplateDto {

    private Long id;

    private Long columnTemplateId;

    private Long columnVariantTemplateId;

    private Long nextFollowUpColumnTemplateId;

}
