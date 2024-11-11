package com.taskify.task.instances.models;

import com.taskify.common.constants.PriorityType;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.user.models.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Entity
@Table(name = "task_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = TaskTemplateModel.class)
    @JoinColumn(name = "task_template_id_fk", nullable = false)
    private TaskTemplateModel taskTemplate;

    @ManyToOne(targetEntity = CustomerModel.class)
    @JoinColumn(name = "customer_id_fk")
    private CustomerModel customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityType priorityType;

    private String pumpType;

    private String pumpManufacturer;

    private String abbreviation;

    private String requirements;

    private String specifications;

    private String problemDescription;

    @ManyToOne(targetEntity = DropdownTemplateModel.class)
    @JoinColumn(name = "dropdown_template_id_fk")
    private DropdownTemplateModel dropdownTemplate;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "created_by_user_id_fk", nullable = false)
    private UserModel createdByUser;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "assigned_to_user_id_fk", nullable = false)
    private UserModel assignedToUser;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "closed_by_user_id_fk")
    private UserModel closedByUser;

    private boolean isArchived;

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

    public void setPriorityType(PriorityType priorityType) {
        if (!EnumSet.allOf(PriorityType.class).contains(priorityType)) {
            throw new IllegalArgumentException("Invalid priority for task...!");
        }
        this.priorityType = priorityType;
    }

    public TaskInstanceModel(Long id) {
        this.id = id;
    }

}
