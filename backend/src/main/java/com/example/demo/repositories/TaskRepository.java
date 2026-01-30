package com.example.demo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.demo.models.Task;

public interface TaskRepository extends MongoRepository<Task, String> {
    // .save(), .findAll(), .deleteById() are available by default
}