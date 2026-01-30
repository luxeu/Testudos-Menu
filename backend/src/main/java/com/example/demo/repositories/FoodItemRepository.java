package com.example.demo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.demo.models.FoodItem;
import java.util.Optional;

public interface FoodItemRepository extends MongoRepository<FoodItem, String> {
    Optional<FoodItem> findByName(String name);
}