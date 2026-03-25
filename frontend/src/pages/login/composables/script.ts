import {ref} from "vue";

const loginStatus = ref("login" as "login" | "register" | "totp");
const totpEnable = ref(false as boolean);
const username = ref("");
const jwtrt = ref("");
const jwtat = ref("");

export {
    loginStatus,
    totpEnable,
    username,
    jwtrt,
    jwtat
}