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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {
    private final MenuRepository menuRepository;
    private final FoodItemRepository foodItemRepository;

    private final Pattern CALORIE_PATTERN = Pattern.compile("Calories\\D*(\\d+(?:\\.\\d+)?)");
    private final Pattern FAT_PATTERN = Pattern.compile("Fat\\D*(\\d+(?:\\.\\d+)?)");
    private final Pattern PROTEIN_PATTERN = Pattern.compile("Protein\\D*(\\d+(?:\\.\\d+)?)");
    private final Pattern CARB_PATTERN = Pattern.compile("Carbohydrates\\D*(\\d+(?:\\.\\d+)?)");
    private final Pattern SODIUM_PATTERN = Pattern.compile("Sodium\\D*(\\d+(?:\\.\\d+)?)");

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
                    Elements rows = card.select(".menu-item-row");
                    for (Element row : rows) {
                        Element link = row.selectFirst(".menu-item-name");
                        if (link == null)
                            continue;

                        String itemName = link.text();
                        String itemUrl = baseUrl + link.attr("href");

                        List<String> allergens = new ArrayList<>();
                        Elements icons = row.select("img.nutri-icon");
                        for (Element icon : icons) {
                            String title = icon.attr("title");
                            if (title.isEmpty())
                                title = icon.attr("alt");

                            if (!title.isEmpty()) {
                                allergens.add(title.replace("Contains ", "").trim().toLowerCase());
                            }
                        }

                        FoodItem item = getOrCreateFoodItem(itemName, itemUrl, allergens);
                        if (item != null) {
                            stationFoods.add(item);
                        }
                    }

                    if (!stationFoods.isEmpty()) {
                        station.setItems(stationFoods);
                        stationsList.add(station);
                    }
                }

                // Save Menu
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

    private FoodItem getOrCreateFoodItem(String name, String url, List<String> allergens) {
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
            newItem.setAllergens(allergens);

            // Scrape Strings
            String fullText = doc.body().text();

            newItem.setCalories(extractValue(CALORIE_PATTERN, fullText));
            newItem.setTotalFat(extractValue(FAT_PATTERN, fullText));
            newItem.setProtein(extractValue(PROTEIN_PATTERN, fullText));
            newItem.setCarbs(extractValue(CARB_PATTERN, fullText));

            Double sodiumMg = extractValue(SODIUM_PATTERN, fullText);
            newItem.setSodium(sodiumMg / 1000.0);

            return foodItemRepository.save(newItem);

        } catch (Exception e) {
            System.err.println("Failed to scrape item: " + name);
            return null;
        }
    }

    private Double extractValue(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}