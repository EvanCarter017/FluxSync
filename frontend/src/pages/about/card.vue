<script setup lang="ts">

import {contents} from "@/pages/about/CardContent";
import {NCard} from "naive-ui";
</script>

<template>
  <n-card v-for="(item, index) in contents"
          :key="item.id"
          class="card"
  >
    <template #header>
      <el-icon style="width: 30px; height: 30px; display: flex; align-items: center;">
        <component :is="item.icon" style="color: #1378E0;" />
      </el-icon>
    </template>

    <template #header-extra>
      <el-text type="info">{{ item.headerExtra }}</el-text>
    </template>

    <template #default>
      <div>
        <el-text style="color: aliceblue; font-size: 18px;">
          {{ item.default }}
        </el-text>
      </div>
      <div style="margin-top: 10px;">
        <el-text type="info">
          {{ item.main }}
        </el-text>
      </div>
    </template>
  </n-card>
</template>

<style scoped>
.card {
  margin-bottom: 30px;
  background-color:#101826;
  border: 2px solid #151E2C;
  border-radius: 15px;
  width: 420px;
  min-width: 420px;
}
</style>