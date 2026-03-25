import {type Ref, type Reactive, watch, reactive, type WatchStopHandle, nextTick} from "vue";
import channelOption from "./channels";

class Controller {

    // 监视容器对象
    scrollerEl: Ref<HTMLElement | null>;

    // 可视 seq 序列
    visibleList: Reactive<Set<Number>>;

    // 监视器回调对象
    observer: IntersectionObserver;

    // 重建监听器
    createObserver = (el: HTMLElement): IntersectionObserver => {
        return new IntersectionObserver(
            entries => {
                entries.forEach(entry => {
                    const seq = Number(entry.target.dataset.seq);

                    if (entry.isIntersecting) {
                        this.visibleList.add(seq);
                    } else {
                        this.visibleList.delete(seq);
                    }
                });

                // console.log([...this.visibleList]);
                channelOption.slidingPointer = [...this.visibleList].sort()[Math.floor(this.visibleList.size / 2)];

            },
            { root: el }
        );
    };

    // 状态监听器
    stateWatcher: WatchStopHandle;

    constructor(scrollerEl: Ref<HTMLElement | null>) {
        this.scrollerEl = scrollerEl;
        this.visibleList = reactive(new Set());

        const initWatch = watch(this.scrollerEl, (el) => {
            if (!el) return;

            this.observer = this.createObserver(el);

            this.stateWatcher?.();
            this.stateWatcher = watch(channelOption.messageObjects, async () => {
                if (!this.observer) return;

                const children = this.scrollerEl?.value.querySelectorAll(".msg") ?? [];

                this.observer.disconnect();

                this.visibleList.clear();

                children.forEach(el => {
                   this.observer.observe(el);
                });

            }, { flush: "post", immediate: true});

            Promise.resolve().then(() => initWatch());

        }, { immediate: true });

    }

}

let instance: Controller | null = null;

const SliderController = {
    init(scrollerEl: Ref<HTMLElement | null>): Controller {
        if (instance) return instance;
        instance = new Controller(scrollerEl);
        return instance;
    },
    get(): Controller {
      if (!instance) throw new Error("SliderControl not initialized");
      return instance;
    }
}

export { SliderController };