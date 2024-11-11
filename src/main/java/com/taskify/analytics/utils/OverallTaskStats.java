package com.taskify.analytics.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OverallTaskStats {

    private long tasks;

    private long customers;

    private long overdueTasks;

    private long newPumpTasks;

    private long serviceTasks;

}
