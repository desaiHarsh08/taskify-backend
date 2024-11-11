package com.taskify.task.templates.models;

import com.taskify.user.models.RoleModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_templates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @ManyToMany(targetEntity = FunctionTemplateModel.class)
    @JoinTable(
            name = "task_templates_function_templates",
            joinColumns = @JoinColumn(name = "task_template_id"),
            inverseJoinColumns = @JoinColumn(name = "function_template_id")
    )
    private List<FunctionTemplateModel> functionTemplates = new ArrayList<>();

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

    public TaskTemplateModel(Long id) {
        this.id = id;
    }

    public void removeFunctionTemplate(FunctionTemplateModel functionTemplate) {
        functionTemplates.remove(functionTemplate);
        functionTemplate.getTaskTemplates().remove(this);
    }

}
