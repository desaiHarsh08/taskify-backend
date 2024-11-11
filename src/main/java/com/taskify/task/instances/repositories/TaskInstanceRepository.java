package com.taskify.task.instances.repositories;

import com.taskify.common.constants.PriorityType;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.user.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskInstanceRepository extends JpaRepository<TaskInstanceModel, Long> {

    List<TaskInstanceModel> findByCustomer(CustomerModel customer);

    Page<TaskInstanceModel> findByTaskTemplate(Pageable pageable, TaskTemplateModel taskTemplate);

    Page<TaskInstanceModel> findByPriorityType(Pageable pageable, PriorityType priorityType);

    Page<TaskInstanceModel> findByCreatedByUser(Pageable pageable, UserModel createdByUser);

    Page<TaskInstanceModel> findByClosedByUser(Pageable pageable, UserModel closedByUser);

    Page<TaskInstanceModel> findByCreatedAt(Pageable pageable, LocalDateTime createdAt);

    Page<TaskInstanceModel> findByUpdatedAt(Pageable pageable, LocalDateTime updatedAt);

    Page<TaskInstanceModel> findByClosedAt(Pageable pageable, LocalDateTime closedAt);

    @Query("SELECT t FROM TaskInstanceModel t WHERE EXTRACT(YEAR FROM t.createdAt) = :year AND EXTRACT(MONTH FROM t.createdAt) = :month ORDER BY t.createdAt DESC")
    List<TaskInstanceModel> findTasksByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT t FROM TaskInstanceModel t JOIN FunctionInstanceModel f ON t.id = f.taskInstance.id WHERE f.dueDate < CURRENT_TIMESTAMP AND f.closedAt = null")
    Page<TaskInstanceModel> findTaskInstancesByOverdue(Pageable pageable);

    @Query("DELETE FROM TaskInstanceModel t WHERE t.taskTemplate.id = :taskTemplateId")
    int deleteByTaskTemplateId(@Param("taskTemplateId") Long taskTemplateId);

    @Query("DELETE FROM TaskInstanceModel t WHERE t.dropdownTemplate.id = :dropdownTemplateId")
    int deleteByDropdownTemplateId(@Param("dropdownTemplateId") Long dropdownTemplateId);

}
