//package com.taskify.task.instances.repositories;
//
//import com.taskify.common.constants.PriorityType;
//import com.taskify.stakeholders.models.CustomerModel;
//import com.taskify.task.instances.dtos.TaskSummaryDto;
//import com.taskify.task.instances.models.TaskInstanceModel;
//import com.taskify.task.templates.models.DropdownTemplateModel;
//import com.taskify.task.templates.models.TaskTemplateModel;
//import com.taskify.user.models.UserModel;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//public interface TaskInstanceRepository extends JpaRepository<TaskInstanceModel, Long> {
//
//    List<TaskInstanceModel> findByCustomerAndIsArchived(CustomerModel customer, boolean isArchived);
//
//    Page<TaskInstanceModel> findByTaskTemplateAndIsArchived(Pageable pageable, TaskTemplateModel taskTemplate, boolean isArchived);
//
//    Page<TaskInstanceModel> findByPriorityTypeAndIsArchived(Pageable pageable, PriorityType priorityType, boolean isArchived);
//
//    Page<TaskInstanceModel> findByCreatedByUserAndIsArchived(Pageable pageable, UserModel createdByUser, boolean isArchived);
//
//    Page<TaskInstanceModel> findByClosedByUserAndIsArchived(Pageable pageable, UserModel closedByUser, boolean isArchived);
//
//    Page<TaskInstanceModel> findByCreatedAtAndIsArchived(Pageable pageable, LocalDateTime createdAt, boolean isArchived);
//
//    Page<TaskInstanceModel> findByUpdatedAtAndIsArchived(Pageable pageable, LocalDateTime updatedAt, boolean isArchived);
//
//    Page<TaskInstanceModel> findByClosedAtAndIsArchived(Pageable pageable, LocalDateTime closedAt, boolean isArchived);
//
//    Page<TaskInstanceModel> findByAssignedToUserAndIsArchived(Pageable pageable, UserModel assignedToUser, boolean isArchived);
//
//    List<TaskInstanceModel> findAllByAbbreviationContainingIgnoreCaseAndIsArchived(String abbreviation, boolean isArchived);
//
//
//    @Query("SELECT t FROM TaskInstanceModel t WHERE (:isClosed = true AND t.closedAt IS NOT NULL) OR (:isClosed = false AND t.closedAt IS NULL)")
//    Page<TaskInstanceModel> findByIsClosedAndIsArchived(Pageable pageable, @Param("isClosed") boolean isClosed, boolean isArchived);
//
//
//    @Query("SELECT t FROM TaskInstanceModel t WHERE EXTRACT(YEAR FROM t.createdAt) = :year AND EXTRACT(MONTH FROM t.createdAt) = :month ORDER BY t.createdAt DESC")
//    List<TaskInstanceModel> findTasksByYearAndMonthAndIsArchived(@Param("year") int year, @Param("month") int month, boolean isArchived);
//
//    @Query("SELECT t FROM TaskInstanceModel t JOIN FunctionInstanceModel f ON t.id = f.taskInstance.id WHERE f.dueDate < CURRENT_TIMESTAMP AND f.closedAt IS NULL")
//    Page<TaskInstanceModel> findTaskInstancesByOverdueAndIsArchived(Pageable pageable, boolean isArchived);
//
//
//    @Query("DELETE FROM TaskInstanceModel t WHERE t.taskTemplate.id = :taskTemplateId")
//    int deleteByTaskTemplateId(@Param("taskTemplateId") Long taskTemplateId);
//
//    @Query("DELETE FROM TaskInstanceModel t WHERE t.dropdownTemplate.id = :dropdownTemplateId")
//    int deleteByDropdownTemplateId(@Param("dropdownTemplateId") Long dropdownTemplateId);
//
//    Page<TaskInstanceModel> findByDropdownTemplate(Pageable pageable, DropdownTemplateModel dropdownTemplate);
//
//
//    @Query("SELECT t FROM TaskInstanceModel t " +
//            "WHERE (:abbreviation IS NULL OR :abbreviation = '' OR LOWER(TRIM(t.abbreviation)) = LOWER(TRIM(:abbreviation))) "
//            +
//            "AND EXTRACT(YEAR FROM t.createdAt) = :year " +
//            "AND EXTRACT(MONTH FROM t.createdAt) = :month " +
//            "AND EXTRACT(DAY FROM t.createdAt) = :day")
//    Page<TaskInstanceModel> findByAbbreviationAndCreatedDate(
//            @Param("abbreviation") String abbreviation,
//            @Param("year") int year,
//            @Param("month") int month,
//            @Param("day") int day,
//            Pageable pageable);
//
//    Optional<TaskInstanceModel> findByAbbreviation(String abbreviation);
//
//    Page<TaskInstanceModel> findByAbbreviationContainingIgnoreCase(Pageable pageable, String abbreviation);
//
//
////    @Query("""
////    SELECT t FROM TaskInstanceModel t
////    WHERE EXISTS (
////        SELECT 1 FROM FunctionInstanceModel f
////        WHERE f.taskInstance = t
////        AND f.functionTemplateId = 30
////        AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
////    )
////    OR EXISTS (
////        SELECT 1 FROM FunctionInstanceModel f
////        WHERE f.taskInstance = t
////        AND f.functionTemplateId IN (37, 38)
////        AND f.closedAt IS NULL
////    )
////""")
////    Page<TaskInstanceModel> findTaskInstancesByFunctionConditions(Pageable pageable); // Dismantle Due
////
////    @Query("""
////    SELECT t FROM TaskInstanceModel t
////    WHERE EXISTS (
////        SELECT 1 FROM FunctionInstanceModel f
////        WHERE f.taskInstance = t
////        AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
////        AND (
////            (f.functionTemplateId = 49 AND f.closedAt IS NULL)
////            OR f.functionTemplateId = 38
////        )
////    )
////""")
////    Page<TaskInstanceModel> findTaskInstancesByLastFunctionConditions(Pageable pageable); // Estimate Due
////
////
////    @Query("""
////    SELECT t FROM TaskInstanceModel t
////    WHERE EXISTS (
////        SELECT 1 FROM FunctionInstanceModel f
////        WHERE f.taskInstance = t
////        AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
////        AND f.functionTemplateId = 50
////    )
////""")
////    Page<TaskInstanceModel> findTaskInstancesByLastFunctionTemplate50(Pageable pageable); // Pending Approval
////
////
////    @Query("""
////    SELECT t FROM TaskInstanceModel t
////    WHERE EXISTS (
////        SELECT 1 FROM FunctionInstanceModel f
////        WHERE f.taskInstance = t
////        AND f.functionTemplateId = 32
////        AND EXISTS (
////            SELECT 1 FROM FieldInstanceModel fi
////            WHERE fi.functionInstance = f
////            AND fi.fieldTemplateId = 53
////            AND EXISTS (
////                SELECT 1 FROM ColumnInstanceModel ci
////                WHERE ci.fieldInstance = fi
////                AND ci.columnTemplateId = 18
////                AND ci.booleanValue = :status
////            )
////        )
////    )
////""")
////    Page<TaskInstanceModel> findTaskInstancesByFunctionFieldColumnConditions(Pageable pageable, @Param("status") boolean status);
//
//
//
//
//    @Query("""
//        SELECT t FROM TaskInstanceModel t
//        WHERE EXISTS (
//            SELECT 1 FROM FunctionInstanceModel f
//            WHERE f.taskInstance = t
//            AND f.functionTemplate.id = 32
//            AND EXISTS (
//                SELECT 1 FROM FieldInstanceModel fi
//                WHERE fi.functionInstance = f
//                AND fi.fieldTemplate.id = 53
//                AND EXISTS (
//                    SELECT 1 FROM ColumnInstanceModel ci
//                    WHERE ci.fieldInstance = fi
//                    AND ci.columnTemplate.id = 18
//                    AND ci.booleanValue = :status
//                )
//            )
//        )
//    """)
//    Page<TaskInstanceModel> findTaskInstancesByFunctionFieldColumnConditions(Pageable pageable, @Param("status") boolean status);
//
//    @Query("""
//        SELECT t FROM TaskInstanceModel t
//        WHERE EXISTS (
//            SELECT 1 FROM FunctionInstanceModel f
//            WHERE f.taskInstance = t
//            AND f.functionTemplate.id = 30
//            AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
//        )
//        OR EXISTS (
//            SELECT 1 FROM FunctionInstanceModel f
//            WHERE f.taskInstance = t
//            AND f.functionTemplate.id IN (37, 38)
//            AND f.closedAt IS NULL
//        )
//    """)
//    Page<TaskInstanceModel> findTaskInstancesByFunctionConditions(Pageable pageable); // Dismantle Due
//
//    @Query("""
//        SELECT t FROM TaskInstanceModel t
//        WHERE EXISTS (
//            SELECT 1 FROM FunctionInstanceModel f
//            WHERE f.taskInstance = t
//            AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
//            AND (
//                (f.functionTemplate.id = 49 AND f.closedAt IS NULL)
//                OR f.functionTemplate.id = 38
//            )
//        )
//    """)
//    Page<TaskInstanceModel> findTaskInstancesByLastFunctionConditions(Pageable pageable); // Estimate Due
//
//    @Query("""
//        SELECT t FROM TaskInstanceModel t
//        WHERE EXISTS (
//            SELECT 1 FROM FunctionInstanceModel f
//            WHERE f.taskInstance = t
//            AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
//            AND f.functionTemplate.id = 50
//        )
//    """)
//    Page<TaskInstanceModel> findTaskInstancesByLastFunctionTemplate50(Pageable pageable); // Pending Approval
//}
//
//
//
//
//






















