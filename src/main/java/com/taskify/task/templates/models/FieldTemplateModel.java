package com.taskify.task.templates.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "field_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToMany(mappedBy = "fieldTemplates")
    private List<FunctionTemplateModel> functionTemplates = new ArrayList<>();

    @ManyToMany(targetEntity = ColumnTemplateModel.class)
    @JoinTable(
            name = "field_templates_column_templates",
            joinColumns = @JoinColumn(name = "field_template_id"),
            inverseJoinColumns = @JoinColumn(name = "column_template_id")
    )
    private List<ColumnTemplateModel> columnTemplates = new ArrayList<>();

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

    public FieldTemplateModel(Long id) {
        this.id = id;
    }

    public void removeColumnTemplate(ColumnTemplateModel columnTemplate) {
        columnTemplates.remove(columnTemplate);
        columnTemplate.getFieldTemplates().remove(this);
    }

}
