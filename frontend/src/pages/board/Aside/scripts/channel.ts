import {reactive, ref} from "vue";
import service from "@/services/request";
import config from "@/store";

const channelContentHeight = ref(45);

const channels = ref([
    // { name: "Group4", id: "DB359D7ED1", role: "Normal", description: "" }
]);
const roleOrder = { Creator: 1, Admin: 2, Normal: 3 };
const roleMap = {"creator": "Creator", "admin": "Admin", "normal": "Normal"};
const sortChannels = () => {
    channels.value.sort((a, b) => {
        return roleOrder[a.role] - roleOrder[b.role];
    });
}

const refresh = async () => {

    await service.get(`/api/channel/get/${config.info.uid}`)
        .then(res => {
            channels.value = [];
            if (res.status === 200) {
                config.info.channels = res.data.data;
                res.data.data.forEach(item => {
                    channels.value.push({
                        name: item.name,
                        id: item.cid,
                        role: roleMap[item.role],
                        description: item.description ?? ''
                    });
                });
            }
        }).catch(e => {
            channels.value = [];
            return ;
    });

    sortChannels();
    if (channels.value.length != 0) {
        channelContentHeight.value = channels.value.length * 45;
    }
}

const click = (item) => {
    // console.log("You clicked: " + item.role);
}

export default {
    refresh,
    channels,
    channelContentHeight,
    click
}