class TabLeader {

    isLeader: boolean;

    constructor(channelName = 'flux-sync') {
        this.channel = new BroadcastChannel(channelName);
        this.isLeader = false;
        this.onLeaderCallbacks = [];
        this.onFollowerCallbacks = [];

        this.beginElection();
    }

    beginElection() {
        // 询问是否已有 leader
        this.channel.postMessage({type: 'request-leader'});

        this.channel.onmessage = (e) => {
            const msg = e.data;

            // 说明已经有 leader 存在
            if (msg.type === 'declare-leader') {
                if (!this.isLeader) {
                    clearTimeout(this.electionTimer);
                    this.becomeFollower();
                }
            }

            // 有人询问 leader，而我就是
            if (msg.type === 'request-leader' && this.isLeader) {
                this.channel.postMessage({type: 'declare-leader'});
            }

            // leader 下线 → 重新选举
            if (msg.type === 'leader-offline') {
                this.restartElection();
            }
        };

        // 如果 100ms 内没人回应 declare → 自主选举
        this.electionTimer = setTimeout(() => {
            this.becomeLeader();
        }, 100);
    }

    restartElection() {
        clearTimeout(this.electionTimer);
        this.electionTimer = setTimeout(() => {
            this.becomeLeader();
        }, 150);
    }

    becomeLeader() {
        if (this.isLeader) return;
        this.isLeader = true;
        this.channel.postMessage({type: 'declare-leader'});

        this.onLeaderCallbacks.forEach((fn) => fn());
    }

    becomeFollower() {
        if (!this.isLeader) return;
        this.isLeader = false;

        this.onFollowerCallbacks.forEach((fn) => fn());
    }

    // Leader 主动下线时调用
    declareOffline() {
        if (this.isLeader) {
            this.isLeader = false;
            this.channel.postMessage({type: 'leader-offline'});
        }
    }

    onLeader(fn) {
        this.onLeaderCallbacks.push(fn);
    }

    onFollower(fn) {
        this.onFollowerCallbacks.push(fn);
    }
}

export default new TabLeader();
