<template>
  <div class="page-container">
    <header class="top-bar">
      <router-link to="/" class="back-link">←</router-link>
      <h1>Yahentamitsi</h1>
    </header>

    <div class="main-layout">

      <div class="sidebar">

        <div class="controls-wrapper">

          <div class="meal-selector">
            <label>Meal Time:</label>
            <select v-model="selectedMeal">
              <option value="All">Show All</option>
              <option value="Breakfast">Breakfast</option>
              <option value="Lunch">Lunch</option>
              <option value="Dinner">Dinner</option>
            </select>
          </div>

          <input v-model="searchQuery" type="text" placeholder="Search menu..." class="search-bar" />

          <div class="filter-chips">
            <button v-for="filter in filters" :key="filter" :class="{ active: activeFilter === filter }"
              @click="activeFilter = filter">
              {{ filter }}
            </button>
          </div>
        </div>

        <div class="results-area">
          <div v-if="loading" class="status-msg">Loading...</div>
          <div v-else-if="error" class="status-msg error">{{ error }}</div>

          <div v-else-if="filteredMenu.length === 0" class="status-msg">
            No results found.
          </div>

          <div v-else>
            <div v-for="menu in filteredMenu" :key="menu.id" class="meal-group">
              <h3 class="meal-header">{{ menu.mealPeriod }}</h3>

              <div v-for="station in menu.stations" :key="station.stationName" class="station-group">
                <div class="station-name">{{ station.stationName }}</div>

                <div v-for="item in station.items" :key="item.id" class="food-card">
                  <div class="food-header">
                    <span class="food-name">{{ item.name }}</span>
                    <span v-if="item.calories" class="cals">{{ item.calories }} cal</span>
                  </div>

                  <div class="tags">
                    <span v-for="alg in item.allergens" :key="alg" class="tag" :class="alg">
                      {{ alg }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="main-content">
        <div class="placeholder-box">
          <h2>Select an item to see details</h2>
          <p>This area will show full nutrition facts later.</p>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

// --- STATE ---
const menuData = ref([]);
const loading = ref(true);
const error = ref(null);

const searchQuery = ref("");
const selectedMeal = ref("All");
const activeFilter = ref("All");
const filters = ["All", "Vegetarian", "Vegan", "Gluten Free"];

// --- FETCH DATA ---
onMounted(async () => {
  try {
    const today = "1/29/2026";
    const location = "Yahentamitsi";

    const response = await axios.get(`http://localhost:8080/api/menus`, {
      params: { date: today, location: location }
    });

    menuData.value = response.data;
    loading.value = false;
  } catch (err) {
    console.error(err);
    error.value = "Could not load menu.";
    loading.value = false;
  }
});

// --- FILTER LOGIC ---
const filteredMenu = computed(() => {
  if (!menuData.value) return [];

  let visibleMenus = menuData.value;
  if (selectedMeal.value !== "All") {
    visibleMenus = visibleMenus.filter(m => m.mealPeriod === selectedMeal.value);
  }

  return visibleMenus.map(menu => {

    const filteredStations = menu.stations.map(station => {

      const filteredItems = station.items.filter(item => {
        // A. Search Query
        const matchesSearch = item.name.toLowerCase().includes(searchQuery.value.toLowerCase());

        // B. Allergen Filters
        let matchesFilter = true;
        const itemTags = item.allergens || [];

        if (activeFilter.value === "Vegetarian") {
          matchesFilter = itemTags.includes("vegetarian");
        }
        else if (activeFilter.value === "Vegan") {
          matchesFilter = itemTags.includes("vegan");
        }
        else if (activeFilter.value === "Gluten Free") {
          const hasGluten = itemTags.includes("gluten") || itemTags.includes("wheat");
          matchesFilter = !hasGluten;
        }

        return matchesSearch && matchesFilter;
      });

      return { ...station, items: filteredItems };

    }).filter(station => station.items.length > 0);

    return { ...menu, stations: filteredStations };

  }).filter(menu => menu.stations.length > 0);
});
</script>

<style scoped>
/* --- LAYOUT --- */
.page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.top-bar {
  background: #e21833;
  color: white;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 60px;
  width: 100%;
  box-sizing: border-box;

  position: relative;
}

.top-bar h1 {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);

  margin: 0;
  font-size: 1.5rem;
  white-space: nowrap;
}

.back-link {
  color: white;
  text-decoration: none;
  font-weight: bold;
  font-size: xx-large;
  z-index: 0;
}

.main-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* --- LEFT SIDEBAR (33% Width) --- */
.sidebar {
  width: 33%;
  min-width: 350px;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.controls-wrapper {
  padding: 15px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.search-bar {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  margin-bottom: 10px;
}

.meal-selector {
  margin-bottom: 10px;
  color: black
}

.meal-selector label {
  font-size: 0.9rem;
  margin-right: 10px;
  font-weight: bold;
}

.meal-selector select {
  padding: 5px;
  border-radius: 4px;
}

.filter-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-chips button {
  font-size: 0.8rem;
  padding: 5px 10px;
  border: 1px solid #ccc;
  background: white;
  border-radius: 15px;
  cursor: pointer;
}

.filter-chips button.active {
  background: #333;
  color: white;
  border-color: #333;
}

.results-area {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

/* --- FOOD ITEMS --- */
.meal-header {
  font-weight: bold;
  color: #000000;
  margin: 20px 0 10px 0;
  border-bottom: 2px solid #eee;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 1rem;
}

.station-name {
  font-weight: bold;
  color: #c63939;
  margin-top: 15px;
  margin-bottom: 5px;
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.food-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 8px;
  transition: background 0.2s;
}

.food-card:hover {
  background: #f9f9f9;
  border-color: #ddd;
}

.food-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.food-name {
  font-weight: 600;
  font-size: 1rem;
  color: rgb(70, 70, 70)
}

.cals {
  font-size: 0.8rem;
  color: #888;
  white-space: nowrap;
  margin-left: 10px;
}

.tags {
  display: flex;
  gap: 5px;
  margin-top: 5px;
  flex-wrap: wrap;
}

.tag {
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 4px;
  background: #eee;
  color: #666;
}

.tag.vegetarian {
  background: #d4edda;
  color: #155724;
}

.tag.vegan {
  background: #c3e6cb;
  color: #155724;
}

.tag.gluten {
  background: #f8d7da;
  color: #721c24;
}

/* --- RIGHT CONTENT (66% Width) --- */
.main-content {
  flex: 1;
  background: #f4f4f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-box {
  text-align: center;
  color: #888;
}

.status-msg {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.error {
  color: red;
}
</style>