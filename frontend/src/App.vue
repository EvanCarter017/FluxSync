
<script setup lang="ts">

import config from "@/store";
import userService from "@/services/userService";
import {onMounted, onUnmounted, ref, watch} from "vue";
import {useRouter} from "vue-router";
import { NSpin } from 'naive-ui';
import {ElNotification} from "element-plus";
import ws from '@/services/websocket/Service';
import jsCookie from 'js-cookie';
import Leader from "@/services/channel/channel";

const Router = useRouter();
const ready = ref(false);

const init = async () => {
  const useService = userService();
  const info = await useService.getInfo();
  if (info) {
    config.info = info;

    Leader.onLeader(() => {
      window.name = 'leader';
      ws.initWebSocket("ws://localhost:1007/api/ws", jsCookie.get('jwt-at'));
    });

    Leader.onFollower(() => {
      ws.close();
    });

    // ws.initWebSocket("ws://localhost:1007/api/ws", jsCookie.get('jwt-at'));

  } else if (info == -1) {
    ElNotification({
      title: '登录失败',
      message: '登录凭证失效, 请尝试重新登录',
      type: 'error'
    });
    await Router.replace("/login");
  } else {
    await Router.replace("/");
  }
  ready.value = true;
}

onMounted(async () => {

  await init();

});

</script>

<template>
  <div id="app" style="height: 100%;">

    <n-spin v-if="!ready"
            :show="!ready"
    >
      <div style="height: 100vh; width: 100vw;"></div>
      <template #description>
        请稍等，精彩即将呈现
      </template>
    </n-spin>

<!--    <div v-show="ready" style="display: contents;">-->
<!--      <router-view />-->
<!--    </div>-->
    <template v-else>
      <router-view />
    </template>

  </div>
</template>

<style scoped>
:deep(.n-spin-description) {
  color: #409EFF;
}
:deep(.n-spin) {
  color: #409EFF;
}
</style>
