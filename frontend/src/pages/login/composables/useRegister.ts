import service from "@/services/request";
import {reactive} from "vue";
import {ElNotification} from "element-plus";
import {
    loginStatus
} from "@/pages/login/composables/script";

const useRegister = () => {

    const form = reactive({
       username: "" as string,
       pwd: "" as string,
       pwd2: "" as string
    });

    const register = () => {

        return service.post("/api/security/register", {
            username: form.username,
            password: form.pwd
        })
            .then(res => {
                ElNotification({
                   title: "注册成功",
                   message: "欢迎来到 FluxSync! 请登录您的账户.",
                   type: "success"
                });
                loginStatus.value = "login";
                return [true, true];
            }).catch(e => {
                if (e.status === 409) {
                    ElNotification({
                        title: "注册失败",
                        message: "用户已存在",
                        type: "error"
                    });
                    return [true, false];
                } else {
                    ElNotification({
                        title: "注册失败",
                        message: "未知错误",
                        type: "error"
                    });
                    return [false, false];
                }
        });

    }

    return {
        register,
        form
    }
}

export default useRegister;