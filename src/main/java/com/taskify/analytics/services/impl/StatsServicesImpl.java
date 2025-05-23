package com.taskify.analytics.services.impl;

import com.taskify.analytics.services.StatsServices;
import com.taskify.analytics.utils.MonthlyTaskStats;
import com.taskify.analytics.utils.OverallTaskStats;
import com.taskify.common.constants.PriorityType;
import com.taskify.stakeholders.repositories.CustomerRepository;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.instances.repositories.TaskInstanceRepository;
import com.taskify.task.templates.models.TaskTemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsServicesImpl implements StatsServices {

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public OverallTaskStats getOverallTaskStats() {
        Pageable pageable = PageRequest.of(1, 100);
        long totalTasks = this.taskInstanceRepository.findByIsArchived(false, pageable).getTotalElements();
        long customers = this.customerRepository.findAll(pageable).getTotalElements();

        long overdueTasks = this.taskInstanceRepository.findTaskInstancesByOverdueAndIsArchived(pageable, false).getTotalElements();
        System.out.println("overdueTasks: " + overdueTasks);

        long newPumpTasks = this.taskInstanceRepository.findByTaskTemplateAndIsArchived(pageable, new TaskTemplateModel(1L), false).getTotalElements();
        long serviceTask = this.taskInstanceRepository.findByTaskTemplateAndIsArchived(pageable, new TaskTemplateModel(2L), false).getTotalElements();

        return new OverallTaskStats(totalTasks, customers, overdueTasks, newPumpTasks, serviceTask);
    }

    @Override
    public MonthlyTaskStats getMonthlyTaskStats() {
        Pageable pageable = PageRequest.of(1, 10);

        List<TaskInstanceModel> taskInstanceModels = this.taskInstanceRepository.findTasksByYearAndMonthAndIsArchived(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), false);
        long highPriority = taskInstanceModels.stream().filter(t -> t.getPriorityType().equals(PriorityType.HIGH)).toList().size();
        long mediumPriority = taskInstanceModels.stream().filter(t -> t.getPriorityType().equals(PriorityType.MEDIUM)).toList().size();
        long normalPriority = taskInstanceModels.stream().filter(t -> t.getPriorityType().equals(PriorityType.NORMAL)).toList().size();
        long pendingTask = taskInstanceModels.stream().filter(t -> t.getClosedAt() == null).toList().size();
        long closedTask = taskInstanceModels.size() - pendingTask;

        return new MonthlyTaskStats(taskInstanceModels.size(), highPriority, mediumPriority, normalPriority, pendingTask, closedTask);
    }
}
