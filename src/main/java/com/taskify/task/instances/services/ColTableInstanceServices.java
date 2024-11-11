package com.taskify.task.instances.services;

import com.taskify.task.instances.dtos.ColTableInstanceDto;
import com.taskify.task.instances.dtos.ColumnInstanceDto;

import java.util.List;

public interface ColTableInstanceServices {

    ColTableInstanceDto createColTableInstance(ColTableInstanceDto colTableInstanceDto);

    ColTableInstanceDto updateColTableInstance(Long id, ColTableInstanceDto colTableInstanceDto);

    void deleteColTableInstance(Long id);

    ColTableInstanceDto getColTableInstanceById(Long id);

    List<ColTableInstanceDto> getColTableInstancesByRowTableById(Long rowTableInstanceId);

}
