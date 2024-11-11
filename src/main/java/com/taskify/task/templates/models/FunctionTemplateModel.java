package com.taskify.task.templates.models;

import com.taskify.common.constants.DepartmentType;
import com.taskify.common.constants.FunctionTemplateType;
import com.taskify.user.models.DepartmentModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Entity
@Table(name = "function_templates", uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "department"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private boolean isChoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentType department;

    @ManyToMany(mappedBy = "functionTemplates", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskTemplateModel> taskTemplates = new ArrayList<>();

    @OneToOne(targetEntity = FunctionTemplateModel.class)
    @JoinColumn(name = "next_follow_up_function_template_id_fk")
    private FunctionTemplateModel nextFollowUpFunctionTemplateModel;

    @ManyToMany
    @JoinTable(
            name = "function_templates_field_templates",
            joinColumns = @JoinColumn(name = "function_template_id"),
            inverseJoinColumns = @JoinColumn(name = "field_template_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"function_template_id", "field_template_id"})}
    )
    private List<FieldTemplateModel> fieldTemplates = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private FunctionTemplateType type = FunctionTemplateType.NORMAL;

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

    public FunctionTemplateModel(Long id) {
        this.id = id;
    }

    public void setType(FunctionTemplateType type) {
        if (!EnumSet.allOf(FunctionTemplateType.class).contains(type)) {
            throw new IllegalArgumentException("Invalid type for function_template provided...!");
        }
        this.type = type;
    }

    public void setDepartment(DepartmentType department) {
        if (!EnumSet.allOf(DepartmentType.class).contains(department)) {
            throw new IllegalArgumentException("Please provide the valid department!");
        }
        this.department = department;
    }

    public void removeFieldTemplate(FieldTemplateModel fieldTemplate) {
        fieldTemplates.remove(fieldTemplate);
        fieldTemplate.getFunctionTemplates().remove(this);
    }

}
