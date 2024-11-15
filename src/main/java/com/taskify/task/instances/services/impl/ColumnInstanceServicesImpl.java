package com.taskify.task.instances.services.impl;

import com.taskify.common.constants.ColumnType;
import com.taskify.common.constants.ResourceType;
import com.taskify.common.exceptions.ResourceNotFoundException;
import com.taskify.common.utils.Helper;
import com.taskify.common.utils.PageResponse;
import com.taskify.task.instances.dtos.ColumnInstanceDto;
import com.taskify.task.instances.dtos.ColumnVariantInstanceDto;
import com.taskify.task.instances.dtos.RowTableInstanceDto;
import com.taskify.task.instances.models.*;
import com.taskify.task.instances.repositories.ColumnInstanceRepository;
import com.taskify.task.instances.repositories.FieldInstanceRepository;
import com.taskify.task.instances.repositories.FunctionInstanceRepository;
import com.taskify.task.instances.repositories.TaskInstanceRepository;
import com.taskify.task.instances.services.ColumnInstanceServices;
import com.taskify.task.instances.services.ColumnVariantInstanceServices;
import com.taskify.task.instances.services.RowTableInstanceServices;
import com.taskify.task.templates.models.ColumnMetadataTemplateModel;
import com.taskify.task.templates.models.ColumnTemplateModel;
import com.taskify.task.templates.models.DropdownTemplateModel;
import com.taskify.task.templates.models.TaskTemplateModel;
import com.taskify.task.templates.repositories.ColumnMetadataTemplateRepository;
import com.taskify.task.templates.repositories.ColumnTemplateRepository;
import com.taskify.task.templates.repositories.DropdownTemplateRepository;
import com.taskify.task.templates.repositories.TaskTemplateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnInstanceServicesImpl implements ColumnInstanceServices {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnTemplateRepository columnTemplateRepository;

    @Autowired
    private ColumnInstanceRepository columnInstanceRepository;

    @Autowired
    private ColumnVariantInstanceServices columnVariantInstanceServices;

    @Autowired
    private FieldInstanceRepository fieldInstanceRepository;

    @Autowired
    private DropdownTemplateRepository dropdownTemplateRepository;

    @Autowired
    private FunctionInstanceRepository functionInstanceRepository;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private ColumnMetadataTemplateRepository columnMetadataTemplateRepository;

    @Autowired
    private RowTableInstanceServices rowTableInstanceServices;

    @Override
    public ColumnInstanceDto createColumnInstance(ColumnInstanceDto columnInstanceDto) {
        // Step 1: Fetch the templates
        ColumnTemplateModel columnTemplateModel = this.columnTemplateRepository.findById(columnInstanceDto.getColumnTemplateId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnInstanceDto.getColumnTemplateId(), true)
        );
        ColumnMetadataTemplateModel columnMetadataTemplateModel = this.columnMetadataTemplateRepository.findById(columnTemplateModel.getColumnMetadataTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_METADATA, "id", columnTemplateModel.getColumnMetadataTemplate().getId(), true)
        );
        columnTemplateModel.setColumnMetadataTemplate(columnMetadataTemplateModel);
        // Step 2: Create the new column_instance
        ColumnInstanceModel newColumnInstanceModel = this.modelMapper.map(columnInstanceDto, ColumnInstanceModel.class);
        newColumnInstanceModel.setColumnTemplate(columnTemplateModel);
        newColumnInstanceModel.setFieldInstance(new FieldInstanceModel(columnInstanceDto.getFieldInstanceId()));
        if (columnInstanceDto.getDropdownTemplateId() != null) {
            newColumnInstanceModel.setDropdownTemplate(new DropdownTemplateModel(columnInstanceDto.getDropdownTemplateId()));
        }
        // Step 3: Save the new column_instance
        newColumnInstanceModel = this.columnInstanceRepository.save(newColumnInstanceModel);
        // Step 4: Create the column_variant (only for CHECKBOX, TABLE)
        if (columnMetadataTemplateModel.getType().equals(ColumnType.CHECKBOX) || columnMetadataTemplateModel.getType().equals(ColumnType.TABLE)) {
            for (ColumnVariantInstanceDto columnVariantInstanceDto: columnInstanceDto.getColumnVariantInstances()) {
                columnVariantInstanceDto.setColumnInstanceId(newColumnInstanceModel.getId());
                this.columnVariantInstanceServices.createColumnVariantInstance(columnVariantInstanceDto);
            }

        }
        // Step 5: Row tables
        for (RowTableInstanceDto rowTableInstanceDto: columnInstanceDto.getRowTableInstances()) {
            rowTableInstanceDto.setColumnInstanceId(newColumnInstanceModel.getId());
            this.rowTableInstanceServices.createRowTableInstance(rowTableInstanceDto);

        }

        return this.columnInstanceModelToDto(newColumnInstanceModel);
    }

    @Override
    public boolean uploadFiles(ColumnInstanceDto columnInstanceDto, MultipartFile[] files) {
        FieldInstanceModel fieldInstanceModel = this.fieldInstanceRepository.findById(columnInstanceDto.getFieldInstanceId()).orElseThrow(
                () -> new IllegalArgumentException("No field_instance exist for id: " + columnInstanceDto.getFieldInstanceId()));

        FunctionInstanceModel functionInstanceModel = this.functionInstanceRepository.findById(fieldInstanceModel.getFunctionInstance().getId()).orElseThrow(
                () -> new IllegalArgumentException("No function_instance exist for id: " + fieldInstanceModel.getFunctionInstance().getId()));

        TaskInstanceModel taskInstanceModel = this.taskInstanceRepository.findById(functionInstanceModel.getTaskInstance().getId()).orElseThrow(
                () -> new IllegalArgumentException("No task_instance exist for id: " + functionInstanceModel.getTaskInstance().getId())
        );

        LocalDateTime date = LocalDateTime.now();

        // Create the directory path
        String directoryPath = this.getFilePath(columnInstanceDto);

        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        System.out.println("Attempting for Creating directory: -");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + directoryPath);
            }
        }
        for (MultipartFile file : files) {
            // Create filename
            String fileNamePrefix = taskInstanceModel.getAbbreviation() + "_" +
                    functionInstanceModel.getId() + "_" +
                    columnInstanceDto.getId() + "_" +
                    date.getYear() + "-" + date.getMonth() + 1 + "-" + date.getDayOfMonth() + "-" +
                    (date.getHour() + 1) + "-" + (date.getMinute() + 1) + "-" + (date.getSecond() + 1);

            // Extract the extension from the original file name
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            // Append the extension to the fileNamePrefix

            String fileName = fileNamePrefix + extension;

            System.out.println("saving...");
            this.saveFile(file, directoryPath, fileName);
        }

        return true;
    }

    private void saveFile(MultipartFile file, String fileDirectory, String fileName) {
        System.out.println(fileDirectory);
        // Define the directory path adjacent to the root directory
        try {
            Path directoryPath = Paths.get(fileDirectory);

            // Ensure the directory exists
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            System.out.println("directory exist, and now storing...");

            // Create the path for the file to be stored, using the provided fileName
            Path filePath = directoryPath.resolve(fileName);

            System.out.println("Full file path: " + filePath.toAbsolutePath());

            // Save the file to the defined directory
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            e.printStackTrace();
             throw new ResourceNotFoundException(ResourceType.FILE, "file", fileName, false);
        }
    }

    @Override
    public PageResponse<ColumnInstanceDto> getAllColumnInstances(int pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public byte[] readFileAsBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File not found or is not a valid file: " + filePath);
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new ResourceNotFoundException(ResourceType.FILE, "file", filePath, false);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        } else {
            throw new ResourceNotFoundException(ResourceType.FILE, "file", filePath, false);
        }
    }

    @Override
    public PageResponse<ColumnInstanceDto> getColumnInstancesByColumnTemplateById(int pageNumber, Integer pageSize, Long columnTemplateId) {
        Pageable pageable = Helper.getPageable(pageNumber, pageSize);
        Page<ColumnInstanceModel> pageColumnInstance = this.columnInstanceRepository.findByColumnTemplate(pageable, new ColumnTemplateModel(columnTemplateId));
        List<ColumnInstanceModel> columnInstanceModels = pageColumnInstance.getContent();

        return new PageResponse<>(
                pageNumber,
                pageSize,
                pageColumnInstance.getTotalPages(),
                pageColumnInstance.getTotalElements(),
                columnInstanceModels.stream().map(this::columnInstanceModelToDto).collect(Collectors.toList())
        );
    }

    @Override
    public List<ColumnInstanceDto> getColumnInstancesByFieldInstanceId(Long fieldInstanceId) {
        List<ColumnInstanceModel> columnInstanceModels = this.columnInstanceRepository.findByFieldInstance(new FieldInstanceModel(fieldInstanceId));
        if (columnInstanceModels.isEmpty()) {
            return new ArrayList<>();
        }

        return columnInstanceModels.stream().map(this::columnInstanceModelToDto).collect(Collectors.toList());
    }

    @Override
    public ColumnInstanceDto getColumnInstanceById(Long id) {
        ColumnInstanceModel foundColumnInstanceModel = this.columnInstanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", id, false)
        );

        return this.columnInstanceModelToDto(foundColumnInstanceModel);
    }

    @Override
    public ColumnInstanceDto updateColumnInstance(ColumnInstanceDto columnInstanceDto) {
        ColumnInstanceModel foundColumnInstanceModel = this.columnInstanceRepository.findById(columnInstanceDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnInstanceDto.getId(), false)
        );

        ColumnTemplateModel columnTemplateModel = this.columnTemplateRepository.findById(foundColumnInstanceModel.getColumnTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnInstanceDto.getColumnTemplateId(), true)
        );
        ColumnMetadataTemplateModel columnMetadataTemplateModel = this.columnMetadataTemplateRepository.findById(columnTemplateModel.getColumnMetadataTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_METADATA, "id", columnTemplateModel.getColumnMetadataTemplate().getId(), true)
        );
        switch (columnMetadataTemplateModel.getType()) {
            case TEXT:
            case LARGE_TEXT:
            case TABLE:
            case EMAIL:
            case PHONE:
                foundColumnInstanceModel.setTextValue(columnInstanceDto.getTextValue());
                break;
            case DATE:
                foundColumnInstanceModel.setDateValue(columnInstanceDto.getDateValue());
                break;
            case BOOLEAN:
                foundColumnInstanceModel.setBooleanValue(columnInstanceDto.getBooleanValue());
                break;
            case AMOUNT:
            case NUMBER:
                foundColumnInstanceModel.setNumberValue(columnInstanceDto.getNumberValue());
                break;
        }
        foundColumnInstanceModel = this.columnInstanceRepository.save(foundColumnInstanceModel);
        for (ColumnVariantInstanceDto columnVariantInstanceDto: columnInstanceDto.getColumnVariantInstances()) {
            this.columnVariantInstanceServices.updateColumnVariantInstance(columnVariantInstanceDto);
        }
        // Row tables
        for (RowTableInstanceDto rowTableInstanceDto: columnInstanceDto.getRowTableInstances()) {
            if (rowTableInstanceDto.getId() == null){
                rowTableInstanceDto.setColumnInstanceId(foundColumnInstanceModel.getId());
                this.rowTableInstanceServices.createRowTableInstance(rowTableInstanceDto);
            }
            else  {
                this.rowTableInstanceServices.updateRowTableInstance(rowTableInstanceDto.getId(), rowTableInstanceDto);
            }


        }

        return this.columnInstanceModelToDto(foundColumnInstanceModel);
    }

    @Override
    public boolean deleteColumnInstance(Long id) {
        // Step 1: Check for column_instance exist
        ColumnInstanceDto foundColumnInstanceDto = this.getColumnInstanceById(id);
        // Step 2: Delete all the column_variants, if exist
        for (ColumnVariantInstanceDto columnVariantInstanceDto: foundColumnInstanceDto.getColumnVariantInstances()) {
            this.columnVariantInstanceServices.deleteColumnVariantInstance(columnVariantInstanceDto.getId());
        }
        // Step 3: Delete the column_instance
        this.columnInstanceRepository.deleteById(id);

        return true;
    }

    @Override
    public boolean deleteColumnInstancesByColumnTemplateId(Long columnTemplateId) {
        Pageable pageable = Helper.getPageable(1, null);
        Page<ColumnInstanceModel>  pageColumnInstance = this.columnInstanceRepository.findByColumnTemplate(pageable, new ColumnTemplateModel(columnTemplateId));
        for (ColumnInstanceModel columnInstanceModel: pageColumnInstance.getContent()) {
            this.deleteColumnInstance(columnInstanceModel.getId());
        }

        for (int i = 2; i < pageColumnInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i, null);
            pageColumnInstance = this.columnInstanceRepository.findByColumnTemplate(pageable, new ColumnTemplateModel(columnTemplateId));
            for (ColumnInstanceModel columnInstanceModel: pageColumnInstance.getContent()) {
                this.deleteColumnInstance(columnInstanceModel.getId());
            }
        }

        return true;
    }

    @Override
    public boolean deleteColumnInstancesByDropdownTemplateId(Long dropdownTemplateId) {
        Pageable pageable = Helper.getPageable(1, null);
        Page<ColumnInstanceModel>  pageColumnInstance = this.columnInstanceRepository.findByDropdownTemplate(pageable, new DropdownTemplateModel(dropdownTemplateId));
        for (ColumnInstanceModel columnInstanceModel: pageColumnInstance.getContent()) {
            this.deleteColumnInstance(columnInstanceModel.getId());
        }

        for (int i = 2; i < pageColumnInstance.getTotalPages(); i++) {
            pageable = Helper.getPageable(i, null);
            pageColumnInstance = this.columnInstanceRepository.findByDropdownTemplate(pageable, new DropdownTemplateModel(dropdownTemplateId));
            for (ColumnInstanceModel columnInstanceModel: pageColumnInstance.getContent()) {
                this.deleteColumnInstance(columnInstanceModel.getId());
            }
        }

        return true;
    }

    private String getFilePath(ColumnInstanceDto columnInstanceDto) {
        FieldInstanceModel fieldInstanceModel = this.fieldInstanceRepository.findById(columnInstanceDto.getFieldInstanceId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnInstanceDto.getFieldInstanceId(), false)
        );
        FunctionInstanceModel functionInstanceModel = this.functionInstanceRepository.findById(fieldInstanceModel.getFunctionInstance().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.FUNCTION, "id", fieldInstanceModel.getFunctionInstance(), false)
        );
        TaskInstanceModel foundTaskInstanceModel = this.taskInstanceRepository.findById(functionInstanceModel.getTaskInstance().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", functionInstanceModel.getTaskInstance().getId(), false)
        );
        TaskTemplateModel foundTaskTemplateModel = this.taskTemplateRepository.findById(foundTaskInstanceModel.getTaskTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.TASK, "id", foundTaskInstanceModel.getTaskTemplate().getId(), true)
        );

        // Create the directory path

        return this.uploadDir + "/" + foundTaskTemplateModel.getTitle() + "/" +
                "TASK-" + foundTaskInstanceModel.getId() + "/" +
                "FUNCTION-" + functionInstanceModel.getId() + "/" +
                "FIELD-" + fieldInstanceModel.getId() + "/" +
                "COLUMN-" + columnInstanceDto.getId();
    }

    private ColumnInstanceDto columnInstanceModelToDto(ColumnInstanceModel columnInstanceModel) {
        if (columnInstanceModel == null) {
            return null;
        }
        ColumnTemplateModel columnTemplateModel = this.columnTemplateRepository.findById(columnInstanceModel.getColumnTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN, "id", columnInstanceModel.getColumnTemplate().getId(), true)
        );
        ColumnMetadataTemplateModel columnMetadataTemplateModel = this.columnMetadataTemplateRepository.findById(columnTemplateModel.getColumnMetadataTemplate().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.COLUMN_METADATA, "id", columnTemplateModel.getColumnMetadataTemplate().getId(), true)
        );
        ColumnInstanceDto columnInstanceDto = this.modelMapper.map(columnInstanceModel, ColumnInstanceDto.class);
        columnInstanceDto.setColumnTemplateId(columnInstanceModel.getColumnTemplate().getId());
        columnInstanceDto.setFieldInstanceId(columnInstanceModel.getFieldInstance().getId());
        if (columnInstanceModel.getDropdownTemplate() != null) {
            columnInstanceDto.setDropdownTemplateId(columnInstanceModel.getDropdownTemplate().getId());
        }
        if (columnMetadataTemplateModel.getType().equals(ColumnType.FILE)) {
            // Get the directory path
            String directoryPath = this.getFilePath(columnInstanceDto);
            // Retrieve file names
            File directory = new File(directoryPath);
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                List<String> filePaths = Arrays.stream(files).map(File::getAbsolutePath).toList();
                // Set file paths in DTO
                columnInstanceDto.setFilePaths(filePaths);
            }
        }

        return columnInstanceDto;
    }

}
