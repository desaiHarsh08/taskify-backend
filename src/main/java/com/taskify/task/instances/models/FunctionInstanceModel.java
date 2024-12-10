package com.taskify.task.instances.models;

import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.user.models.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "function_instances")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionInstanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FunctionTemplateModel.class)
    @JoinColumn(name = "function_template_id_fk")
    private FunctionTemplateModel functionTemplate;

    @ManyToOne(targetEntity = TaskInstanceModel.class)
    @JoinColumn(name = "task_instances_id_fk")
    private TaskInstanceModel taskInstance;

    private String remarks;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "created_by_user_id_fk")
    private UserModel createdByUser;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "closed_by_user_id_fk")
    private UserModel closedByUser;

    @ManyToOne(targetEntity = DropdownTemplateModel.class)
    @JoinColumn(name = "dropdown_template_id_fk")
    private DropdownTemplateModel dropdownTemplate;

    private LocalDateTime dueDate = LocalDateTime.now();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    public FunctionInstanceModel(Long id) {
        this.id = id;
    }

}
