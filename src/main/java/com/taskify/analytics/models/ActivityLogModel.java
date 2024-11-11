package com.taskify.analytics.models;

import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.ResourceType;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.models.ParentCompanyModel;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.user.models.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType = ResourceType.TASK;

    @Enumerated(EnumType.STRING)
    private ActionType actionType = ActionType.CREATE;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "user_id_fk")
    private UserModel user;

    @ManyToOne(targetEntity = TaskInstanceModel.class)
    @JoinColumn(name = "task_instance_id_fk")
    private TaskInstanceModel taskInstance;

    @ManyToOne(targetEntity = FunctionInstanceModel.class)
    @JoinColumn(name = "function_instance_id_fk")
    private FunctionInstanceModel functionInstance;

    @ManyToOne(targetEntity = FieldInstanceModel.class)
    @JoinColumn(name = "field_instance_id_fk")
    private FieldInstanceModel fieldInstance;

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

}
