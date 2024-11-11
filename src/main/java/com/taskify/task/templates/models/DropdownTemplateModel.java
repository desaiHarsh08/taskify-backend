package com.taskify.task.templates.models;

import com.taskify.common.constants.DropdownLevel;
import com.taskify.task.templates.dtos.DropdownTemplateDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Entity
@Table(name = "dropdown_templates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DropdownTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String group;

    @Enumerated(EnumType.STRING)
    private DropdownLevel level = DropdownLevel.COLUMN;

    @Column(nullable = false)
    private String value;

    @ManyToOne(targetEntity = TaskTemplateModel.class)
    @JoinColumn(name = "task_template_id_fk")
    private TaskTemplateModel taskTemplate;

    @ManyToOne(targetEntity = FunctionTemplateModel.class)
    @JoinColumn(name = "function_template_id_fk")
    private FunctionTemplateModel functionTemplate;

    @ManyToOne(targetEntity = ColumnTemplateModel.class)
    @JoinColumn(name = "column_template_id_fk")
    private ColumnTemplateModel columnTemplate;

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

    public void setLevel(DropdownLevel dropdownLevel) {
        if (!EnumSet.allOf(DropdownLevel.class).contains(dropdownLevel)) {
            throw new IllegalArgumentException("Invalid dropdown level: " + dropdownLevel);
        }
        this.level = dropdownLevel;
    }

    public void setAssociatedTemplateId(DropdownTemplateDto dropdownTemplateDto) {
        switch (level) {
            case TASK:
                if (dropdownTemplateDto.getTaskTemplateId() == null) {
                    throw new IllegalArgumentException("Please provide a valid task_template ID");
                }
                this.taskTemplate = new TaskTemplateModel(dropdownTemplateDto.getTaskTemplateId());
                this.functionTemplate = null;
                this.columnTemplate = null;
                break;

            case FUNCTION:
                if (dropdownTemplateDto.getFunctionTemplateId() == null) {
                    throw new IllegalArgumentException("Please provide a valid function_template ID");
                }
                this.taskTemplate = null;
                this.functionTemplate = new FunctionTemplateModel(dropdownTemplateDto.getFunctionTemplateId());
                this.columnTemplate = null;
                break;

            case COLUMN:
                if (dropdownTemplateDto.getColumnTemplateId() == null) {
                    throw new IllegalArgumentException("Please provide a valid column_template ID");
                }
                this.taskTemplate = null;
                this.functionTemplate = null;
                this.columnTemplate = new ColumnTemplateModel(dropdownTemplateDto.getColumnTemplateId());
                break;

            default:
                throw new IllegalArgumentException("Unsupported dropdown level: " + level);
        }
    }

    public DropdownTemplateModel(Long id) {
        this.id = id;
    }

}
