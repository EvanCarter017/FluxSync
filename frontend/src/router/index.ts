import { createRouter, createWebHistory } from "vue-router";
import ws from '@/services/websocket/Service';

const routes = [
    {
        path: "/",
        name: "about",
        component: () => import("@/pages/about/about.vue"),
        meta: { dark: true }
    },
    {
        // 登录页
        path: "/login",
        name: "login",
        component: () => import("@/pages/login/index.vue"),
        meta: { dark: true }
    },
    {
        // 控制台
        path: "/board",
        component: () => import("@/pages/board/Aside/layout.vue"),
        children: [
            {
                // 控制台首页
                path: '',
                name: 'Home',
                component: () => import("@/pages/board/home/index.vue")
            },
            {
                // 团队仓库
                path: 'teamhub',
                name: 'teamhub',
                component: () => import("@/pages/board/teamhub/index.vue")
            },
            {
                // 频道
                path: 'channel',
                name: 'channel',
                component: () => import("@/pages/board/channel/index.vue")
            }
        ]
    }
];

const index = createRouter({
    history: createWebHistory(),
    routes,     
});

index.beforeEach((to, from, next) => {
    document.body.style.backgroundColor = to.meta?.dark
        ? "#101922"
        : "#1A1A1A";

    const resolved = index.resolve(to.path);
    const isValid = resolved.matched.length > 0;

    if (!isValid &&
        !(
            to.path.startsWith('/board/editor') ||
            to.path.startsWith('/board/channel')
        )
    ) {
        return next('/');
    }

    if (to.path.startsWith('/board/channel')) {
        if (!ws.ready) {
            return next('/board');
        }

        if (!to.query._cid) {
            return next('/board');
        }
        if (Object.keys(to.query).length > 1) {
            return next('/board');
        }
    }

    if (to.path !== '/' && to.path.endsWith('/')) {
        const normalized = to.path.replace(/\/+$/, '');
        return next({ ...to, path: normalized, replace: false });
    }

    next();
});


export default index;
