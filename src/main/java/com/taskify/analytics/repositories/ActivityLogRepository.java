package com.taskify.analytics.repositories;

import com.taskify.analytics.models.ActivityLogModel;
import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.ResourceType;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.models.ParentCompanyModel;
import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.user.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogModel, Long> {

    Page<ActivityLogModel> findByResourceType(Pageable pageable, ResourceType resourceType);

    Page<ActivityLogModel> findByActionType(Pageable pageable, ActionType actionType);

    @Query("SELECT a FROM ActivityLogModel a WHERE YEAR(a.createdAt) = :year AND MONTH(a.createdAt) = :month")
    Page<ActivityLogModel> findByYearAndMonth(Pageable pageable, @Param("year") int year, @Param("month") int month);

    Page<ActivityLogModel> findByCreatedAt(Pageable pageable, LocalDateTime createdAt);

    Page<ActivityLogModel> findByUser(Pageable pageable, UserModel user);

    Page<ActivityLogModel> findByTaskInstance(Pageable pageable, TaskInstanceModel taskInstance);

    Page<ActivityLogModel> findByFunctionInstance(Pageable pageable, FunctionInstanceModel functionInstance);

    Page<ActivityLogModel> findByFieldInstance(Pageable pageable, FieldInstanceModel fieldInstance);

    Page<ActivityLogModel> findByColumnInstance(Pageable pageable, ColumnInstanceModel columnInstance);

}
