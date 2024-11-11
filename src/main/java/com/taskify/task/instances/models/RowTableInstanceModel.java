package com.taskify.task.instances.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "row_table_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RowTableInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ColumnInstanceModel.class)
    @JoinColumn(name = "column_instance_id_fk")
    private ColumnInstanceModel columnInstance;

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

    public RowTableInstanceModel(Long id) {
        this.id = id;
    }

}
