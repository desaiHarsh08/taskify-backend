package com.taskify.task.templates.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "next_follow_up_column_templates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NextFollowUpColumnTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ColumnTemplateModel.class)
    @JoinColumn(name = "column_template_id_fk")
    private ColumnTemplateModel columnTemplate;

    @ManyToOne(targetEntity = ColumnTemplateModel.class)
    @JoinColumn(name = "next_follow_up_column_template_id_fk", nullable = false)
    private ColumnTemplateModel nextFollowUpColumnTemplate;

    @ManyToOne(targetEntity = ColumnVariantTemplateModel.class)
    @JoinColumn(name = "column_variant_template_id_fk")
    private ColumnVariantTemplateModel columnVariantTemplate;


}
