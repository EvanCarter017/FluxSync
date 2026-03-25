<script setup lang="ts">

import Aside from "@/pages/board/Aside/Aside.vue";
import config from "./store";
import {ref, onMounted, onUnmounted, nextTick} from 'vue';
import "./css/layout.css";

const asideWidth = ref(260);

let isResizing = false;
let startX = 0;
let startWidth = 0;

const aside = ref<HTMLELement | null>(null);

const startResize = (e: MouseEvent) => {
  isResizing = true;
  startX = e.clientX;
  startWidth = asideWidth.value;
}

const onMouseMove = (e: MouseEvent) => {
  if (!isResizing) return;
  asideWidth.value = Math.max(240, Math.min(600, startWidth + e.clientX - startX));
}

const onMouseUp = () => {
  isResizing = false;
};

const onMouseLeave = () => {
  if (isResizing) onMouseUp();
};

window.addEventListener('beforeunload', () => {
  localStorage.setItem('hiddenAside', config.hiddenAside.value);
});

onMounted(() => {
  config.hiddenAside.value = localStorage.getItem("hiddenAside") === 'true';
  document.addEventListener('mousemove', onMouseMove);
  document.addEventListener('mouseup', onMouseUp);
  document.addEventListener('mouseleave', onMouseLeave);
});

onUnmounted(() => {
  document.removeEventListener('mousemove', onMouseMove);
  document.removeEventListener('mouseup', onMouseUp);
  document.removeEventListener('mouseleave', onMouseLeave);
});

const showMenu = ref(false);
const x = ref(0);
const y = ref(0);
const menu = ref<HTMLElement | null>(null);
function onRightClick(e) {

  if (!menu) return;

  showMenu.value = true;

  nextTick(() => {

    const menuHeight = menu.value.offsetHeight;
    const screenHeight = window.innerHeight;

    x.value = e.clientX;

    if (e.clientY >= screenHeight - menuHeight - 10) {
      y.value = screenHeight - menuHeight - 10;
    } else {
      y.value = e.clientY;
    }

  });

}

function choose(action) {
  showMenu.value = false;

  if (action == 'openClose') {
    config.hiddenAside.value = !config.hiddenAside.value;
  }

}

</script>

<template>
  <!-- layout.css -->
  <div>

    <el-container>

      <el-container @click="showMenu = false">

        <el-aside @contextmenu.prevent="onRightClick" ref="aside" style="height: 100vh;" :style="{ width: `${config.hiddenAside.value ? 60 : asideWidth}px`  }">
          <Aside/>
        </el-aside>

        <div v-if="!config.hiddenAside.value" @mousedown="startResize" class="divider"></div>

        <el-main>
          <router-view :key="$route.fullPath"/>
        </el-main>

      </el-container>

    </el-container>

    <div v-if="showMenu"
         class="menu"
         :style="{top: y + 'px', left: x + 'px'}"
         ref="menu"
    >
      <div @click="choose('openClose')">{{ !config.hiddenAside.value ? "收起菜单" : "展开菜单" }}</div>
    </div>

  </div>

</template>

<style scoped>

</style>