import { Ref } from 'vue';

export function useDragScroll(scrollContainer: Ref<HTMLElement | null>) {
    let isMouseDown = false;
    let startX: number;
    let scrollLeft: number;

    const onMouseDown = (e: MouseEvent) => {
        if (!scrollContainer.value) return;
        isMouseDown = true;
        startX = e.pageX - scrollContainer.value.offsetLeft;
        scrollLeft = scrollContainer.value.scrollLeft;
    };

    const onMouseUpOrLeave = () => {
        if (!scrollContainer.value) return;
        isMouseDown = false;
    };

    const onMouseMove = (e: MouseEvent) => {
        if (!isMouseDown || !scrollContainer.value) return;
        const x = e.pageX - scrollContainer.value.offsetLeft;
        const walk = (x - startX) * 1.1;
        scrollContainer.value.scrollLeft = scrollLeft - walk;
    };

    const init = () => {
        if (!scrollContainer.value) return;
        scrollContainer.value.scrollLeft = 100;
    }

    return {
        onMouseDown,
        onMouseUpOrLeave,
        onMouseMove,
        init
    };
}