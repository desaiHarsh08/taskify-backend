package com.taskify.task.templates.models;

import com.taskify.common.constants.ColumnType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "column_variant_templates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVariantTemplateModel {

    private static final Set<ColumnType> ALLOWED_VALUE_TYPES = EnumSet.of(
            ColumnType.TEXT,
            ColumnType.EMAIL,
            ColumnType.PHONE,
            ColumnType.AMOUNT,
            ColumnType.NUMBER,
            ColumnType.BOOLEAN,
            ColumnType.DATE
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "column_template_id")
    private ColumnTemplateModel columnTemplate;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ColumnType valueType;

    private String targetedValue;

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

    public void setValueType(ColumnType valueType) {
        if (!ALLOWED_VALUE_TYPES.contains(valueType)) {
            throw new IllegalArgumentException("Invalid valueType: " + valueType
                    + ". Allowed types: " + ALLOWED_VALUE_TYPES);
        }
        this.valueType = valueType;
    }

    public ColumnVariantTemplateModel(Long id) {
        this.id = id;
    }

}
