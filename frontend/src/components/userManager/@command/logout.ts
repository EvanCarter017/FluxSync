import jsCookie from "js-cookie";
import {ElNotification} from "element-plus";
import config from "@/store";

export default async function logout(Router) {

    const username = config.info.username;

    jsCookie.remove("jwt-at");
    jsCookie.remove("jwt-rt");

    config.info.uid = 0;
    config.info.username = "";
    config.info.nickname = "";
    config.info.role = "";
    config.info.description = "";

    await Router.replace("/login");

    ElNotification({
        title: "已退出登录",
        message: `${username}, 期待您的再次归来.`,
        type: "success"
    });

}