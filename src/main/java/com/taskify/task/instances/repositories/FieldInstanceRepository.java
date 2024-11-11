package com.taskify.task.instances.repositories;

import com.taskify.task.instances.models.FieldInstanceModel;
import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.templates.models.FieldTemplateModel;
import com.taskify.user.models.UserModel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FieldInstanceRepository extends JpaRepository<FieldInstanceModel, Long> {

    Page<FieldInstanceModel> findByFieldTemplate(Pageable pageable, FieldTemplateModel fieldTemplate);

    List<FieldInstanceModel> findByFunctionInstance(FunctionInstanceModel functionInstance);

    Page<FieldInstanceModel> findByCreatedByUser(Pageable pageable, UserModel createdByUser);

    Page<FieldInstanceModel> findByClosedByUser(Pageable pageable, UserModel closedByUser);

    Page<FieldInstanceModel> findByCreatedAt(Pageable pageable, LocalDateTime createdAt);

    Page<FieldInstanceModel> findByUpdatedAt(Pageable pageable, LocalDateTime updatedAt);

    Page<FieldInstanceModel> findByClosedAt(Pageable pageable, LocalDateTime closedAt);

    @Modifying
    @Transactional
    @Query("DELETE FROM FieldInstanceModel f WHERE f.fieldTemplate.id = :fieldTemplateId")
    int deleteByFieldTemplateId(Long fieldTemplateId);

}
