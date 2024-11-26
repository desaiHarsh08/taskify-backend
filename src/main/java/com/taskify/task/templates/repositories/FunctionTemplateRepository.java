package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.common.constants.DepartmentType;
import com.taskify.common.constants.FunctionTemplateType;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FunctionTemplateRepository extends JpaRepository<FunctionTemplateModel, Long> {

    @Override
    @Cacheable(value = CacheNames.FUNCTION_TEMPLATE, key = "#id", condition = "#result != null && #result.isPresent()")
    Optional<FunctionTemplateModel> findById(Long id);

    Optional<FunctionTemplateModel> findByTitle(String title);

    Optional<FunctionTemplateModel> findByTitleAndDepartment(String title, DepartmentType department);

    List<FunctionTemplateModel> findByType(FunctionTemplateType type);

    List<FunctionTemplateModel> findByTaskTemplates(TaskTemplateModel taskTemplate);

    @Modifying
    @Transactional
    @Query("DELETE FROM FunctionTemplateModel ft WHERE :taskTemplateId MEMBER OF ft.taskTemplates")
    int deleteByTaskTemplateId(Long taskTemplateId);


    @Query("SELECT ft FROM FunctionTemplateModel ft WHERE ft.id IN :ids")
    List<FunctionTemplateModel> findByIds(@Param("ids") List<Long> ids);

}
