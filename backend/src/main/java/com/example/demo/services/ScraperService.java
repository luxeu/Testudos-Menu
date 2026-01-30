package com.example.demo.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import com.example.demo.models.DiningMenu;
import com.example.demo.models.FoodItem;
import com.example.demo.repositories.FoodItemRepository;
import com.example.demo.repositories.MenuRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScraperService {
    private final MenuRepository menuRepository;
    private final FoodItemRepository foodItemRepository;

    public ScraperService(MenuRepository menuRepository, FoodItemRepository foodItemRepository) {
        this.menuRepository = menuRepository;
        this.foodItemRepository = foodItemRepository;
    }

    public void scrape(String locationNum, String locationName, String date) {
        String baseUrl = "https://nutrition.umd.edu/";
        String fullUrl = baseUrl + "?locationNum=" + locationNum + "&dtdate=" + date;

        try {
            Document doc = Jsoup.connect(fullUrl).userAgent("Mozilla/5.0").timeout(10000).get();

            Map<String, String> paneMap = new HashMap<>();
            paneMap.put("pane-1", "Breakfast");
            paneMap.put("pane-2", "Lunch");
            paneMap.put("pane-3", "Dinner");

            for (Map.Entry<String, String> entry : paneMap.entrySet()) {
                String paneId = entry.getKey();
                String mealPeriodName = entry.getValue();

                Element pane = doc.getElementById(paneId);

                if (pane == null)
                    continue;

                DiningMenu menu = new DiningMenu();
                menu.setLocation(locationName);
                menu.setDate(date);
                menu.setMealPeriod(mealPeriodName);

                List<DiningMenu.Station> stationsList = new ArrayList<>();

                // Get all cards
                Elements stationCards = pane.select(".card");

                for (Element card : stationCards) {
                    // Get station name
                    String stationName = card.select(".card-title").text();

                    if (stationName.isEmpty())
                        continue;

                    DiningMenu.Station station = new DiningMenu.Station();
                    station.setStationName(stationName);

                    List<FoodItem> stationFoods = new ArrayList<>();

                    // Find all items in this card
                    Elements foodLinks = card.select("a.menu-item-name");

                    for (Element link : foodLinks) {
                        String itemUrl = baseUrl + link.attr("href");
                        FoodItem item = getOrCreateFoodItem(link.text(), itemUrl);
                        if (item != null) {
                            stationFoods.add(item);
                        }
                    }

                    if (!stationFoods.isEmpty()) {
                        station.setItems(stationFoods);
                        stationsList.add(station);
                    }
                }

                if (!stationsList.isEmpty()) {
                    menu.setStations(stationsList);
                    menuRepository.save(menu);
                    System.out.println("Saved " + mealPeriodName + " with " + stationsList.size() + " stations.");
                }
            }

        } catch (Exception e) {
            System.err.println("Scrape Error: " + e.getMessage());
        }
    }

    private FoodItem getOrCreateFoodItem(String name, String url) {
        // Check DB first to avoid re-scraping
        Optional<FoodItem> existing = foodItemRepository.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        try {
            Thread.sleep(200);
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();

            FoodItem newItem = new FoodItem();
            newItem.setName(name);

            // Scrape Strings
            String rawCal = doc.select(".calories-value").text(); // "120"
            String rawFat = doc.select(".total-fat-value").text(); // "14g"
            String rawSod = doc.select(".sodium-value").text(); // "540mg"
            String rawPro = doc.select(".protein-value").text(); // "20 g"
            String rawCarb = doc.select(".carb-value").text(); // "30g"

            newItem.setCalories(parseToGrams(rawCal));
            newItem.setTotalFat(parseToGrams(rawFat));
            newItem.setSodium(parseToGrams(rawSod));
            newItem.setProtein(parseToGrams(rawPro));
            newItem.setCarbs(parseToGrams(rawCarb));

            // Scrape Allergens
            List<String> allergens = doc.select("img.allergen-icon").eachAttr("alt");
            newItem.setAllergens(allergens);

            return foodItemRepository.save(newItem);

        } catch (Exception e) {
            System.err.println("Failed to scrape item: " + name);
            return null;
        }
    }

    private Double parseToGrams(String rawValue) {
        if (rawValue == null || rawValue.isEmpty())
            return 0.0;

        String numberOnly = rawValue.replaceAll("[^0-9.]", "");
        if (numberOnly.isEmpty())
            return 0.0;

        try {
            double val = Double.parseDouble(numberOnly);

            // If "mg"
            if (rawValue.toLowerCase().contains("mg")) {
                return val / 1000.0;
            }

            return val;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}