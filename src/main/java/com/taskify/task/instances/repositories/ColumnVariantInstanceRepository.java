package com.taskify.task.instances.repositories;

import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.ColumnVariantInstanceModel;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnVariantInstanceRepository extends JpaRepository<ColumnVariantInstanceModel, Long> {

    List<ColumnVariantInstanceModel> findByColumnInstance(ColumnInstanceModel columnInstance);

    Page<ColumnVariantInstanceModel> findByColumnVariantTemplate(Pageable pageable, ColumnVariantTemplateModel columnVariantTemplate);

    @Query("DELETE FROM ColumnVariantInstanceModel c WHERE c.columnVariantTemplate.id = :columnVariantTemplateId")
    int deleteByColumnVariantTemplateId(@Param("columnVariantTemplateId") Long columnVariantTemplateId);

}
