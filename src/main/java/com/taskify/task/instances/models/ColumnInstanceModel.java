package com.taskify.task.instances.models;

import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "column_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class    ColumnInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ColumnTemplateModel.class)
    @JoinColumn(name = "column_template_id_fk", nullable = false)
    private ColumnTemplateModel columnTemplate;

    @ManyToOne(targetEntity = FieldInstanceModel.class)
    @JoinColumn(name = "field_instance_id_fk", nullable = false)
    private FieldInstanceModel fieldInstance;

    @ManyToOne(targetEntity = DropdownTemplateModel.class)
    @JoinColumn(name = "dropdown_template_id_fk")
    private DropdownTemplateModel dropdownTemplate;

    private LocalDate dateValue;

    private Long numberValue;

    private Boolean booleanValue;

    private String textValue;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ColumnInstanceModel(Long id) {
        this.id = id;
    }

}
