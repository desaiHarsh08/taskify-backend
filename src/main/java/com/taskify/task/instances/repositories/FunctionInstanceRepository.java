package com.taskify.task.instances.repositories;

import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.user.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FunctionInstanceRepository extends JpaRepository<FunctionInstanceModel, Long> {

    List<FunctionInstanceModel> findByTaskInstanceOrderByIdDesc(TaskInstanceModel taskInstance);

    Page<FunctionInstanceModel> findByFunctionTemplate(Pageable pageable, FunctionTemplateModel functionTemplate);

    Page<FunctionInstanceModel> findByCreatedByUser(Pageable pageable, UserModel createdByUser);

    Page<FunctionInstanceModel> findByClosedByUser(Pageable pageable, UserModel closedByUser);

    Page<FunctionInstanceModel> findByCreatedAt(Pageable pageable, LocalDateTime createdAt);

    @Query("SELECT f FROM FunctionInstanceModel f " +
            "WHERE f.taskInstance = :taskInstance " +
            "AND f.closedAt IS NULL " +
            "ORDER BY f.id DESC")
    List<FunctionInstanceModel> findByTaskInstanceLatest(TaskInstanceModel taskInstance);

    Page<FunctionInstanceModel> findByClosedAt(Pageable pageable, LocalDateTime closedAt);

    Page<FunctionInstanceModel> findByUpdatedAt(Pageable pageable, LocalDateTime updatedAt);

    Page<FunctionInstanceModel> findByDropdownTemplate(Pageable pageable, DropdownTemplateModel dropdownTemplate);

}
