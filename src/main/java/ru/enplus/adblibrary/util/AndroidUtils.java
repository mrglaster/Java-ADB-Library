package ru.enplus.adblibrary.util;

public class AndroidUtils {
    public static String truncateAndroidVersion(String androidVersion) {
        int firstDotIndex = androidVersion.indexOf('.');
        if (firstDotIndex != -1) {
            int secondDotIndex = androidVersion.indexOf('.', firstDotIndex + 1);
            if (secondDotIndex != -1) {
                return androidVersion.substring(0, secondDotIndex);
            }
        }
        return androidVersion;
    }
}
