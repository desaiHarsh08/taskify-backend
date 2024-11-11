package com.taskify.task.instances.models;

import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.user.models.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "field_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FieldInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FieldTemplateModel.class)
    @JoinColumn(name = "field_template_id_fk", nullable = false)
    private FieldTemplateModel fieldTemplate;

    @ManyToOne(targetEntity = FunctionInstanceModel.class)
    @JoinColumn(name = "function_instance_id_fk", nullable = false)
    private FunctionInstanceModel functionInstance;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "created_by_user_id_fk", nullable = false)
    private UserModel createdByUser;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "closed_by_user_id_fk")
    private UserModel closedByUser;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public FieldInstanceModel(Long id) {
        this.id = id;
    }

}
