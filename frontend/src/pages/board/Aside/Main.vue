<script setup lang="ts">

import {HomeSharp} from '@vicons/ionicons5';
import "./css/Main.css";
import menu from "./scripts/Main";
import Hub from "@/components/icons/hub.vue";
import config from "./store";
import Right from "@/components/icons/right.vue";
import {useRouter, useRoute} from "vue-router";
import {NPopover, NEllipsis} from 'naive-ui';
import {Plus, Promotion} from "@element-plus/icons-vue";
import {computed, onMounted, ref, watch} from "vue";
import DoChannel from "@/pages/board/Aside/doChannel.vue";
import channel from "./scripts/channel";
import ws from "@/services/websocket/Service";

const Router = useRouter();
const route = useRoute();

const menuContain = ref(null);
let spanWidth = ref('115px');

onMounted(() => {
  const observer = new ResizeObserver(() => {
    if (menuContain.value) {
      spanWidth.value =  `${115 + menuContain.value.offsetWidth - 240}px`;
    }
  });
  observer.observe(menuContain.value);

  channel.refresh();

});


</script>

<template>

  <!-- Main.css -->
  <div style="padding: 10px 10px;" ref="menuContain">

    <!--  主页  -->
    <div @click=" Router.push('/board'); menu.MenuActive.value = 'Home';" class="main-options"
         :class="{ 'main-options__active-box': route.path === '/board' }">
      <div class="flex alignCenter" v-if="!config.hiddenAside.value">
        <el-icon class="main-options__icon-box" :class="{ 'main-options__active-font': route.path === '/board' }">
          <HomeSharp class="main-options__icon" :class="{ 'main-options__active-icon': route.path === '/board' }"/>
        </el-icon>
        <el-text class="main-options__font" :class="{ 'main-options__active-font': route.path === '/board' }">
          主页
        </el-text>
      </div>

      <!--   收起状态   -->
      <div style="width: 100%;" v-if="config.hiddenAside.value">
        <n-popover trigger="hover" placement="right">
          <template #trigger>
            <el-icon class="main-options__hiddenicon-box"
                     :class="{ 'main-options__active-font': route.path === '/board' }">
              <HomeSharp class="main-options__hiddenicon"
                         :class="{ 'main-options__active-icon': route.path === '/board' }"/>
            </el-icon>
          </template>
          主页
        </n-popover>
      </div>
    </div>

    <!--  团队仓库  -->
    <div @click="Router.push('/board/teamhub'); menu.MenuActive.value = 'teamhub';" class="main-options">
      <div v-if="!config.hiddenAside.value" style="display: flex; width: 100%;">
        <div class="flex alignCenter">
          <el-icon class="main-options__icon-box">
            <Hub class="main-options__icon"/>
          </el-icon>
          <el-text class="main-options__font">
            团队仓库
          </el-text>
        </div>
        <div style="margin-left: auto;" class="flex alignCenter justifyCenter">
          <el-icon class="main-options__icon-box main-options__icon-hidden">
            <Promotion class="main-options__icon"/>
          </el-icon>
        </div>
      </div>

      <!--   收起状态   -->
      <div style="width: 100%;" v-if="config.hiddenAside.value">
        <n-popover trigger="hover" placement="right">
          <template #trigger>
            <el-icon class="main-options__hiddenicon-box">
              <Hub class="main-options__hiddenicon"/>
            </el-icon>
          </template>
          团队仓库
        </n-popover>
      </div>
    </div>

    <div style="max-height: calc(100vh - 272px); overflow-y: auto;">

      <!--  频道  -->
      <div class="main-channel__wrapper" v-if="!config.hiddenAside.value">

        <!-- title -->
        <div class="main-channel__title-wrapper flex alignCenter">
          <!--    展开/收起频道    -->
          <n-popover trigger="hover">
            <template #trigger>
              <el-text class="main-channel__text" @click="config.spreadChannel.value = !config.spreadChannel.value"
                       :class="{ 'main-channel__text-active': menu.MenuActive.value === 'channel' }">
                我的频道
              </el-text>
            </template>
            {{ !config.spreadChannel.value ? "点击收起" : "点击展开" }}
          </n-popover>
          <!--   加入频道     -->
          <div class="main-channel__icon-wrapper flex alignCenter">
            <n-popover trigger="hover">
              <template #trigger>
                <el-icon @click="menu.channel.value = true;">
                  <Plus class="main-channel__icon"/>
                </el-icon>
              </template>
              创建/加入 频道
            </n-popover>
          </div>
          <DoChannel v-if="menu.channel.value" />
        </div>

        <!--   content   -->
        <div style="width: 100%; margin-top: 10px; overflow-y: hidden; transition: height 0.3s ease-in;"
             :style="{ height: !config.spreadChannel.value ? `${channel.channelContentHeight.value}px` : '0' }">

          <template v-if="ws.ready">
            <div v-if="channel.channels.value.length == 0" class="main-channel__content-wrapper" @click="menu.channel.value = true;">
              <div class="flex alignCenter">
                <el-icon style="padding-left: 35px; color: #BEC4C9;">
                  <Plus />
                </el-icon>
                <n-ellipsis class="main-channel__font" style="color: #909399; padding-left: 10px;" :style="`max-width: ${spanWidth} !important;`">
                  创建 / 加入 频道
                </n-ellipsis>
              </div>
            </div>

            <template v-else>
              <div v-for="item in channel.channels.value" :key="item.id" class="main-channel__content-wrapper"
                   @click="channel.click(item); Router.push(`/board/channel?_cid=${item.id}`)"
                   :class="{ 'main-options__active-box': route.path === `/board/channel` && route.query._cid == item.id }"
              >
                <n-popover trigger="hover">
                  <template #trigger>
                    <div class="flex alignCenter">
                      <n-ellipsis class="main-channel__font" :style="`max-width: ${spanWidth} !important;`"
                                  :class="{ 'main-options__active-font': route.path === `/board/channel` && route.query._cid == item.id }"
                      >
                        {{ item.name }}
                      </n-ellipsis>
                    </div>
                  </template>
                  {{ item.name }}
                </n-popover>

                <el-text class="main-channel__roletag" :class="{
                  'main-channel-Creator' : item.role === 'Creator',
                  'main-channel-Admin' : item.role === 'Admin',
                  'main-channel-Member' : item.role === 'Normal'
                }">
                  {{ item.role }}
                </el-text>
              </div>
            </template>
          </template>

        </div>

      </div>

      <!--  本地仓库  -->
      <div class="main-channel__wrapper" v-if="!config.hiddenAside.value">

        <!-- title -->
        <div class="main-channel__title-wrapper flex alignCenter">
          <!--    展开/收起仓库    -->
          <n-popover trigger="hover">
            <template #trigger>
              <el-text class="main-channel__text" @click="config.spreadLocalStore.value = !config.spreadLocalStore.value">
                本地仓库
              </el-text>
            </template>
            {{ config.spreadLocalStore.value ? "点击收起" : "点击展开" }}
          </n-popover>
          <!--   新建文档     -->
          <div class="main-channel__icon-wrapper flex alignCenter">
            <n-popover trigger="hover">
              <template #trigger>
                <el-icon>
                  <Plus class="main-channel__icon"/>
                </el-icon>
              </template>
              新建文档
            </n-popover>
          </div>
        </div>

      </div>

    </div>

  </div>

</template>