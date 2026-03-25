import {reactive, ref, watch} from "vue";
import type { MentionOption } from 'element-plus';
import service from "@/services/request";
import {
    resolve
} from "@/services/MessageResolver";

class Channels {

    constructor() {
        watch(this.inputs, (value) => {
            if (value == "") return ;
            this.optionUsers.value = this.extractMentions(value);
        });
    }

    // 监听高度
    watchHeight(el, cb) {
        if (!el) return ;
        const ro = new ResizeObserver(entries => {
           cb(entries[0].contentRect.height);
        });
        ro.observe(el);
        return () => ro.disconnect();
    }

    // 隐藏(false)/显示(true)成员名单
    visGroup = ref(false);

    // 用户输入内容
    inputs = ref("");

    // 内容区高度
    contentHeight = ref(0);

    // 实时流/历史流
    isRealTime = ref<boolean>(false);

    // 消息协议序列
    // "T$1766422777382|C$B398737F0F|U$10001|M$QEV2YW4gQEp1ZGkgSGF2ZSB5b3UgZmluaXNoZWQgeW91ciByZXBvcnQ_|ID$B398737F0F019b4720-a085-70ab-9c95-51376ea95feb|AT$10002+10003|SEQ$1"
    messageProtocols = ref([]);

    // 消息流上下界
    minSeq =  ref<number>(0);
    maxSeq = ref<number>(0);

    // 消息流滑动指针 - 基于 seq
    slidingPointer = ref(0);

    // 解析后的消息协议结构
    messageObjects = ref([]);

    // 缓存
    tempMapper = reactive({});

    // 更新消息协议序列
    async updateHistoryMessageProtocols(cid: string, seq: number | -1) {

        let length;
        await service.get(`/api/channel/messages?cid=${cid}&seq=${seq}`).then(res => {
            this.messageProtocols.value = res.data.data.messages;
            length = res.data.data.length;
        });

        if (this.isRealTime.value) return;

        this.messageObjects.value = [];

        await Promise.all(
            this.messageProtocols.value.map(async item => {
                const [msg, seq] = resolve(item);

                const nicknameQuery = `/api/user/nfind/${msg.uid}`;
                const usernameQuery = `/api/user/find/${msg.uid}`;

                if (!this.tempMapper[nicknameQuery]) {
                    this.tempMapper[nicknameQuery] =
                        service.get(nicknameQuery).then(res => res.data.data.nickname);
                }

                if (!this.tempMapper[usernameQuery]) {
                    this.tempMapper[usernameQuery] =
                        service.get(usernameQuery).then(res => res.data.data.username);
                }

                const [nickname, username] = await Promise.all([
                    this.tempMapper[nicknameQuery],
                    this.tempMapper[usernameQuery]
                ]);

                msg.nickname = nickname;

                const avatarQuery = `/api/user/avatar/${username}`;
                if (!this.tempMapper[avatarQuery]) {
                    this.tempMapper[avatarQuery] =
                        service.get(avatarQuery).then(res => res.data.data.avatar);
                }

                msg.avatar = await this.tempMapper[avatarQuery];

                this.messageObjects.value.push(msg);

            })
        );

        this.messageObjects.value.sort((a, b) => a.timestamp - b.timestamp);
        this.minSeq.value = this.messageObjects.value[0]?.seq;
        this.maxSeq.value = this.messageObjects.value[this.messageObjects.value.length - 1]?.seq;
    }

    // 提及表
    options = ref<MentionOption[]>([]);
    allUsers = ref<string[]>([]);
    optionUsers = ref(new Set<string>()); // 被提及的目标
    optionsLoading = ref(false);
    optionsTimer: number | null = null;
    handleSearch = async (pattern: string, cid: string, role: string) => {
        if (this.optionsTimer !== null) return ;

        // 启动加载
        this.optionsLoading.value = true;
        this.options.value = [];

        // 拿群组信息
        await service.get(`/api/channel/search/${cid}`)
            .then(res => {
                if (res.status === 200) {
                    this.allUsers.value = res.data.data;
                }
            });

        this.options.value.push({
            label: "everyone",
            value: "everyone",
            data: {
                uid: -1,
                nickname: 'everyone',
                username: "everyone",
            },
            unable: (role != "creator") && (role != "admin"),
            selected: -1
        });

        this.allUsers.value.forEach(item => {
            this.options.value.push({
                label: (item.nickname ?? item.username).toString(),
                value: (item.nickname ?? item.username).toString(),
                data: item,
                selected: item.uid.toString()
            });
        });
        this.optionsLoading.value = false;
        this.optionsTimer = setTimeout(() => {
            this.optionsTimer = null;
        }, 500);
    }
    extractMentions = (value): Set<string> => {
        const result = new Set<string>();
        const reg = /@(\w+)/g;
        let m: RegExpExecArray | null;
        while ((m = reg.exec(value)) !== null) {
            const name = m[1];
            const exp = this.options.value.filter(item => item.data.nickname == name || item.data.username == name);
            if (exp[0]) {
                result.add(exp[0].selected);
            }
        }
        return result;
    }

    // 发送信息
    sendMessage = () => {
        if (this.inputs.value == "") {
            return ;
        }
        console.log(this.inputs.value + " -> ", this.optionUsers.value);

        this.inputs.value = "";
    }

}

export default new Channels();

