package com.taskify.task.instances.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnVariantInstanceDto;

import java.util.List;

public interface ColumnVariantInstanceServices {

    ColumnVariantInstanceDto createColumnVariantInstance(ColumnVariantInstanceDto ColumnVariantInstanceDto);

    PageResponse<ColumnVariantInstanceDto> getAllColumnVariantInstances(int pageNumber, Integer pageSize);

    PageResponse<ColumnVariantInstanceDto> getColumnVariantInstancesByColumnVariantTemplateById(int pageNumber, Integer pageSize, Long columnVariantTemplateId);

    ColumnVariantInstanceDto getColumnVariantInstanceById(Long id);

    ColumnVariantInstanceDto updateColumnVariantInstance(ColumnVariantInstanceDto ColumnVariantInstanceDto);

    boolean deleteColumnVariantInstance(Long id);

    boolean deleteColumnVariantInstancesByColumnVariantTemplateId(Long id);
    
}
