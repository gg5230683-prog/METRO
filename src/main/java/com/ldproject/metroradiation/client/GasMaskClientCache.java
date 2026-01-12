package com.ldproject.metroradiation.client;

/**
 * Кэш данных противогаза на клиенте (синхронизируется с сервера)
 */
public class GasMaskClientCache {
    public static boolean hasGasMask = false;
    public static int filterTime = 0;
    public static int durability = 1000;
    public static boolean transitionActive = false;
    public static long transitionStartMs = 0L;
    public static final long TRANSITION_DURATION_MS = 180L;
    public static long filterDisplayStartMs = 0L;
    public static final long FILTER_DISPLAY_DURATION_MS = 2000L;

    public static void startTransition() {
        transitionStartMs = net.minecraft.Util.getMillis();
        transitionActive = true;
    }

    public static void startFilterDisplay() {
        filterDisplayStartMs = net.minecraft.Util.getMillis();
    }
}
