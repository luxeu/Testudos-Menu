package com.example.demo.controllers;

import com.example.demo.models.DiningMenu;
import com.example.demo.repositories.MenuRepository;
import com.example.demo.services.ScraperService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class MenuController {

    private final ScraperService scraperService;
    private final MenuRepository menuRepository;

    public MenuController(ScraperService scraperService, MenuRepository menuRepository) {
        this.scraperService = scraperService;
        this.menuRepository = menuRepository;
    }

    @GetMapping
    public List<DiningMenu> getMenus(@RequestParam String date, @RequestParam String location) {
        return menuRepository.findByDateAndLocation(date, location);
    }

    // Manual request
    @PostMapping("/scrape")
    public String triggerScrape(@RequestParam String date) {
        new Thread(() -> {
            System.out.println("Starting background scrape...");
            scraperService.scrape("19", "Yahentamitsi", date);
            scraperService.scrape("51", "251 North", date);
            scraperService.scrape("16", "South Campus", date);
        }).start();

        return "Scraping started for " + date + ".";
    }
}
