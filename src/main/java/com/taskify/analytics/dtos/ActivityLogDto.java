package com.taskify.analytics.dtos;

import com.taskify.common.constants.ActionType;
import com.taskify.common.constants.ResourceType;
import com.taskify.user.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {

    private Long id;

    private ResourceType resourceType = ResourceType.TASK;

    private ActionType actionType = ActionType.CREATE;

    private Long parentCompanyId;

    private Long customerId;

    private Long userId;

    private Long taskInstanceId;

    private Long functionInstanceId;

    private Long fieldInstanceId;

    private Long columnInstanceId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
