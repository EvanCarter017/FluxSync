<script setup lang="ts">

import {NModal, NCard} from 'naive-ui';
import {reactive, ref} from 'vue';
import menu from './scripts/Main';
import "./css/channel.css"
import {ElNotification} from "element-plus";
import config from "@/store";
import service from "@/services/request";
import channel from "./scripts/channel";

const show = ref<boolean>(true);
const tabs = ref<'create' | 'join'>('create');

const form = reactive({
  channelName: "",
  channelDesc: "",
  channelId: ""
});

const formClear = () => {
  form.channelName = "";
  form.channelDesc = "";
  form.channelId = "";
}

const createChannel = () => {

  if (form.channelName == "") {
    ElNotification({
      title: "创建失败",
      message: "请输入频道名称",
      type: "error"
    });
    return ;
  }

  const body = {
    uid: config.info.uid,
    name: form.channelName,
  }

  if (form.channelDesc != "") {
    body.description = form.channelDesc;
  }

  service.post("/api/channel/create", body)
      .then(res => {
        if (res.status == 200) {
          ElNotification({
            title: "创建成功",
            message: `频道 ${form.channelName} 已创建`,
            type: "success"
          });
          channel.refresh();
        }
      }).catch(e => {
        if (e.status == 400) {
          ElNotification({
            title: "创建失败",
            type: "error"
          });
          channel.refresh();
        }
  });

  menu.channel.value = false

};

const joinChannel = () => {

  if (form.channelId == "" || form.channelId.length != 10) {
    ElNotification({
      title: "加入失败",
      message: "请检查频道号是否正确",
      type: "error"
    });
    return ;
  }

  service.post("/api/channel/join", {
    uid: config.info.uid,
    cid: form.channelId,
  }).then(res => {

    if (res.status == 200) {
      ElNotification({
        title: '加入成功',
        message: `欢迎加入频道`,
        type: "success"
      });
      channel.refresh();
    }

  }).catch(e => {
    ElNotification({
      title: "加入失败",
      message: "您已加入该频道.",
      type: "warning"
    });
    channel.refresh();
  });
  menu.channel.value = false

};

</script>

<template>

  <n-modal v-model:show="show" :on-after-leave="() => { menu.channel.value = false; formClear(); }" :mask-closable="false">

    <!--  channel.css  -->
    <div class="Dochannel__wrapper">

      <div class="Dochannel__title-wrapper">
        <el-text class="Dochannel__title">
          创建或加入频道
        </el-text>
        <el-text>
          选择一个选项以继续
        </el-text>
      </div>

      <div style="margin-top: 20px;">
        <el-tabs v-model="tabs" class="tabs">

          <el-tab-pane label="创建" name="create">
            <el-text style="font-size: 12px;">
              频道名称
            </el-text>
            <el-input v-model="form.channelName" type="text" placeholder="请输入频道名称"
                      style="margin-top: 5px; height: 40px; margin-bottom: 20px;" />

            <el-text style="font-size: 12px;">
              频道描述 (可选)
            </el-text>
            <el-input v-model="form.channelDesc" type="textarea" placeholder="简单描述一下你的频道"
                      style="margin-top: 5px; height: 100px;" />

            <div style="margin-top: 15px; display: flex; justify-content: right;">
              <el-button style="border-radius: 10px;" @click="menu.channel.value = false">
                取消
              </el-button>
              <el-button @click="createChannel" style="border-radius: 10px;" type="primary">
                创建
              </el-button>
            </div>

          </el-tab-pane>

          <el-tab-pane style="padding-left: 0;" label="加入" name="join">

            <el-text style="font-size: 12px;">
              频道号
            </el-text>
            <el-input v-model="form.channelId" type="text" placeholder="请输入频道号"
                      style="margin-top: 5px; height: 40px; margin-bottom: 20px;" />

            <div style=" display: flex; justify-content: right;">
              <el-button style="border-radius: 10px;" @click="menu.channel.value = false">
                取消
              </el-button>
              <el-button @click="joinChannel" style="border-radius: 10px;" type="primary">
                加入
              </el-button>
            </div>

          </el-tab-pane>

        </el-tabs>
      </div>

    </div>

  </n-modal>

</template>

<style scoped>
:deep(.el-text) {
  align-self: unset;
}

:deep(.tabs > .el-tabs__content) {
  padding-top: 10px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
}
:deep(.el-textarea__inner) {
  border-radius: 10px;
  max-height: 100px !important;
  height: 100px;
  min-height: 100px !important;
}
</style>