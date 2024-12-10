package com.taskify.task.instances.models;

import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "column_variant_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVariantInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ColumnVariantTemplateModel.class)
    @JoinColumn(name = "column_variant_template_id_fk")
    private ColumnVariantTemplateModel columnVariantTemplate;

    @ManyToOne(targetEntity = ColumnInstanceModel.class)
    @JoinColumn(name = "column_instance_id_fk")
    private ColumnInstanceModel columnInstance;

    private LocalDate dateValue;

    private Long numberValue;

    private Boolean booleanValue;

    private String textValue;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ColumnVariantInstanceModel(Long id) {
        this.id = id;
    }

}
