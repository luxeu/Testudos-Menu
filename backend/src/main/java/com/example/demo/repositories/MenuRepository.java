package com.example.demo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.demo.models.DiningMenu;
import java.util.List;

public interface MenuRepository extends MongoRepository<DiningMenu, String> {
    List<DiningMenu> findByDateAndLocation(String date, String location);
}