package com.taskify.analytics.controller;

import com.taskify.analytics.services.StatsServices;
import com.taskify.analytics.utils.MonthlyTaskStats;
import com.taskify.analytics.utils.OverallTaskStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class StatsController {

    @Autowired
    private StatsServices statsServices;

    @GetMapping("/overall-stats")
    public ResponseEntity<OverallTaskStats> getOverallTasksStats() {
        return new ResponseEntity<>(this.statsServices.getOverallTaskStats(), HttpStatus.OK);
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<MonthlyTaskStats> getMonthlyTasksStats() {
        return new ResponseEntity<>(this.statsServices.getMonthlyTaskStats(), HttpStatus.OK);
    }

}
