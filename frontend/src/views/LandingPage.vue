<template>
  <div class="landing">
    <h1>Food For Terps</h1>

    <div class="buttons">
      <router-link to="/yahentamitsi" class="btn">Yahentamitsi</router-link>
      <router-link to="/251-north" class="btn">251 North</router-link>
      <router-link to="/south-campus" class="btn">South Campus</router-link>
    </div>
  </div>
</template>

<style scoped>
.landing {
  text-align: center;
  margin-top: 50px;
  color: black
}

.buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 20px;
}

.btn {
  padding: 15px 30px;
  background-color: #e21833;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: bold;
}

.btn:hover {
  background-color: #b00020;
}
</style>