package com.taskify.task.instances.repositories;

import com.taskify.common.constants.PriorityType;
import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.task.instances.dtos.TaskSummaryFlatDto;
import com.taskify.task.instances.dtos.TaskSummaryFlatProjection;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.user.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskInstanceRepository extends JpaRepository<TaskInstanceModel, Long> {

    List<TaskInstanceModel> findByCustomerAndIsArchived(CustomerModel customer, boolean isArchived);

    Page<TaskInstanceModel> findByTaskTemplateAndIsArchived(Pageable pageable, TaskTemplateModel taskTemplate, boolean isArchived);

    Page<TaskInstanceModel> findByPriorityTypeAndIsArchived(Pageable pageable, PriorityType priorityType, boolean isArchived);

    Page<TaskInstanceModel> findByIsArchived(boolean isArchived, Pageable pageable);

    Page<TaskInstanceModel> findByCreatedByUserAndIsArchived(Pageable pageable, UserModel createdByUser, boolean isArchived);

    Page<TaskInstanceModel> findByClosedByUserAndIsArchived(Pageable pageable, UserModel closedByUser, boolean isArchived);

    Page<TaskInstanceModel> findByCreatedAtAndIsArchived(Pageable pageable, LocalDateTime createdAt, boolean isArchived);

    Page<TaskInstanceModel> findByUpdatedAtAndIsArchived(Pageable pageable, LocalDateTime updatedAt, boolean isArchived);

    Page<TaskInstanceModel> findByClosedAtAndIsArchived(Pageable pageable, LocalDateTime closedAt, boolean isArchived);

    Page<TaskInstanceModel> findByAssignedToUserAndIsArchived(Pageable pageable, UserModel assignedToUser, boolean isArchived);

    List<TaskInstanceModel> findAllByAbbreviationContainingIgnoreCaseAndIsArchived(String abbreviation, boolean isArchived);

    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE ((:isClosed = true AND t.closedAt IS NOT NULL) OR (:isClosed = false AND t.closedAt IS NULL))
        AND t.isArchived = :isArchived
    """)
    Page<TaskInstanceModel> findByIsClosedAndIsArchived(Pageable pageable, @Param("isClosed") boolean isClosed, @Param("isArchived") boolean isArchived);

    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE EXTRACT(YEAR FROM t.createdAt) = :year
        AND EXTRACT(MONTH FROM t.createdAt) = :month
        AND t.isArchived = :isArchived
        ORDER BY t.createdAt DESC
    """)
    List<TaskInstanceModel> findTasksByYearAndMonthAndIsArchived(@Param("year") int year, @Param("month") int month, @Param("isArchived") boolean isArchived);

    @Query("""
        SELECT t FROM TaskInstanceModel t
        JOIN FunctionInstanceModel f ON t.id = f.taskInstance.id
        WHERE f.dueDate < CURRENT_TIMESTAMP
        AND f.closedAt IS NULL
        AND t.isArchived = :isArchived
    """)
    Page<TaskInstanceModel> findTaskInstancesByOverdueAndIsArchived(Pageable pageable, @Param("isArchived") boolean isArchived);

    @Query("DELETE FROM TaskInstanceModel t WHERE t.taskTemplate.id = :taskTemplateId")
    int deleteByTaskTemplateId(@Param("taskTemplateId") Long taskTemplateId);

    @Query("DELETE FROM TaskInstanceModel t WHERE t.dropdownTemplate.id = :dropdownTemplateId")
    int deleteByDropdownTemplateId(@Param("dropdownTemplateId") Long dropdownTemplateId);

    Page<TaskInstanceModel> findByDropdownTemplate(Pageable pageable, DropdownTemplateModel dropdownTemplate);

    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE (:abbreviation IS NULL OR :abbreviation = '' OR LOWER(TRIM(t.abbreviation)) = LOWER(TRIM(:abbreviation)))
        AND EXTRACT(YEAR FROM t.createdAt) = :year
        AND EXTRACT(MONTH FROM t.createdAt) = :month
        AND EXTRACT(DAY FROM t.createdAt) = :day
        AND t.isArchived = true
    """)
    Page<TaskInstanceModel> findByAbbreviationAndCreatedDate(
            @Param("abbreviation") String abbreviation,
            @Param("year") int year,
            @Param("month") int month,
            @Param("day") int day,
            Pageable pageable);

    Optional<TaskInstanceModel> findByAbbreviation(String abbreviation);

    Page<TaskInstanceModel> findByAbbreviationContainingIgnoreCase(Pageable pageable, String abbreviation);

    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE EXISTS (
            SELECT 1 FROM FunctionInstanceModel f
            WHERE f.taskInstance = t
            AND f.functionTemplate.id = 32
            AND EXISTS (
                SELECT 1 FROM FieldInstanceModel fi
                WHERE fi.functionInstance = f
                AND fi.fieldTemplate.id = 53
                AND EXISTS (
                    SELECT 1 FROM ColumnInstanceModel ci
                    WHERE ci.fieldInstance = fi
                    AND ci.columnTemplate.id = 18
                    AND ci.booleanValue = :status
                )
            )
        )
        AND t.isArchived = :isArchived
    """)
    Page<TaskInstanceModel> findTaskInstancesByFunctionFieldColumnConditions(
            Pageable pageable,
            @Param("status") boolean status,
            @Param("isArchived") boolean isArchived);

    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE (
            EXISTS (
                SELECT 1 FROM FunctionInstanceModel f
                WHERE f.taskInstance = t
                AND f.functionTemplate.id = 30
                AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
            )
            OR EXISTS (
                SELECT 1 FROM FunctionInstanceModel f
                WHERE f.taskInstance = t
                AND f.functionTemplate.id IN (37, 38)
                AND f.closedAt IS NULL
            )
        )
        AND t.isArchived = :isArchived
    """)
    Page<TaskInstanceModel> findTaskInstancesByFunctionConditions(
            Pageable pageable,
            @Param("isArchived") boolean isArchived); // Dismantle Due

    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE EXISTS (
            SELECT 1 FROM FunctionInstanceModel f
            WHERE f.taskInstance = t
            AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
            AND (
                (f.functionTemplate.id = 49 AND f.closedAt IS NULL)
                OR f.functionTemplate.id = 38
            )
        )
        AND t.isArchived = :isArchived
    """)
    Page<TaskInstanceModel> findTaskInstancesByLastFunctionConditions(
            Pageable pageable,
            @Param("isArchived") boolean isArchived); // Estimate Due

    @Query("""
    SELECT t FROM TaskInstanceModel t
    WHERE (
        EXISTS (
            SELECT 1 FROM FunctionInstanceModel f
            WHERE f.taskInstance = t
            AND f.functionTemplate.id = 50
            AND f.closedAt IS NOT NULL
            AND f.id = (
                SELECT MAX(f1.id) FROM FunctionInstanceModel f1
                WHERE f1.taskInstance = t AND f1.functionTemplate.id = 50
            )
        )
        OR NOT EXISTS (
            SELECT 1 FROM FunctionInstanceModel f2
            WHERE f2.taskInstance = t
            AND f2.functionTemplate.id = 32
        )
    )
    AND t.isArchived = :isArchived
""")
    Page<TaskInstanceModel> findTaskInstancesForAwaitingApproval(
            Pageable pageable,
            @Param("isArchived") boolean isArchived
    );




    @Query("""
SELECT t FROM TaskInstanceModel t
WHERE EXISTS (
    SELECT 1 FROM FunctionInstanceModel f
    WHERE f.taskInstance = t
    AND f.createdAt = (
        SELECT MAX(f2.createdAt)
        FROM FunctionInstanceModel f2
        WHERE f2.taskInstance = t
    )
    AND f.functionTemplate.id = 32
    AND EXISTS (
        SELECT 1 FROM FieldInstanceModel fe
        WHERE fe.functionInstance = f
        AND fe.fieldTemplate.id = 53
        AND EXISTS (
            SELECT 1 FROM ColumnInstanceModel c
            WHERE c.fieldInstance = fe
            AND c.columnTemplate.id = 18
            AND c.booleanValue = true
        )
    )
    AND NOT EXISTS (
        SELECT 1 FROM FunctionInstanceModel f3
        WHERE f3.taskInstance = t
        AND f3.functionTemplate.id = 45
    )
)
AND t.isArchived = :isArchived
""")
    Page<TaskInstanceModel> findTaskInstancesForWorkInProgress(
            Pageable pageable,
            @Param("isArchived") boolean isArchived
    );

    @Query("""
SELECT t FROM TaskInstanceModel t
WHERE EXISTS (
    SELECT 1 FROM FunctionInstanceModel f
    WHERE f.taskInstance = t
    AND f.createdAt = (
        SELECT MAX(f2.createdAt)
        FROM FunctionInstanceModel f2
        WHERE f2.taskInstance = t
    )
    AND f.functionTemplate.id = 45
    AND f.closedAt IS NOT NULL
    AND NOT EXISTS (
        SELECT 1 FROM FunctionInstanceModel f3
        WHERE f3.taskInstance = t
        AND f3.functionTemplate.id = 71
    )
)
AND t.isArchived = :isArchived
""")
    Page<TaskInstanceModel> findTaskInstancesForReady(
            Pageable pageable,
            @Param("isArchived") boolean isArchived
    );


    @Query("""
SELECT t FROM TaskInstanceModel t
WHERE EXISTS (
    SELECT 1 FROM FunctionInstanceModel f
    WHERE f.taskInstance = t
    AND f.createdAt = (
        SELECT MAX(f2.createdAt)
        FROM FunctionInstanceModel f2
        WHERE f2.taskInstance = t
    )
    AND f.functionTemplate.id = 71
    AND f.closedAt IS NOT NULL
)
AND t.isArchived = :isArchived
""")
    Page<TaskInstanceModel> findTaskInstancesForPendingBill(
            Pageable pageable,
            @Param("isArchived") boolean isArchived
    );


    @Query("""
SELECT t FROM TaskInstanceModel t
WHERE EXISTS (
    SELECT 1 FROM FunctionInstanceModel f
    WHERE f.taskInstance = t
    AND f.createdAt = (
        SELECT MAX(f2.createdAt)
        FROM FunctionInstanceModel f2
        WHERE f2.taskInstance = t
    )
    AND f.functionTemplate.id = 47
    AND f.closedAt IS NULL
)
AND (
    NOT EXISTS (
        SELECT 1 FROM FunctionInstanceModel r
        WHERE r.taskInstance = t
        AND r.functionTemplate.id = 52
    )
    OR EXISTS (
        SELECT 1 FROM FunctionInstanceModel r
        WHERE r.taskInstance = t
        AND r.functionTemplate.id = 52
        AND r.closedAt IS NULL
    )
)
AND t.isArchived = :isArchived
""")
    Page<TaskInstanceModel> findTaskInstancesForLathe(
            Pageable pageable,
            @Param("isArchived") boolean isArchived
    );







    @Query("""
        SELECT t FROM TaskInstanceModel t
        WHERE EXISTS (
            SELECT 1 FROM FunctionInstanceModel f
            WHERE f.taskInstance = t
            AND f.createdAt = (SELECT MAX(f2.createdAt) FROM FunctionInstanceModel f2 WHERE f2.taskInstance = t)
            AND f.functionTemplate.id = 50
        )
        AND t.isArchived = :isArchived
    """)
    Page<TaskInstanceModel> findTaskInstancesByLastFunctionTemplate50(
            Pageable pageable,
            @Param("isArchived") boolean isArchived); // Pending Approval






