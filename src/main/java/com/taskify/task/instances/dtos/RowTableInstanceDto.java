package com.taskify.task.instances.dtos;

import com.taskify.task.instances.models.ColumnInstanceModel;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class RowTableInstanceDto {

    private Long id;

    private Long columnInstanceId;

    private List<ColTableInstanceDto> colTableInstances = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
