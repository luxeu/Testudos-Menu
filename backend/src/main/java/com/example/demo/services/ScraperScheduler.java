package com.example.demo.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScraperScheduler {

    private final ScraperService scraperService;

    public ScraperScheduler(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleScrapingTask() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy"));

        System.out.println("Starting background scrape...");
        scraperService.scrape("19", "Yahentamitsi", date);
        scraperService.scrape("51", "251 North", date);
        scraperService.scrape("16", "South Campus", date);
        System.out.println("Finished background scrape.");
    }

}