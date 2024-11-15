package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnTemplateRepository extends JpaRepository<ColumnTemplateModel, Long> {

    @Override
    @Cacheable(value = CacheNames.COLUMN_TEMPLATE, key = "#id", condition = "#result != null && #result.isPresent()")
    Optional<ColumnTemplateModel> findById(Long id);

    List<ColumnTemplateModel> findByFieldTemplates(FieldTemplateModel fieldTemplate);

    @Query("SELECT c FROM ColumnTemplateModel c WHERE LOWER(TRIM(c.name)) = LOWER(TRIM(:name)) AND c.columnMetadataTemplate = :columnMetadataTemplate")
    Optional<ColumnTemplateModel> findByNameAndColumnMetadataTemplate(
            @Param("name") String name,
            @Param("columnMetadataTemplate") ColumnMetadataTemplateModel columnMetadataTemplate
    );
}
