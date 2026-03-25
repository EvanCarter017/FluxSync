import {reactive, ref} from "vue";

type UserInfo = {
    uid: number,
    username: string,
    nickname: string,
    role: string,
    description: string,
    usedBytes: string,
    limitBytes: string,
    usedBytesNumber: string,
    limitBytesNumber: string,
    channels: object[]
}

export default {

    // 用户信息
    info: reactive<UserInfo>({
        uid: 0,
        username: "",
        nickname: "",
        role: "",
        description: "",
        usedBytes: "",
        limitBytes: "",
        usedBytesNumber: "",
        limitBytesNumber: "",
        channels: []
    }),

    avatars: ref()

}