package com.taskify.task.instances.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taskify.task.templates.models.DropdownTemplateModel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionInstanceDto {

    private Long id;

    private Long functionTemplateId;

    private Long taskInstanceId;

    private Long createdByUserId;

    private Long closedByUserId;

    private Long dropdownTemplateId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate = LocalDateTime.now();

    List<FieldInstanceDto> fieldInstances = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime closedAt;

    List<String> filePaths = new ArrayList<>();

    private String remarks;

//    private List<MultipartFile> multipartFiles = new ArrayList<>();

}
