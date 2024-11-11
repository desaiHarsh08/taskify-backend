package com.taskify.task.instances.dtos;

import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.ColumnVariantInstanceModel;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColTableInstanceDto {

    private Long id;

    private Long rowTableInstanceId;

    private String textValue;

    private boolean booleanValue;

    private LocalDate dateValue;

    private Long numberValue;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
