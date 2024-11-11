package com.taskify.task.instances.repositories;

import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnInstanceRepository extends JpaRepository<ColumnInstanceModel, Long> {

    List<ColumnInstanceModel> findByFieldInstance(FieldInstanceModel fieldInstance);

    Page<ColumnInstanceModel> findByColumnTemplate(Pageable pageable, ColumnTemplateModel columnTemplate);

    Page<ColumnInstanceModel> findByDropdownTemplate(Pageable pageable, DropdownTemplateModel dropdownTemplate);

}
