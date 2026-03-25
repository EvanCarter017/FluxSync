function decodeBase64Url(base64Url: string): string {
    let base64 = base64Url
        .replace(/-/g, '+')
        .replace(/_/g, '/');

    while (base64.length % 4 !== 0) {
        base64 += '='
    }

    return decodeURIComponent(
        Array.from(atob(base64))
            .map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
            .join('')
    );
}

enum MessageRole {
    Creator = 1,
    Admin = 2,
    Normal = 3
}

interface MentionUser {
    uid: number
    username: string
}

interface MessageStructure {
    // 毫秒级时间戳
    timestamp: number
    // 频道号
    cid: string
    // 消息发送者uid
    uid: number
    // 消息主体
    content: string
    // 消息id
    mid: string
    // 被@的人
    mention?: []
    // 序列号
    seq: number
}

const MessageResolver = (() => {

    let methods = {
        "T": (ctx, struct) => struct.timestamp = Number(ctx),
        "C": (ctx, struct) => struct.cid = ctx,
        "U": (ctx, struct) => struct.uid = Number(ctx),
        "M": (ctx, struct) => struct.content = decodeBase64Url(ctx),
        "ID": (ctx, struct) => struct.mid = ctx,
        "AT": (ctx, struct) => {
            struct.mention = [];
            ctx.split("+").forEach(item => {
                struct.mention.push(Number(item));
            });
        },
        "SEQ": (ctx, struct) => struct.seq = Number(ctx)
    }

    return (protocol: string) => {

        const struct: Partial<MessageStructure> = {}

        protocol.split("|").forEach(item => {
            let [method, ctx] = item.split("$");
            methods[method](ctx, struct);
        });

        return struct as MessageStructure;
    };
})();

const resolve = (protocol: string): Object => {
    return [MessageResolver(protocol.payload), protocol.seq];
}

export {
    resolve
};