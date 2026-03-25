import {
    ref,
    reactive
} from "vue";

export default {

    /**
     * 是否隐藏侧边栏
     */
    hiddenAside: ref<boolean>(false),

    /**
     * 频道列表 收起 / 展开
     */
    spreadChannel: ref<boolean>(false),

    /**
     * 本地仓库 收起 / 展开
     */
    spreadLocalStore: ref<boolean>(false),

}