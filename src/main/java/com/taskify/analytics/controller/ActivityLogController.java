package com.taskify.analytics.controller;

import com.taskify.analytics.services.ActivityLogServices;
import com.taskify.common.utils.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activities")
public class ActivityLogController {

    @Autowired
    private ActivityLogServices activityLogServices;

    @GetMapping("/date")
    public ResponseEntity<?> getActivityLogsByDate(
            @RequestParam("page") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam LocalDateTime date
    ) {
        return new ResponseEntity<>(this.activityLogServices.getActivityLogsByDate(pageNumber, pageSize, date), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getActivityLogsByDate(
            @RequestParam("page") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(value = "year") int year,
            @RequestParam(value = "month") int month
    ) {
        return new ResponseEntity<>(this.activityLogServices.getActivityLogsByYearAndMonth(pageNumber, pageSize, year, month), HttpStatus.OK);
    }

}
