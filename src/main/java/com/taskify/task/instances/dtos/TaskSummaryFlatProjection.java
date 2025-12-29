package com.taskify.task.instances.dtos;

public interface TaskSummaryFlatProjection {

    Long getId();
    Long getTaskTemplateId();
    String getAbbreviation();
    Long getCustomerId();
    Long getFunctionId();
    String getPriorityType();
    java.time.LocalDateTime getClosedAt();
    java.time.LocalDateTime getUpdatedAt();
    java.time.LocalDateTime getReceiptNoteCreatedAt();

    String getJobNumber();
    String getPumpMake();
    String getPumpType();
    String getStage();
    String getSerialNumber();
    String getMotorMake();
    String getHp();
    String getVolts();
    String getPhase();
}
