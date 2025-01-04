package com.taskify.task.templates.repositories;

import com.taskify.task.templates.models.ColumnSequenceModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnSequenceRepository extends JpaRepository<ColumnSequenceModel, Long> {

    List<ColumnSequenceModel> findByFieldTemplate(FieldTemplateModel fieldTemplate);

}
