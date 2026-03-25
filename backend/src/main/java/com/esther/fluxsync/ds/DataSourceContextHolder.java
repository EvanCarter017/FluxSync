package com.esther.fluxsync.ds;

import java.util.ArrayDeque;
import java.util.Deque;

public class DataSourceContextHolder {

    private static final ThreadLocal<Deque<String>> CTX =
            ThreadLocal.withInitial(ArrayDeque::new);

    private DataSourceContextHolder() {}

    // 压栈
    public static void set(String key) {
        if (key == null || key.isEmpty()) return;
        CTX.get().push(key);
    }

    // peek 栈顶
    public static String get() {
        Deque<String> d = CTX.get();
        return d.isEmpty() ? null : d.peek();
    }

    public static void clear() {
        Deque<String> d = CTX.get();
        if (!d.isEmpty()) d.pop();
        if (d.isEmpty()) CTX.remove(); // 防泄漏处理
    }

}
