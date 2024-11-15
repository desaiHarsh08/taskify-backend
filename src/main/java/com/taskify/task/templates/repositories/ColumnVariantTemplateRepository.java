package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnVariantTemplateRepository extends JpaRepository<ColumnVariantTemplateModel, Long> {

    @Override
    @Cacheable(value = CacheNames.COLUMN_VARIANT_TEMPLATE, key = "#id", condition = "#result != null && #result.isPresent()")
    Optional<ColumnVariantTemplateModel> findById(Long id);

    List<ColumnVariantTemplateModel> findByColumnTemplate(ColumnTemplateModel columnTemplate);

    List<ColumnVariantTemplateModel> findByValueType(ColumnType valueType);

}
