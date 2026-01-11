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

    public static void startTransition() {
        transitionStartMs = net.minecraft.Util.getMillis();
        transitionActive = true;
    }
}