//    @Query(
//            value = """
//        SELECT
//            ti.id AS id,
//            ti.task_template_id_fk AS taskTemplateId,
//            ti.abbreviation AS abbreviation,
//            ti.customer_id_fk AS customerId,
//            fi.id AS functionId,
//            ti.priority_type AS priorityType,
//            ti.closed_at AS closedAt,
//            ti.updated_at AS updatedAt,
//            ti.receipt_note_created_at AS receiptNoteCreatedAt,
//
//            MAX(CASE WHEN ct.id = 134 THEN ci.text_value END) AS jobNumber,
//            MAX(CASE WHEN ct.id = 152 THEN ci.text_value END) AS pumpMake,
//            MAX(CASE WHEN ct.id = 135 THEN ci.text_value END) AS pumpType,
//            MAX(CASE WHEN ct.id = 72  THEN ci.text_value END) AS stage,
//            MAX(CASE WHEN ct.id = 153 THEN ci.text_value END) AS serialNumber,
//            MAX(CASE WHEN ct.id = 155 THEN ci.text_value END) AS motorMake,
//            MAX(CASE WHEN ct.id = 73  THEN ci.text_value END) AS hp,
//            MAX(CASE WHEN ct.id = 146 THEN ci.text_value END) AS volts,
//            MAX(CASE WHEN ct.id = 143 THEN ci.text_value END) AS phase
//
//        FROM task_instances ti
//        LEFT JOIN function_instances fi ON fi.task_instances_id_fk = ti.id
//        LEFT JOIN function_templates ft ON ft.id = fi.function_template_id_fk AND ft.id = 30
//        LEFT JOIN field_instances fii ON fii.function_instance_id_fk = fi.id
//        LEFT JOIN field_templates fit ON fit.id = fii.field_template_id_fk AND fit.id = 48
//        LEFT JOIN column_instances ci ON ci.field_instance_id_fk = fii.id
//        LEFT JOIN column_templates ct
//            ON ct.id = ci.column_template_id_fk
//            AND ct.id IN (134,152,135,72,153,155,73,146,143)
//
//        WHERE ti.is_archived = false
//
//        GROUP BY
//            ti.id,
//            ti.task_template_id_fk,
//            ti.abbreviation,
//            ti.customer_id_fk,
//            fi.id,
//            ti.priority_type,
//            ti.closed_at,
//            ti.updated_at,
//            ti.receipt_note_created_at
//
//        ORDER BY ti.created_at DESC
//        """,
//            nativeQuery = true
//    )
//    Page<TaskSummaryFlatDto> findTaskSummaries(Pageable pageable);


    @Query(
            value = """
            SELECT
                ti.id AS id,
                ti.task_template_id_fk AS taskTemplateId,
                ti.abbreviation AS abbreviation,
                ti.customer_id_fk AS customerId,
                fi.id AS functionId,
                ti.priority_type AS priorityType,
                ti.closed_at AS closedAt,
                ti.updated_at AS updatedAt,
                fi.created_at AS receiptNoteCreatedAt,

                MAX(CASE WHEN ct.id = 134 THEN ci.text_value END) AS jobNumber,
                MAX(CASE WHEN ct.id = 152 THEN ci.text_value END) AS pumpMake,
                MAX(CASE WHEN ct.id = 135 THEN ci.text_value END) AS pumpType,
                MAX(CASE WHEN ct.id = 72  THEN ci.text_value END) AS stage,
                MAX(CASE WHEN ct.id = 153 THEN ci.text_value END) AS serialNumber,
                MAX(CASE WHEN ct.id = 155 THEN ci.text_value END) AS motorMake,
                MAX(CASE WHEN ct.id = 73  THEN ci.text_value END) AS hp,
                MAX(CASE WHEN ct.id = 146 THEN ci.text_value END) AS volts,
                MAX(CASE WHEN ct.id = 143 THEN ci.text_value END) AS phase

            FROM (
                SELECT id
                FROM task_instances
                WHERE is_archived = false
                ORDER BY created_at DESC
            ) paged_ti

            JOIN task_instances ti ON ti.id = paged_ti.id
            LEFT JOIN function_instances fi ON fi.task_instances_id_fk = ti.id
            LEFT JOIN function_templates ft ON ft.id = fi.function_template_id_fk AND ft.id = 30
            LEFT JOIN field_instances fii ON fii.function_instance_id_fk = fi.id
            LEFT JOIN field_templates fit ON fit.id = fii.field_template_id_fk AND fit.id = 48
            LEFT JOIN column_instances ci ON ci.field_instance_id_fk = fii.id
            LEFT JOIN column_templates ct
                ON ct.id = ci.column_template_id_fk
                AND ct.id IN (134,152,135,72,153,155,73,146,143)

            GROUP BY
                ti.id,
                ti.task_template_id_fk,
                ti.abbreviation,
                ti.customer_id_fk,
                fi.id,
                ti.priority_type,
                ti.closed_at,
                ti.updated_at,
                fi.created_at

            ORDER BY ti.created_at DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM task_instances
            WHERE is_archived = false
        """,
            nativeQuery = true
    )
    Page<TaskSummaryFlatProjection> findTaskSummaries(Pageable pageable);





    @Query(
            value = """
        SELECT
            ti.id AS id,
            ti.task_template_id_fk AS taskTemplateId,
            ti.abbreviation AS abbreviation,
            ti.customer_id_fk AS customerId,
            fi.id AS functionId,
            ti.priority_type AS priorityType,
            ti.closed_at AS closedAt,
            ti.updated_at AS updatedAt,
            fi.created_at AS receiptNoteCreatedAt,

            MAX(CASE WHEN ct.id = 134 THEN ci.text_value END) AS jobNumber,
            MAX(CASE WHEN ct.id = 152 THEN ci.text_value END) AS pumpMake,
            MAX(CASE WHEN ct.id = 135 THEN ci.text_value END) AS pumpType,
            MAX(CASE WHEN ct.id = 72  THEN ci.text_value END) AS stage,
            MAX(CASE WHEN ct.id = 153 THEN ci.text_value END) AS serialNumber,
            MAX(CASE WHEN ct.id = 155 THEN ci.text_value END) AS motorMake,
            MAX(CASE WHEN ct.id = 73  THEN ci.text_value END) AS hp,
            MAX(CASE WHEN ct.id = 146 THEN ci.text_value END) AS volts,
            MAX(CASE WHEN ct.id = 143 THEN ci.text_value END) AS phase

        FROM task_instances ti
        LEFT JOIN function_instances fi ON fi.task_instances_id_fk = ti.id AND fi.function_template_id_fk = 30
        LEFT JOIN function_templates ft ON ft.id = fi.function_template_id_fk AND ft.id = 30
        LEFT JOIN field_instances fii ON fii.function_instance_id_fk = fi.id
        LEFT JOIN column_instances ci ON ci.field_instance_id_fk = fii.id
        LEFT JOIN column_templates ct
            ON ct.id = ci.column_template_id_fk
            AND ct.id IN (134,152,135,72,153,155,73,146,143)

        WHERE ti.is_archived = false
          AND (
              UPPER(ci.text_value) LIKE CONCAT('%', UPPER(:searchTxt), '%')
              OR UPPER(ti.abbreviation) LIKE CONCAT('%', UPPER(:searchTxt), '%')
              OR ti.customer_id_fk IN :customerIds
          )

        GROUP BY
            ti.id,
            ti.task_template_id_fk,
            ti.abbreviation,
            ti.customer_id_fk,
            fi.id,
            ti.priority_type,
            ti.closed_at,
            ti.updated_at,
            fi.created_at

        ORDER BY ti.created_at DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM task_instances ti
        WHERE ti.is_archived = false
          AND (
              UPPER(ti.abbreviation) LIKE CONCAT('%', UPPER(:searchTxt), '%')
              OR ti.customer_id_fk IN :customerIds
          )
    """,
            nativeQuery = true
    )
    Page<TaskSummaryFlatProjection> searchTaskSummaries(
            @Param("searchTxt") String searchTxt,
            @Param("customerIds") List<Long> customerIds,
            Pageable pageable
    );



}
