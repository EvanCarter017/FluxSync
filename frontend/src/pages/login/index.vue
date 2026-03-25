<script setup lang="ts">

import Login from "@/pages/login/components/Login.vue";
import Register from "@/pages/login/components/Register.vue";
import logo from "@/assets/Logo&Fonts.png";
import {
  loginStatus
} from "./composables/script";
import Totp from "@/pages/login/components/Totp.vue";


</script>

<template>
  <div class="container">
    <div class="box">

      <div v-if="loginStatus == 'login' || loginStatus == 'register'" style="display: flex; justify-content: center; margin-top: 20px;">
        <el-image :src="logo" style="width: 40%;" />
      </div>

      <div style="display: flex; justify-content: center; margin-top: 30px;">
        <transition name="fade" mode="out-in">
          <Login style="width: 380px;" v-if="loginStatus == 'login'" />
          <Register style="width: 380px;" v-else-if="loginStatus == 'register'" />
          <Totp style="width: 550px;" v-else />
        </transition>
      </div>

    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter {
  opacity: 0;
}
.fade-leave-to {
  opacity: 0;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100vw;
}

.box {
  height: 530px;
  border-radius: 10px;
  padding: 20px;
}
</style>