package com.esther.fluxsync.utils;

public final class LogConverter {

    private LogConverter() {}

    private static final String DEBUG = "\u001B[2m";
    private static final String ERROR = "\u001B[31m";
    private static final String WARN = "\u001B[33m";
    private static final String INFO = "\u001B[32m";

    private static final String BOLD = "\u001B[1m";
    private static final String RESET = "\u001B[0m";

    public static String info(String msg) { return INFO + BOLD + "[FluxSync] ✅ " + RESET + INFO + msg + RESET; }

    public static String error(String msg) { return ERROR + BOLD + "[FluxSync] ❌ " + RESET + ERROR + msg + RESET; }

    public static String debug(String msg) { return DEBUG + BOLD + "[FluxSync] \uD83D\uDCAC " + RESET + DEBUG + msg + RESET; }

    public static String warn(String msg) { return WARN + BOLD + "[FluxSync] ⚠️ " + RESET + WARN + msg + RESET; }

}
