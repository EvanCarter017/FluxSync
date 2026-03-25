import { h } from 'vue';
import { NIcon } from 'naive-ui';
import { LogOutOutline } from '@vicons/ionicons5'; // xicons
import { User, Help } from '@vicons/carbon';

export default [
    {
        key: 'header-divider',
        type: 'divider'
    },

    {
        key: 'profile',
        label: '个人信息',
        icon() {
            return h(NIcon, null, {
                default: () => h(User)
            })
        }
    },

    {
        key: 'helper',
        label: '帮助中心',
        icon() {
            return h(NIcon, null, {
                default: () => h(Help)
            })
        }
    },

    {
        key: 'header-divider',
        type: 'divider'
    },

    {
        key: 'logout',
        label: '退出登录',
        icon() {
            return h(NIcon, null, {
                default: () => h(LogOutOutline)
            })
        }
    },
]