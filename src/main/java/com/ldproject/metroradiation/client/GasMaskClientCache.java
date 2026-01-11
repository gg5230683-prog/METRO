package com.ldproject.metroradiation.client;

/**
 * Кэш данных противогаза на клиенте (синхронизируется с сервера)
 */
public class GasMaskClientCache {
    public static boolean hasGasMask = false;
    public static int filterTime = 0;
    public static int durability = 1000;
}
