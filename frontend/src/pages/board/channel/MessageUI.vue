<script setup lang="ts">
import channelOption from "./channels";
import Avatar from "@/components/avatars/Avatar.vue";
import {defineProps, inject, nextTick, onBeforeMount, onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import { SliderController } from "@/pages/board/channel/SliderController";

const props = defineProps<{
  everyOne: keyof any[];
}>();

function formatTimestamp(ms) {
  const d = new Date(ms);

  const YYYY = d.getFullYear();
  const MM = String(d.getMonth() + 1).padStart(2, '0');
  const DD = String(d.getDate()).padStart(2, '0');
  const HH = String(d.getHours()).padStart(2, '0');
  const mm = String(d.getMinutes()).padStart(2, '0');

  return `${YYYY}/${MM}/${DD} ${HH}:${mm}`;
}

function findField(uid, field) {
  const obj = props.everyOne.filter(u => u.uid == uid)[0];
  return obj?.[field];
}

const scrollerEl = inject("Channel-ScrollerEl");

onMounted(async () => {

  const instance = SliderController.init(scrollerEl);

});

</script>

<template>
  <div v-for="(item, index) in channelOption.messageObjects.value" :key="item.mid" style="padding: 10px;" class="msg" :data-seq="item.seq">

    <div style="display: flex; width: 100%;">
      <div style="height: 50px; width: 50px; margin-right: 10px;" class="flex justifyCenter alignCenter">
        <Avatar :name="item.avatar" size="small" />
      </div>
      <div style="width: calc(100% - 70px); height: 100%;">
        <div style="display: flex; align-items: center;">
          <span style="color: aliceblue; font-size: 16px; margin-right: 10px;">{{ item.nickname }}</span>
          <span style="color: aliceblue; margin-right: 5px;">|</span>
          <span style="font-size: 14px; margin-right: 5px; font-weight: bold; border-radius: 10px; padding: 0 5px;"
                :style="{
                    color: findField(item.uid, 'role') === 'admin' ? '#7B68EE' :
                           findField(item.uid, 'role') === 'creator' ? '#DAA520' : 'aliceblue'
                }">{{ findField(item.uid, "role") }}</span>
          <span style="color: aliceblue; margin-right: 10px;">|</span>
          <span style="color: #94959C; font-size: 12px"> {{ formatTimestamp(item.timestamp) }} </span>
        </div>
        <div>
          <span style="color: #CCCDD0;">{{item.content}}</span>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>

</style>