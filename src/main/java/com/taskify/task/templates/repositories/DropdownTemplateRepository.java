package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.common.constants.DropdownLevel;
import com.taskify.task.templates.models.*;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DropdownTemplateRepository extends JpaRepository<DropdownTemplateModel, Long> {

    @Override
    @Cacheable(value = CacheNames.DROPDOWN_TEMPLATE, key = "#id")
    Optional<DropdownTemplateModel> findById(Long id);

    List<DropdownTemplateModel> findByGroup(String group);

    List<DropdownTemplateModel> findByLevel(DropdownLevel level);

    Optional<DropdownTemplateModel> findByIdAndTaskTemplate(Long id, TaskTemplateModel taskTemplate);


    DropdownTemplateModel findByGroupAndLevelAndTaskTemplateAndValue(String group, DropdownLevel level, TaskTemplateModel taskTemplate, String value);

    DropdownTemplateModel findByGroupAndLevelAndFunctionTemplateAndValue(String group, DropdownLevel level, FunctionTemplateModel functionTemplate, String value);

    DropdownTemplateModel findByGroupAndLevelAndColumnTemplateAndValue(String group, DropdownLevel level, ColumnTemplateModel columnTemplateModel, String value);

    List<DropdownTemplateModel> findByTaskTemplate(TaskTemplateModel taskTemplate);

    List<DropdownTemplateModel> findByFunctionTemplate(FunctionTemplateModel functionTemplate);

    List<DropdownTemplateModel> findByColumnTemplate(ColumnTemplateModel columnTemplate);

    @Modifying
    @Transactional
    @Query("DELETE FROM DropdownTemplateModel dt WHERE dt.taskTemplate.id = :taskTemplateId")
    int deleteByTaskTemplateId(Long taskTemplateId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DropdownTemplateModel dt WHERE dt.functionTemplate.id = :functionTemplateId")
    int deleteByFunctionTemplateId(Long functionTemplateId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DropdownTemplateModel dt WHERE dt.columnTemplate.id = :columnTemplateId")
    int deleteByColumnTemplateId(Long columnTemplateId);

}
