package com.taskify.task.templates.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.ColumnVariantTemplateModel;
import com.taskify.task.templates.models.NextFollowUpColumnTemplateModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NextFollowUpColumnTemplateRepository extends JpaRepository<NextFollowUpColumnTemplateModel, Long> {


    List<NextFollowUpColumnTemplateModel> findByColumnTemplate(ColumnTemplateModel columnTemplate);

    List<NextFollowUpColumnTemplateModel> findByColumnVariantTemplate(ColumnVariantTemplateModel columnVariantTemplate);

}
