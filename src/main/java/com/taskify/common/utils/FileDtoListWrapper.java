package com.taskify.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDtoListWrapper {

    private List<FileDto> fileDtos = new ArrayList<>();

}