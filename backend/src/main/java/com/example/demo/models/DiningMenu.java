package com.example.demo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.List;

@Data
@Document(collection = "menus")
public class DiningMenu {
    @Id
    private String id;
    
    private String location;
    private String date;
    private String mealPeriod;

    private List<Station> stations; 

    @Data
    public static class Station {
        private String stationName;
        
        @DBRef
        private List<FoodItem> items;
    }
}