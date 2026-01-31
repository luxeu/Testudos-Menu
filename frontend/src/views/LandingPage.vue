<template>
  <div class="landing-page">

    <header class="top-bar">
      <h1>Testudo's Menu</h1>
    </header>

    <div class="nav-grid">

      <router-link to="/yahentamitsi" class="nav-item">
        <div class="bg-image" :style="{ backgroundImage: `url(${yaImg})` }"></div>
        <div class="overlay"></div>
        <span class="label">Yahentamitsi</span>
      </router-link>

      <router-link to="/251-north" class="nav-item">
        <div class="bg-image" :style="{ backgroundImage: `url(${northImg})` }"></div>
        <div class="overlay"></div>
        <span class="label">251 North</span>
      </router-link>

      <router-link to="/south-campus" class="nav-item">
        <div class="bg-image" :style="{ backgroundImage: `url(${southImg})` }"></div>
        <div class="overlay"></div>
        <span class="label">South Campus</span>
      </router-link>

    </div>
  </div>
</template>

<script setup>
import yaImg from '../assets/ya.png';
import northImg from '../assets/251.png';
import southImg from '../assets/south.png';
</script>
<style scoped>
/* --- PAGE LAYOUT --- */
.landing-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: black;
}

/* --- HEADER --- */
.top-bar {
  background: #000000;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
  width: 100%;
  z-index: 10;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.top-bar h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: bold;
}

/* --- NAVIGATION GRID --- */
.nav-grid {
  display: flex;
  flex: 1;
  width: 100%;
}

.nav-item {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  overflow: hidden;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
}

.nav-item:last-child {
  border-right: none;
}

/* --- BACKGROUND IMAGE & ANIMATION --- */
.bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.6s ease;
  z-index: 1;
}

.nav-item:hover .bg-image {
  transform: scale(1.1);
}

/* --- DARK OVERLAY --- */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  transition: background 0.3s;
  z-index: 2;
}

.nav-item:hover .overlay {
  background: rgba(0, 0, 0, 0.15);
}

/* --- TEXT LABEL --- */
.label {
  position: relative;
  z-index: 3;
  color: white;
  font-size: 2rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 2px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.8);
  border: 2px solid rgba(255, 255, 255, 0.7);
  padding: 15px 30px;
  background: rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
}

.nav-item:hover .label {
  background: #e21833;
  border-color: #e21833;
}
</style>