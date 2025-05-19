package com.taskify.task.instances.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PumpDetailsDto {
    private String pumpMake;

    private String pumpType;

    private String stage;

    private String serialNumber;

    private String motorMake;

    private String hp;

    private String volts;

    private String phase;

}
