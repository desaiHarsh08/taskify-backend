package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColumnMetadataTemplateRepository extends JpaRepository<ColumnMetadataTemplateModel, Long> {

    @Override
    @Cacheable(value = CacheNames.COLUMN_METADATA_TEMPLATE, key = "#id")
    Optional<ColumnMetadataTemplateModel> findById(Long id);

    List<ColumnMetadataTemplateModel> findByType(ColumnType type);

    Optional<ColumnMetadataTemplateModel> findByTypeAndAcceptMultipleFiles(ColumnType type, boolean acceptMultipleFiles);

}
