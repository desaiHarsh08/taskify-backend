package com.taskify.task.instances.dtos;

import com.taskify.common.constants.PriorityType;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface TaskSummaryMapper {

    default TaskSummaryDto toDto(TaskSummaryFlatProjection p) {
        if (p == null) return null;

        PumpDetailsDto pumpDetails = new PumpDetailsDto(
                p.getPumpMake(),
                p.getPumpType(),
                p.getStage(),
                p.getSerialNumber(),
                p.getMotorMake(),
                p.getHp(),
                p.getVolts(),
                p.getPhase()
        );

        return new TaskSummaryDto(
                p.getId(),
                p.getTaskTemplateId(),
                p.getAbbreviation(),
                p.getJobNumber(),
                p.getCustomerId(),
                p.getFunctionId(),
                PriorityType.valueOf(p.getPriorityType()),
                p.getClosedAt(),
                p.getUpdatedAt(),
                p.getReceiptNoteCreatedAt(),
                pumpDetails
        );
    }
}
