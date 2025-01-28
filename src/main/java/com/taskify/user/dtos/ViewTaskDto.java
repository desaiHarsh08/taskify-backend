package com.taskify.user.dtos;

import com.taskify.common.constants.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewTaskDto {
    private Long id;

    private DepartmentType taskType;

    private Long userId;

    private List<PermissionDto> permissions = new ArrayList<>();
}
