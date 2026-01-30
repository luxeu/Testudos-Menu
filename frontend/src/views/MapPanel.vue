<template>
    <div class="map-container">
        <img :src="imageSrc" alt="Map" class="map-image" />

        <svg :viewBox="viewBox" class="map-overlay">

            <polygon v-for="(coords, name) in coordinates" :key="name" :points="coords" class="station-zone"
                :class="{ 'active': activeStations.includes(name) }">
                <title>{{ name }}</title>
            </polygon>

        </svg>
    </div>
</template>

<script setup>
import { yStationCoordinates } from '../views/mappings/YMapping.js';

const props = defineProps({
    activeStations: {
        type: Array,
        default: () => []
    },
    imageSrc: { type: String, required: true },
    coordinates: Object,
    viewBox: String
});

const mappings = yStationCoordinates;
</script>

<style scoped>
.map-container {
    position: relative;
    width: 66%;
    height: auto;
}

.map-image {
    width: 100%;
    height: auto;
    display: block;
}

.map-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
}

.station-zone {
    fill: transparent;
    stroke: none;
    transition: fill 0.3s ease;
    pointer-events: auto;
    cursor: pointer;
}

.station-zone:hover {
    fill: rgba(0, 0, 0, 0.1);
}

.station-zone.active {
    fill: rgba(226, 24, 51, 0.6);
    stroke: #fff;
    stroke-width: 2px;
    animation: pulse 2s infinite;
}

@keyframes pulse {
    0% {
        opacity: 0.6;
    }

    50% {
        opacity: 0.3;
    }

    100% {
        opacity: 0.6;
    }
}
</style>