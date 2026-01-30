<template>
  <DiningHallView locationId="251 North" displayName="251 North" :imageSrc="mapImg" :coordinates="northData"
    viewBox="0 0 1311 1273" />
</template>

<script setup>
import DiningHallView from '../views/DiningHallView.vue';
// import { northCoordinates } from '../data/northMappings';
import mapImg from '../assets/251-north-map.png';

const northData = {};
</script>