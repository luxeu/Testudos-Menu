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
          <div v-else-if="filteredMenu.length === 0" class="status-msg">No results found.</div>

          <div v-else>
            <div v-for="menu in filteredMenu" :key="menu.id" class="meal-group">
              <h3 class="meal-header">{{ menu.mealPeriod }}</h3>
              <div v-for="station in menu.stations" :key="station.stationName" class="station-group">
                <div class="station-name">{{ station.stationName }}</div>

                <div v-for="item in station.items" :key="item.id" class="food-card"
                  :class="{ 'selected': selectedItem && selectedItem.name === item.name }" @click="selectItem(item)">
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

        <div v-if="selectedItem" class="nutrition-pane">
          <div class="pane-header">
            <h3>{{ selectedItem.name }}</h3>
            <button class="close-btn" @click="selectedItem = null">×</button>
          </div>

          <div class="pane-content">
            <div class="macro-grid">
              <div class="macro-item">
                <span class="label">Calories</span>
                <span class="value">{{ selectedItem.calories || 0 }}</span>
              </div>
              <div class="macro-item">
                <span class="label">Protein</span>
                <span class="value">{{ selectedItem.protein || 0 }}g</span>
              </div>
              <div class="macro-item">
                <span class="label">Carbs</span>
                <span class="value">{{ selectedItem.carbs || 0 }}g</span>
              </div>
              <div class="macro-item">
                <span class="label">Fat</span>
                <span class="value">{{ selectedItem.totalFat || 0 }}g</span>
              </div>
            </div>
            <div class="macro-item">
              <span class="label">Sodium:</span>
              <span class="value">{{ (selectedItem.sodium * 1000).toFixed(0) }} mg</span>
            </div>

            <div v-if="selectedItem.allergens && selectedItem.allergens.length > 0" class="pane-allergens">
              <strong>Contains: </strong> {{ selectedItem.allergens.join(", ") }}
            </div>
          </div>
        </div>

      </div>

      <MapPanel :activeStations="highlightedStationNames" />

      <div class="main-content"></div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import MapPanel from '../views/MapPanel.vue';

// --- STATE ---
const menuData = ref([]);
const loading = ref(true);
const error = ref(null);
const searchQuery = ref("");
const selectedMeal = ref("All");
const activeFilter = ref("All");
const filters = ["All", "Vegetarian", "Vegan", "Gluten Free"];
const selectedItem = ref(null);

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

// Click Handler
const selectItem = (item) => {
  selectedItem.value = item;
};

// --- FILTER ---
const filteredMenu = computed(() => {
  if (!menuData.value) return [];
  let visibleMenus = menuData.value;
  if (selectedMeal.value !== "All") {
    visibleMenus = visibleMenus.filter(m => m.mealPeriod === selectedMeal.value);
  }

  return visibleMenus.map(menu => {
    const filteredStations = menu.stations.map(station => {
      const filteredItems = station.items.filter(item => {
        const matchesSearch = item.name.toLowerCase().includes(searchQuery.value.toLowerCase());
        let matchesFilter = true;
        const itemTags = item.allergens || [];

        if (activeFilter.value === "Vegetarian") matchesFilter = itemTags.includes("vegetarian");
        else if (activeFilter.value === "Vegan") matchesFilter = itemTags.includes("vegan");
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

// --- MAP ---
const highlightedStationNames = computed(() => {
  const names = new Set();

  filteredMenu.value.forEach(meal => {
    meal.stations.forEach(station => {
      if (station.items.length > 0) {
        names.add(station.stationName);
      }
    });
  });

  return Array.from(names);
});

</script>


<style scoped>
/* --- PAGE LAYOUT --- */
.page-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: white;
  position: absolute;
  top: 0;
  left: 0;
}

.top-bar {
  background: #000000;
  color: white;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 60px;
  width: 100%;
  box-sizing: border-box;
  position: relative;
  font-size: xx-large;
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
  z-index: 10;
}

.main-layout {
  display: flex;
  width: 100%;
  height: calc(100vh - 60px);
  overflow: hidden;
}

/* --- SIDEBAR --- */
.sidebar {
  width: 33%;
  height: 100%;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  background: #fff;
  z-index: 10;
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
  box-sizing: border-box;
}

.meal-selector {
  margin-bottom: 10px;
  color: #333
}

.meal-selector label {
  font-size: 0.9rem;
  margin-right: 10px;
  font-weight: bold;
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

/* --- RESULTS LIST --- */
.results-area {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

/* --- FOOD CARDS --- */
.meal-header {
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #000000;
  margin: 20px 0 10px 0;
  border-bottom: 2px solid #eee;
}

.station-name {
  font-weight: bold;
  color: #e36b6b;
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
  transition: all 0.2s;
  cursor: pointer;
}

.food-card:hover {
  background: #f9f9f9;
  border-color: #ddd;
}

.food-card.selected {
  border-color: #e21833;
  background: #fff0f0;
}

.food-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.food-name {
  font-weight: 600;
  font-size: 1rem;
  color: #515151
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
  background: #fffec8;
  color: #72713e;
}

.tag.vegan {
  background: #c3e6cb;
  color: #155724;
}

.tag.gluten {
  background: #f8d7da;
  color: #721c24;
}

/* NUTRITION PANE --- */
.nutrition-pane {
  height: 250px;
  border-top: 2px solid #e21833;
  background: #fff;
  padding: 15px;
  box-shadow: 0 -4px 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }

  to {
    transform: translateY(0);
  }
}

.pane-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.pane-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #888;
}

.close-btn:hover {
  color: #333;
}

.macro-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.macro-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f8f9fa;
  padding: 10px;
  border-radius: 8px;
}

.macro-item .label {
  color: #000000;
  font-size: 0.75rem;
  text-transform: uppercase;
}

.macro-item .value {
  font-weight: bold;
  font-size: 1.1rem;
  color: #333333;
}

.macro-row {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  border-bottom: 1px solid #b8b8b8;
}

.pane-allergens {
  margin-top: 10px;
  font-size: 0.9rem;
  color: #555;
}

/* --- RIGHT CONTENT --- */
.main-content {
  flex: 1;
  background: #f4f4f9;
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