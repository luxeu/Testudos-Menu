package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;
import com.example.demo.models.Task;
import com.example.demo.repositories.TaskRepository;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Task> getTasks() {
        return repository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody String title) {
        return repository.save(new Task(title));
    }
}