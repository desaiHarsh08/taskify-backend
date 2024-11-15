package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.models.TaskTemplateModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplateModel, Long> {



    @Override
    @Cacheable(value = CacheNames.TASK_TEMPLATE, key = "#id", condition = "#result != null && #result.isPresent()")
    Optional<TaskTemplateModel> findById(Long id);

    Optional<TaskTemplateModel> findByTitle(String title);

}
