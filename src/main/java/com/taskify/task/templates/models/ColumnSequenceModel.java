package com.taskify.task.templates.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "column_sequences")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnSequenceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FieldTemplateModel.class)
    @JoinColumn(name = "field_template_id_fk")
    private FieldTemplateModel fieldTemplate;

    @ManyToOne(targetEntity = ColumnTemplateModel.class)
    @JoinColumn(name = "column_template_id_fk")
    private ColumnTemplateModel columnTemplate;

    private Integer sequence;

}
