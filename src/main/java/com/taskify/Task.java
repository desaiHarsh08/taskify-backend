package com.taskify;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Task {

    private Long id;

    private String title;
    private String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
