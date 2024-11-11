package com.taskify.task.templates.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "column_templates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(targetEntity = ColumnMetadataTemplateModel.class)
    @JoinColumn(name = "column_metadata_template", nullable = false)
    private ColumnMetadataTemplateModel columnMetadataTemplate;

    @ManyToMany(mappedBy = "columnTemplates", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FieldTemplateModel> fieldTemplates = new ArrayList<>();

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

    public ColumnTemplateModel(Long id) {
        this.id = id;
    }

}
