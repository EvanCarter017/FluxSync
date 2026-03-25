<script setup lang="ts">

import { NDropdown } from "naive-ui";
import {h, markRaw, onMounted, ref} from "vue";
import { NAvatar, NText } from "naive-ui";
import service from "@/services/request";
import { Avatars } from "@/components/avatars/Avatars";
import config from "@/store";
import {renderToString} from "@vue/server-renderer";
import {useRouter} from "vue-router";

const Router = useRouter();
const svg = ref<null | object>(null);
const avatarUrl = ref<null | string>(null);

onMounted(async () => {

  const username = config.info.username;

  const avatar = await service.get(`/api/user/avatar/${username}`)
      .then(res => {
        return res.data.data.avatar;
      });

  const component = Avatars[avatar];
  svg.value = markRaw(component.default || component);

  const svgString = await renderToString(h(svg.value));

  const blob = new Blob([svgString], { type: 'image/svg+xml'} );
  avatarUrl.value = URL.createObjectURL(blob);
});

import option from "./options";
const options = [
  {
    key: 'header',
    type: 'render',
    render: header
  },
  ...option
]

function header() {

  const {
    uid,
    username,
    nickname,
    role,
    description
  } = config.info;

  return h(
      'div',
      {
        style: 'display: flex; align-items: center; padding: 8px 12px;'
      },
      [
        h(NAvatar, {
          round: true,
          class: 'avatar-info',
          style: 'margin-right: 12px; display: flex; justify-content: center; align-items: center;',
          src: avatarUrl.value
        }),
        h('div', null, [

          h('div', null, [
              h(NText, {
                depth: 2, style: "color: aliceblue;"
              }, {
                default: () => nickname ? nickname : username
              })
          ]),

          h('div', { style: 'font-size: 12px;' }, [
            h(
                NText,
                { depth: 3, style: "color: aliceblue; opacity: 0.5;" },
                { default: () => description ? description : "这个用户很懒，什么都没有留下" }
            )
          ])
        ])
      ]
  )
}

const handleSelect = async (key) => {
  const mod = await import(`@/components/userManager/@command/${key}.ts`);
  mod.default?.(Router);
};

</script>

<template>

  <n-dropdown
      trigger="click"
      :options="options"
      @select="handleSelect"
      placement="bottom-end"

  >
    <n-avatar round style="background-color: aliceblue;">
      <component :is="svg" />
    </n-avatar>
  </n-dropdown>
</template>

<style scoped>

</style>

<style>
.avatar-info img {
  width: 80% !important;
  height: 80% !important;
}
</style>