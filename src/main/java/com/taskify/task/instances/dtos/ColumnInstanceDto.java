package com.taskify.task.instances.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taskify.task.instances.models.ColumnVariantInstanceModel;
import com.taskify.task.instances.models.RowTableInstanceModel;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import com.taskify.task.templates.models.DropdownTemplateModel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInstanceDto {

    private Long id;

    private Long columnTemplateId;

    private Long fieldInstanceId;

    private Long dropdownTemplateId;

    private LocalDate dateValue;

    private Long numberValue;

    private Boolean booleanValue;

    private String textValue;

    private List<RowTableInstanceDto> rowTableInstances = new ArrayList<>();

    private List<ColumnVariantInstanceDto> columnVariantInstances = new ArrayList<>();

    private List<String> filePaths = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private List<ColTableInstanceDto> colTableInstances = new ArrayList<>();

    private List<MultipartFile> multipartFiles = new ArrayList<>();

}
