package com.taskify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TmpController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        this.messagingTemplate.convertAndSend("/topic/tasks", task);
        return task;
    }


}
