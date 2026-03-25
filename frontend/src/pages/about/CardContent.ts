import security from "@/components/icons/security.vue";
import setting from "@/components/icons/setting.vue";
import Storage from "@/components/icons/storage.vue";
import History from "@/components/icons/history.vue";
import Team from "@/components/icons/team.vue";

export const contents = [
    {
        headerExtra: "由 FluxSync security 提供",
        icon: security,
        default: "账户安全保障",
        main: "借助目前主流的安全解决方案，采用 jwt + totp 双重防护, 保护您的账户免受未授权访问, 确保身份验证的安全性与时效性, 降低被攻击的风险, 提升用户数据的隐私性与完整性。",
        id: 1
    },
    {
        headerExtra: "由 FluxSync Editor 提供",
        icon: setting,
        default: "实时协同编辑",
        main: "使用 WebSocket 与 CRDT 技术, 实现多人同时在线撰写与编辑, 文档毫秒级同步, 保障团队协作高效、流畅且无冲突。",
        id: 2
    },
    {
        headerExtra: "由 FluxSync Editor 提供",
        icon: History,
        default: "版本快照与恢复",
        main: "支持文档历史版本快照与一键恢复，轻松追踪修改记录，确保团队在协作过程中不会丢失重要信息。",
        id: 3
    },
    {
        headerExtra: "由 FluxSync Storage 提供",
        icon: Storage,
        default: "安全稳定存储",
        main: "基于 MinIO 对象存储，实现高可靠、高可用的文件管理，支持自动备份与版本控制，多设备随时访问，确保团队数据安全与完整性。",
        id: 4
    },
    {
        headerExtra: "由 FluxSync Team 提供",
        icon: Team,
        default: "高效团队协作",
        main: "提供项目组织、任务分配与团队权限管理功能，让团队协作更高效，信息更清晰透明。",
        id: 5
    }
];