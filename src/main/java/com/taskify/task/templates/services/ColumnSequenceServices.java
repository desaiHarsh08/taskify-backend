package com.taskify.task.templates.services;

import com.taskify.task.templates.dtos.ColumnSequenceDto;

import java.util.List;

public interface ColumnSequenceServices {

    ColumnSequenceDto createSequence(ColumnSequenceDto columnSequenceDto);

    List<ColumnSequenceDto> getSequencesByFieldTemplate(Long fieldTemplateId);

    ColumnSequenceDto updateSequence(Long id, ColumnSequenceDto columnSequenceDto);

    void deleteSequence(Long id);

}
