package com.av.avmessenger;

import android.app.Activity;

public class ThemeManager {
    private static boolean isDarkMode = false;

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
        // Apply the theme change here if needed
    }

    public static void applyTheme(Activity activity){
        if (isDarkMode) {
         activity.setTheme(R.style.SCREEN_DARK);
        } else {
         activity.setTheme(R.style.SCREEN);
        }
    }
}
