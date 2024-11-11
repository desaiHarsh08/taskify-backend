package com.taskify.task.instances.services;

import com.taskify.task.instances.dtos.RowTableInstanceDto;

import java.util.List;

public interface RowTableInstanceServices {

    RowTableInstanceDto createRowTableInstance(RowTableInstanceDto rowTableInstanceDto);

    RowTableInstanceDto updateRowTableInstance(Long id, RowTableInstanceDto rowTableInstanceDto);

    void deleteRowTableInstance(Long id);

    RowTableInstanceDto getRowTableInstanceById(Long id);

    List<RowTableInstanceDto> getRowTableInstancesByColumnInstanceId(Long columnInstanceId);

}
