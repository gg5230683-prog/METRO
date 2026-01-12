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
    public static final long FILTER_DISPLAY_DURATION_MS = 2000L; // Всегда показываем таймер 2 секунды
    public static boolean forceShowTimer = false;
    
    // Для плавного появления/исчезновения при нажатии Y
    public static long yKeyPressStartMs = 0L;
    public static long yKeyReleaseStartMs = 0L;
    public static final long Y_FADE_DURATION_MS = 600L; // 600мс для очень плавного fade

    public static void startTransition() {
        transitionStartMs = net.minecraft.Util.getMillis();
        transitionActive = true;
    }

    public static void startFilterDisplay() {
        filterDisplayStartMs = net.minecraft.Util.getMillis();
    }
}
