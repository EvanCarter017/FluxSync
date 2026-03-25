<script setup lang="ts">
import logo from "@/assets/Logo&Fonts.png";
import {onMounted, reactive, onBeforeUnmount, ref, watch} from "vue";
import jsCookie from "js-cookie";
import {useRouter} from "vue-router";
import {ElNotification} from "element-plus";
import Card from "@/pages/about/card.vue";
import {useDragScroll} from "./CardScroll";
import {service} from "@/services/request";
import UserManager from "@/components/userManager/userManager.vue";
import config from "@/store";

const Router = useRouter();

const scrollContainer = ref(null);
const {onMouseDown, onMouseUpOrLeave, onMouseMove, init} = useDragScroll(scrollContainer);
onMounted(() => {
  document.title = "FluxSync，一款面向团队的实时协同笔记与云端文档管理工具";
  const container = scrollContainer.value;
  if (container) {
    container.addEventListener('mousedown', onMouseDown);
    container.addEventListener('mouseleave', onMouseUpOrLeave);
    container.addEventListener('mouseup', onMouseUpOrLeave);
    container.addEventListener('mousemove', onMouseMove);
    init();
  }
  if (config.info.username) {
    isLogin.value = true;
  }
});

onBeforeUnmount(() => {
  const container = scrollContainer.value;
  if (container) {
    container.removeEventListener('mousedown', onMouseDown);
    container.removeEventListener('mouseleave', onMouseUpOrLeave);
    container.removeEventListener('mouseup', onMouseUpOrLeave);
    container.removeEventListener('mousemove', onMouseMove);
  }
});

const options = reactive([
  {id: 1, name: "首页", path: "/", weight: "bold"},
  {id: 2, name: "关于我们", path: "/about"}
]);

const isLogin = ref(false);

import ws from '@/services/websocket/Service';

</script>

<template>

  <div class="tbarborder"></div>
  <!--   topbar   -->
  <div class="tbar">

    <div class="topbar" style="display: flex; width: 130px; min-width: 130px; margin-right: 10%;">
      <el-image :src="logo" fit="contain"/>
    </div>

    <div style="
              display: flex;
              gap: 50px;
              user-select: none;
              color: #444444;
              width: 700px;
      " class="topbar">
      <el-text v-for="item in options" :key="item.id" :style="{ fontWeight: item.weight }" style="font-size: 16px;">
        <!--    :href="item.path"    -->
        <a @click.prevent="Router.push(`${item.path}`)" style="color: aliceblue; text-decoration: none;" class="menus">
          {{ item.name }}
        </a>
      </el-text>

      <el-button @click="() => {
        ws.send({
          type: 'chat',
          sid: config.info.uid,
          payload: {
            test: 'test'
          },
          timestamp: Date.now()
        });
      }">
        about:about.vue 测试
      </el-button>
    </div>

    <!--  头像区  -->
    <div class="topbar">
      <el-button v-if="!isLogin" type="primary" plain @click="Router.push('/login')">登录/注册</el-button>
      <div v-else>
        <UserManager/>
      </div>
    </div>

    <!--  topbar mobile  -->
    <div class="mobile">

      <div style="display: flex; width: 130px; min-width: 130px; margin-left: 20px;">
        <el-image :src="logo" fit="contain"/>
      </div>

      <div style="display: flex; justify-content: flex-end; margin-right: 20px;"></div>


    </div>

  </div>

  <div>
    <!--  topbar 占位容器  -->
    <div style="display: flex; height: 80px; margin: 0 250px;"></div>

    <!--  main  -->
    <div class="mainContain" style="max-width: 100%; max-height: 100%; margin: 30px 300px 0 300px;">

      <!--   宣传图   -->
      <div class="titleBg">
        <div class="adv" style="margin-bottom: 60px; margin-left: 60px; margin-right: 30%;">
          <h1 style="color: aliceblue">让团队协作更高效、更流畅</h1>
          <p style="color: aliceblue">
            FluxSync 为你提供实时协同笔记与云端文档管理，支持多人同时编辑、自动同步与版本追踪，
            轻松应对远程办公、项目管理与知识共享。
          </p>
          <el-button color="#137FEC" style="width: 200px; height: 40px;"
                     @click="isLogin ? Router.push('/board') : Router.push('/login')">免费使用
          </el-button>
        </div>
      </div>


      <div class="product" style="display: flex; margin-top: 40px;">

        <!-- 产品介绍 -->
        <div style="display: flex; flex-direction: column; width: 100%;">
          <el-text style="color: aliceblue; font-size: 20px; align-self: unset; margin-bottom: 15px;">产品特性</el-text>
          <el-text type="info" style="margin-bottom: 30px; align-self: unset;">
            FluxSync 提供功能强大的产品与服务，旨在满足用户不断变化的需求
          </el-text>

          <div style="display: flex; gap: 20px; overflow-x: auto; user-select: none;" class="cards"
               ref="scrollContainer">
            <card/>
          </div>

        </div>

      </div>

      <footer class="foot">
        <div style="color: aliceblue">
          <p style="opacity: 0.4">FluxSync 1.0.0 · Made by Esther</p>
        </div>
      </footer>

    </div>


  </div>
</template>

<style scoped>

.foot {
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cards:active {
  cursor: grabbing;
}

.cards {
  cursor: grab;
}

.mobile {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.adv {
  backdrop-filter: blur(3px);
}

@media (max-width: 1024px) or (orientation: portrait) {
  .mainContain {
    margin: unset !important;
  }

  .product {
    margin-left: 20px;
    margin-right: 20px;
  }

  .adv {
    margin-left: 10px !important;
    margin-right: 10px !important;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .tbar {
    left: 0 !important;
    right: 0 !important;
  }

  .topbar {
    display: none !important;
  }

  .mobile {
    display: flex !important;
  }
}

@media (min-width: 1025px) {
  .topbar {
    display: flex !important;
  }

  .mobile {
    display: none !important;
  }
}

.titleBg {
  background-image: linear-gradient(rgba(0, 0, 0, 0.2) 0%, rgba(0, 0, 0, 0.6) 100%), url("@/assets/bg2.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  width: 100%;
  height: 600px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  border-radius: 20px;
}

.topbar:nth-child(3) {
  margin-left: auto;
}

.topbar button {
  background-color: #102D49;
  border: transparent;
  color: #137EEA;
  width: 100px;
  height: 40px;
  border-radius: 10px;
}

.topbar button:hover {
  background-color: #409EFF;
  color: aliceblue;
}

.tbar {
  display: flex;
  height: 80px;
  align-items: center;
  position: fixed;
  top: 0;
  left: 12%;
  right: 12%;
  background-color: rgba(16, 25, 34, 0.8);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
}

.tbarborder {
  display: flex;
  width: 100%;
  height: 80px;
  z-index: 999;
  position: fixed;
  top: 0;
  border-bottom: 1px solid rgba(244, 244, 245, 0.1);
  pointer-events: none;
}

.menus:hover::after {
  content: "";
  border-bottom: 1px solid aliceblue;
  width: 100%;
  display: block;
}

</style>
