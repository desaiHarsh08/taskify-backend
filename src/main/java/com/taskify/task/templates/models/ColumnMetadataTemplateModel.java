package com.taskify.task.templates.models;

import com.taskify.common.constants.ColumnType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Entity
@Table(name = "column_metadata_templates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetadataTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ColumnType type = ColumnType.TEXT;

    private boolean acceptMultipleFiles;

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

    public ColumnMetadataTemplateModel(Long id) {
        this.id = id;
    }

    public void setType(ColumnType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (!EnumSet.allOf(ColumnType.class).contains(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.type = type;
    }
}
