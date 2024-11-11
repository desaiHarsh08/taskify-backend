package com.taskify.analytics.services;

import com.taskify.analytics.utils.MonthlyTaskStats;
import com.taskify.analytics.utils.OverallTaskStats;

public interface StatsServices {

    OverallTaskStats getOverallTaskStats();

    MonthlyTaskStats getMonthlyTaskStats();

}
