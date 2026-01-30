<template>
  <DiningHallView locationId="South Campus" displayName="South Campus Dining" :imageSrc="mapImg"
    :coordinates="southData" viewBox="0 0 1918 1555" />
</template>

<script setup>
import DiningHallView from '../views/DiningHallView.vue';
// import { southCoordinates } from '../data/southMappings';
import mapImg from '../assets/south-campus-map.png';

const southData = {};
</script>