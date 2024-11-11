package com.taskify.task.templates.services;


import com.taskify.common.constants.ColumnType;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;

import java.util.List;

public interface ColumnMetadataTemplateServices {

    ColumnMetadataTemplateModel createColumnMetadataTemplate(ColumnMetadataTemplateModel columnMetadataTemplateModel);

    List<ColumnMetadataTemplateModel> getAllColumnMetadataTemplates();

    List<ColumnMetadataTemplateModel> getColumnMetadataTemplatesByType(ColumnType type);

    ColumnMetadataTemplateModel getColumnMetadataTemplateByTypeAndAcceptingMultipleFiles(ColumnType type, boolean acceptimgMultipleFiles);

    ColumnMetadataTemplateModel getColumnMetadataTemplateById(Long id);

    ColumnMetadataTemplateModel updateColumnMetadataTemplate(ColumnMetadataTemplateModel columnMetadataTemplateModel);

    boolean deleteColumnMetadataTemplate(Long id);
    
}
