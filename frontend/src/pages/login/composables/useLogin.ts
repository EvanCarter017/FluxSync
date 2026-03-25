import service from "@/services/request";
import jsCookie from "js-cookie";
import {reactive} from "vue";
import {
    totpEnable,
    username,
    jwtrt,
    jwtat
} from "./script";
import {ElNotification} from "element-plus";
import config from "@/store";
import userService from "@/services/userService";

const useLogin = () => {

    const form = reactive({
        username: "" as string,
        pwd: "" as string
    });

    const login = async () => {
        return await service.post("/api/security/login", {
            username: form.username,
            password: form.pwd
        })
            .then(async (res) => {
                totpEnable.value = res.data.data.totpEnable;
                if (!totpEnable.value) {
                    ElNotification({
                        title: "登录成功",
                        message: `${form.username} 欢迎回到 FluxSync`,
                        type: "success"
                    });
                    jsCookie.set("jwt-rt", res.data.data.token.refresh_token, { expires: 15 });
                    jsCookie.set("jwt-at", res.data.data.token.access_token, { expires: 15 });
                    const useService = userService();
                    const info = await useService.getInfo();
                    if (info) {
                        config.info = info;
                    }
                    return [true, true];
                }
                username.value = form.username;
                jwtrt.value = res.data.data.token.refresh_token;
                jwtat.value = res.data.data.token.access_token;
                return [true, false];
            }).catch(e => {
                return [false, false];
        });
    }

    return {
        login,
        form
    }

};

export default useLogin;