package com.taskify.user.dtos;

import com.taskify.common.constants.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    private Long id;

    private PermissionType type;

    private Long viewTaskId;

}
