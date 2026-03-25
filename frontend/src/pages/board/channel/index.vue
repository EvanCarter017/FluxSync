<script setup lang="ts">

import {nextTick, onMounted, provide, reactive, ref} from "vue";
import {useRouter, useRoute} from "vue-router";
import channel from "@/pages/board/Aside/scripts/channel";
import "./css/channel-index.css";
import service from "@/services/request";
import Group from "@/components/icons/group.vue";
import {NPopover, NAvatar} from "naive-ui";
import config from "@/store";
import Avatar from "@/components/avatars/Avatar.vue";

const Router = useRouter();
const route = useRoute();

const cid = ref("");
let cinfo = ref({});
const everyOne = ref([]);
const ownerID = ref(null);
const ownerName = ref("");

const contentsBox = ref(null);
provide("Channel-ScrollerEl", contentsBox);

onMounted(async () => {

  await nextTick();

  const qcid = route.query._cid;

  if (!qcid) {
    await Router.push("/board");
    return;
  }

  if (qcid.length != 10 || !channel.channels.value.some(item => item.id == qcid)) {
    await Router.push("/board");
    return ;
  } else {
    cid.value = qcid;
    cinfo.value = channel.channels.value.find(item => item.id == qcid);
  }

  channelOption.watchHeight(contentsBox.value, h => channelOption.contentHeight.value = h);
  await channelOption.updateHistoryMessageProtocols(cid.value,-1);
  channelOption.isRealTime.value = true;

  contentsBox.value.scrollTop = contentsBox.value.scrollHeight ?? 0;

  contentsBox.value.addEventListener('scroll', () => {
    channelOption.isRealTime.value = !(contentsBox.value.scrollTop + contentsBox.value.clientHeight <= contentsBox.value.scrollHeight - contentsBox.value.clientHeight);
  });

  await service.get(`/api/channel/find/${qcid}`)
      .then(async res => {

        if (res.status === 200) {
          res.data.data.forEach(item => {
            everyOne.value.push({
              uid: item.uid,
              cid: item.cid,
              jointime: item.jointime,
              role: item.role
            });
          });

          ownerID.value = everyOne.value.find(item => item.role == 'creator').uid;
          await service.get(`/api/user/find/${ownerID.value}`).then(res => ownerName.value = res.data.data.username);
        }

      }).catch(err => {

        Router.push("/board");

      });

});

import channelOption from "./channels";
import {Promotion} from "@element-plus/icons-vue";
import MessageUI from "@/pages/board/channel/MessageUI.vue";

</script>

<template>

  <!-- channel-index.css -->
  <div class="channel-index__container">

    <!--  top  -->
    <div style="
      display: flex;
      border-bottom: 1px solid rgba(240, 248, 255, .2);
      width: 100%;
    ">

      <div class="channel-index__top-container-left">
        <el-text
            style="color: aliceblue; margin-left: 20px; font-size: 22px; height: 100%; display: flex; align-items: center;">
          {{ cinfo.name }}
          <el-text class="channel-index__top-badge" :class="[
            `channel-index__top-badge-${cinfo.role}`
        ]">
            [ {{ cinfo.role }} ]
          </el-text>
        </el-text>
        <el-divider direction="vertical" v-if="cinfo.description"/>
        <div style="padding-top: 3px; height: 100%; display: flex; align-items: center;">
          <el-text style="
              color: rgba(240 ,248, 255, .6);
              font-size: 12px;
              max-width: 800px;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
              display: inline-block;
          ">
            {{ cinfo.description }}
          </el-text>
        </div>
      </div>

      <div class="channel-index__top-container-right">
        <n-popover trigger="hover" placement="bottom-end">
          <template #trigger>
            <div class="channel-index__top-icon-group" @click="() => {
              channelOption.visGroup.value = !channelOption.visGroup.value;
            }">
              <Group style="color: aliceblue; width: 22px;"/>
            </div>
          </template>
          <span>
            {{ `${channelOption.visGroup.value ? '隐藏' : '显示'}成员名单` }}
          </span>
        </n-popover>
      </div>

    </div>

    <!--  content  -->
    <div style="
        /*background-color: rgba(255, 192, 203, .2);*/
        display: flex;
        width: 100%;
        height: 100%;
        justify-content: space-between;
        flex-direction: column;
    ">

      <!--   contents   -->
      <div style="display: flex; flex-direction: column; height: 100%; ">

        <!--    消息区    -->
        <div style="width: 100%; height: 100%; min-height: 0; ">
          <!--          <Avatar name="cat" size="small" style="width: 50px; height: 50px;" />-->
          <div ref="contentsBox" class="scroller"
               style="padding: 10px; height: calc(100vh - 80px - 105px); overflow-y: auto;">
            <MessageUI :every-one="everyOne" />
          </div>
        </div>

        <!--    用户显示区    -->

      </div>

      <!--  input-box  -->
      <div style="padding: 5px;">
        <el-mention
            :maxlength="1200"
            v-model="channelOption.inputs.value"
            :placeholder="`给 ${cinfo.name} 发消息`"
            style="height: 50px;"
            @keydown.shift.enter.prevent="channelOption.sendMessage()"
            :loading="channelOption.optionsLoading.value"
            :options="channelOption.options.value"
            whole
            @search="(pattern: string) => channelOption.handleSearch(pattern, cid, cinfo.role)"
        >
          <template #label="{ item }">
            <div style="display: flex;">

              <div style="display: flex; justify-content: center; align-items: center;" v-if="item.data.uid !== -1">
                <Avatar :name="item.data.avatar" size="small" style="width: 16px; height: 16px;"/>
              </div>

              <div style="display: flex; align-items: center">
              <span style="margin-left: 6px" :style="{
                color: item.data.uid === -1 ? '#7B68EE' : '',
                fontWeight: item.data.uid === -1 ? 'bold': ''
              }">{{ item.data.nickname ?? item.data.username }}</span>
                <span v-if="item.data.uid !== -1" :style="{
                  color: ({ creator: '#DAA520', admin: '#7B68EE' }[item.data.role] || '')
                }" style="font-weight: bold;">
                  &nbsp;{{ `${item.data.role}` }}
                </span>
              </div>
            </div>
          </template>

          <!--suppress VueUnrecognizedSlot -->
          <template #suffix>
            <span class="el-input__count" v-if="channelOption.inputs.value.length !== 0">
              <span class="el-input__count-inner">{{ channelOption.inputs.value.length }} / 1200</span>
            </span>
            <n-popover trigger="hover" placement="top-end" v-if="channelOption.inputs.value != ''">
              <template #trigger>
                <el-icon size="22" class="send-icon" @click="channelOption.sendMessage();">
                  <Promotion/>
                </el-icon>
              </template>
              <span>发送消息</span>
            </n-popover>
          </template>
        </el-mention>
      </div>

    </div>

    <!--   groups   -->
    <div style="display: none">

    </div>

  </div>

</template>

<style scoped>

:deep(.el-input__wrapper) {
  border-radius: 10px;
  background-color: #393A41;
  --el-input-border-color: #393A41;
  --el-input-text-color: aliceblue;
  --el-input-focus-border-color: #44454C;
  --el-input-hover-border-color: transparent;
  caret-color: aliceblue;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 5px rgba(240, 248, 255, .5);
}

:deep(.el-icon.send-icon:hover) {
  color: aliceblue;
  cursor: pointer;
}

:deep(.el-input__count-inner) {
  background: unset !important;
  color: aliceblue;
  margin-right: 10px;
}


</style>