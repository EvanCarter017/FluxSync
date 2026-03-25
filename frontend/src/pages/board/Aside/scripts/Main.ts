import {ref, reactive} from "vue";

const MenuActive = ref<
    "Home" | "teamhub" | "channel"
>("Home");

const exec = (option) => {
    MenuActive.value = option;
}

const channel = ref(false);

export default {
    MenuActive,
    exec,
    channel
}