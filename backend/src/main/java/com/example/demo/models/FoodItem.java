package com.example.demo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "food_items")
public class FoodItem {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private Double calories;
    private Double totalFat;
    private Double sodium;
    private Double protein;
    private Double carbs;

    private List<String> allergens;
}