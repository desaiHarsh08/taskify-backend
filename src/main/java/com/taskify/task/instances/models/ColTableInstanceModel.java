package com.taskify.task.instances.models;

import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "col_table_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColTableInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ColumnInstanceModel.class)
    @JoinColumn(name = "row_table_instance_id_fk")
    private RowTableInstanceModel rowTableInstance;

    private String textValue;

    private boolean booleanValue;

    private LocalDate dateValue;

    private Long numberValue;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
