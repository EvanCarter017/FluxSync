import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import "@/components/avatars/avatar.css";

const app = createApp(App)

import 'element-plus/dist/index.css';
import ElementPlus from 'element-plus';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import zhCn from 'element-plus/es/locale/lang/zh-cn';

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.use(ElementPlus, {
    locale: zhCn
});

import index from "@/router";

app.use(index);

app.mount('#app');
