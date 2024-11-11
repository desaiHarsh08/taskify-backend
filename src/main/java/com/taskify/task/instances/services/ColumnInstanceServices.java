package com.taskify.task.instances.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnInstanceDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ColumnInstanceServices {

    ColumnInstanceDto createColumnInstance(ColumnInstanceDto columnInstanceDto);

    boolean uploadFiles(ColumnInstanceDto columnInstanceDto, MultipartFile[] files);

    PageResponse<ColumnInstanceDto> getAllColumnInstances(int pageNumber, Integer pageSize);

    byte[] readFileAsBytes(String filePath);

    boolean deleteFile(String filePath);

    ColumnInstanceDto getColumnInstanceById(Long id);

    PageResponse<ColumnInstanceDto> getColumnInstancesByColumnTemplateById(int pageNumber, Integer pageSize, Long columnTemplateId);


    List<ColumnInstanceDto> getColumnInstancesByFieldInstanceId(Long fieldInstanceId);

    ColumnInstanceDto updateColumnInstance(ColumnInstanceDto columnInstanceDto);

    boolean deleteColumnInstance(Long id);

    boolean deleteColumnInstancesByColumnTemplateId(Long columnTemplateId);

    boolean deleteColumnInstancesByDropdownTemplateId(Long dropdownTemplateId);
    
}
