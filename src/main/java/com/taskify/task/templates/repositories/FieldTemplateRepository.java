package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldTemplateRepository extends JpaRepository<FieldTemplateModel, Long> {

    @Override
    @Cacheable(value = CacheNames.FIELD_TEMPLATE, key = "#id", condition = "#result != null && #result.isPresent()")
    Optional<FieldTemplateModel> findById(Long id);

    List<FieldTemplateModel> findByFunctionTemplates(FunctionTemplateModel functionTemplate);

}
