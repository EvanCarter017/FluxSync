<script setup lang="ts">

import config from "./store";
import Plus from "@/components/icons/plus.vue";
import Sub from "@/components/icons/sub.vue";
import Flux from "@/components/icons/flux.vue";
import {NProgress} from "naive-ui";
import "@/pages/board/Aside/css/progress.css";
import Gconfig from "@/store";
import {onMounted, ref, watch} from "vue";
import Main from "@/pages/board/Aside/Main.vue";
import ws from '@/services/websocket/Service';

const percent = ref(0);
const usedBytesNumber = Gconfig.info.usedBytesNumber;
const limitBytesNumber = Gconfig.info.limitBytesNumber;
const progressStatus = ref<"info" | "warning" | "error">("info")

const wsStatus = ref("");
onMounted(() => {

  const ratio = BigInt(usedBytesNumber) * 1000n / BigInt(limitBytesNumber);
  percent.value = Number(ratio) / 10;

  if (percent.value < 70) {
    progressStatus.value = "info";
  } else if (percent.value < 90) {
    progressStatus.value = "warning";
  } else {
    progressStatus.value = "error";
  }

  watch(ws.ready, (n, o) => {
    if (n) {
      wsStatus.value = "服务正常";
      document.querySelector(".status-icon")?.classList.remove("offline");
      document.querySelector(".status-icon")?.classList.add("online");
    } else {
      document.querySelector(".status-icon")?.classList.remove("online");
      document.querySelector(".status-icon")?.classList.add("offline");
      wsStatus.value = "未连接服务";
    }
  }, { immediate: true });

});


</script>

<template>

  <div
      style="
        background-color: #292929;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        user-select: none;
      "
  >
    <!--  Header  -->
    <div style="display: flex;justify-content: center; flex-direction: column; align-items: center;" :style="{ padding: `${config.hiddenAside.value ? 12 : 18}px` }">
      <template
          v-if="!config.hiddenAside.value"
      >
        <el-text
            style="
            color: aliceblue;
            font-size: 24px;
          "
        >
          Flux Sync
        </el-text>
        <span class="status-icon offline">
          {{ wsStatus }}
        </span>
      </template>
      <Flux style="width: 35px; height: 35px; color: aliceblue;" v-else/>
    </div>

    <!--  Main  -->
    <div style="height: 100%;">

      <Main />

    </div>

    <!--  footer  -->
    <!--  border-top: 1px solid rgba(240, 248, 255, 0.3);  -->
    <div class="footer" style="padding: 5px 18px; display: flex; height: 80px;" :class="{ isClosable: !config.hiddenAside.value }">

<!--      <div class="flex alignCenter">-->
<!--        <transition name="fade" mode="out-in">-->
<!--          <Plus v-if="config.hiddenAside.value" class="footerBtn" @click="config.hiddenAside.value = false"/>-->
<!--          <Sub v-else class="footerBtn" @click="config.hiddenAside.value = true"/>-->
<!--        </transition>-->
<!--      </div>-->

      <!--   progress.css   -->
      <div class="progress-box whp100" v-if="!config.hiddenAside.value">
        <div class="wp100 flex alignCenter">
          <el-text style="color: aliceblue; font-size: 12px;">云盘空间</el-text>
          <el-text style="color: aliceblue; font-size: 12px; margin-left: auto">点击进入 ></el-text>
        </div>
        <div class="wp100 flex justifyCenter alignCenter" style="padding-top: 3px; padding-bottom: 8px;">
          <n-progress
              type="line"
              :status="progressStatus"
              :show-indicator="false"
              :percentage="percent"
              class="progress"
          />
        </div>
        <div class="wp100 flex alignCenter">
          <el-text style="color: aliceblue; font-size: 12px;">
            {{ Gconfig.info.usedBytes }} / {{ Gconfig.info.limitBytes }}
          </el-text>

          <el-text style="color: aliceblue; font-size: 12px; margin-left: auto;">
            {{
              Gconfig.info.usedBytes == 0 ? "0%" :
                  percent.toFixed(1) + "%"
            }}
          </el-text>
        </div>
      </div>

    </div>

  </div>

</template>

<style scoped>

.footer {
  position: relative;
}

.footer.isClosable::after {
  content: "";
  position: absolute;
  top: 0;
  left: 10px;
  right: 10px;
  height: 1px;
  background: rgba(240, 248, 255, 0.3);
}

.footerBtn {
  color: aliceblue;
}

.footerBtn:hover {
  opacity: 0.7;
}

.fade-enter-active {
  transition: transform 0.3s ease;
}

.fade-enter-from {
  transform: rotate(-90deg);
}

.fade-enter-to {
  transform: rotate(0deg);
}

.fade-leave-active {
  transition: transform 0.3s ease;
}

.fade-leave-from {
  transform: rotate(0deg);
}

.fade-leave-to {
  transform: rotate(90deg);
}

.status-icon {
  color: aliceblue;
  position: relative;
  font-size: 12px;
  margin-top: 5px;
}
.status-icon::after {
  content: '';
  position: absolute;
  top: 4px;
  left: -15px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: background-color 0.25s ease;
}
.status-icon.online::after {
  background: #2ECC71;
}
.status-icon.offline::after {
  background: #FF6F61;
}

</style>