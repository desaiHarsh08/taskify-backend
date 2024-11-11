package com.taskify.analytics.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTaskStats {

    private long tasks;

    private long highPriorityTasks;

    private long mediumPriorityTasks;

    private long normalPriorityTasks;

    private long pendingTasks;

    private long closedTasks;

}
