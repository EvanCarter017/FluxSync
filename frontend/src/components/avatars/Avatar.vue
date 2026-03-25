<script setup lang="ts">
defineOptions({ inheritAttrs: false });
import {Avatars} from "@/components/avatars/Avatars";
import { defineProps, ref, onMounted, computed, nextTick, useAttrs } from 'vue';
import { NAvatar } from 'naive-ui';

const attrs = useAttrs();

const props = defineProps<{
  name: keyof typeof Avatars;
  size?: keyof 'small' | 'medium' | 'large' | number;
}>();

const Component = computed(() => Avatars[props.name]);

const wrapper = ref<HTMLElement | null>(null);
const imgSrc = ref<string>('');

onMounted(async () => {
  await nextTick();
  if (!wrapper.value) return;

  const svg = wrapper.value.querySelector('svg');
  if (!svg) return;

  const color = getComputedStyle(wrapper.value).color;

  let str = new XMLSerializer().serializeToString(svg);
  str = str.replace(/stroke="currentColor"/g, `stroke="${color}"`);

  imgSrc.value = `data:image/svg+xml;base64,${btoa(str)}`;

  wrapper.value.style.display = 'none';

});

</script>

<template>

  <div ref="wrapper" style="display: inline-block;">
    <component :is="Component" />
  </div>

  <n-avatar
      :style="attrs.style"
      style="user-select: none; padding: 5px;"
      :class="attrs.class"
      round
      v-if="imgSrc"
      :src="imgSrc"
      :size="props.size"
  />

</template>

<style scoped>

</style>