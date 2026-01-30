<template>
  <DiningHallView locationId="Yahentamitsi" displayName="Yahentamitsi Dining Hall" :imageSrc="mapImg"
    :coordinates="yData" viewBox="0 0 3180 2079" />
</template>

<script setup>
import DiningHallView from '../views/DiningHallView.vue';
import { yStationCoordinates } from '../views/mappings/YMapping';
import mapImg from '../assets/yahentamitsi-map.png';

const yData = yStationCoordinates;
</script>