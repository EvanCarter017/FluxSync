import service from "@/services/request";
import jsCookie from "js-cookie";
import useToken from "@/services/token";
import { ElNotification } from 'element-plus';

const userService = () => {

    const token = useToken();

    // 获取用户信息
    const getInfo = async () => {
        const refresh_token = jsCookie.get('jwt-rt');
        if (refresh_token != undefined) {
            const access_token = await token.refresh(refresh_token);
            if (access_token == -1) {
                ElNotification({
                    title: '网络错误',
                    type: 'error'
                });
                return null;
            }
            if (access_token != undefined) {
                jsCookie.set('jwt-at', access_token, { expires: 7 });
                return await service.get("/api/me")
                    .then(res => {
                        return res.data.data;
                    }).catch(e => {
                        return -1;
                    })
            } else {
                jsCookie.remove('jwt-rt');
                return null;
            }
        }
        return null;
    }

    return {
        getInfo
    }

}

export default userService;