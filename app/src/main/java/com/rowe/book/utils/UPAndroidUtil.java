package com.rowe.book.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class UPAndroidUtil {

    public static Application getAppContext(Context context) {
        if (context != null) {
            if (context instanceof Application) {
                return (Application) context;
            } else {
                return (Application) context.getApplicationContext();
            }
        }

        return null;
    }

    public static String getAppName(Context context) {
        final ApplicationInfo ai = context.getApplicationInfo();

        return context.getResources().getString(ai.labelRes);
    }

    public static int getAppIcon(Context context) {
        final ApplicationInfo ai = context.getApplicationInfo();

        return ai.icon;
    }

    public static int getVersionCode(Context context) {
        try {
            final PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            final PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isDarkColor(int color) {
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        return (r * 0.299 + g * 0.587 + b * 0.114) < 192;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setStatusBarColor(Window window, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        final boolean isDarkIcon = !isDarkColor(color);

        // MIUI9之前，小米未兼容Android官方的沉浸式方案，需单独适配
        try {
            final Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            final Field darkModeField = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            final int darkModeFlag = darkModeField.getInt(layoutParams);

            final Class<?> clazz = window.getClass();
            final Method field = clazz.getMethod("setExtraFlags", int.class, int.class);
            field.invoke(window, isDarkIcon ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            // Eat
        }

        try {
            int flag = window.getDecorView().getSystemUiVisibility();
            if (isDarkIcon) { // 黑色图标
                flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else { // 白色图标
                flag &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(flag);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNavigationBarColor(Window window, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        try {
            window.setNavigationBarColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    /**
     * 这个方法不是稳定可靠的，所以不要用来做强逻辑的需求
     */
    public static String getCurrentProcessName(Context context) {
        String processName = "";

        // ActivityThread->currentProcessName()

        try {
            final Class clazz = Class.forName("android.app.ActivityThread");

            final Method methodCurrentProcessName = clazz.getDeclaredMethod("currentProcessName");
            methodCurrentProcessName.setAccessible(true);

            processName = (String) methodCurrentProcessName.invoke(null);
        } catch (Throwable t) {
            // Eat
        }

        // ActivityThread->currentActivityThread()->getProcessName()

        if (TextUtils.isEmpty(processName)) {
            try {
                final Class clazz = Class.forName("android.app.ActivityThread");

                final Method methodCurrentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
                methodCurrentActivityThread.setAccessible(true);

                final Method methodGetProcessName = clazz.getDeclaredMethod("getProcessName");
                methodGetProcessName.setAccessible(true);

                final Object activityThread = methodCurrentActivityThread.invoke(null);

                if (activityThread != null) {
                    processName = (String) methodGetProcessName.invoke(activityThread);
                }
            } catch (Throwable t) {
                // Eat
            }
        }

        // /proc/self/cmdline
        if (TextUtils.isEmpty(processName)) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader("/proc/self/cmdline"));

                processName = reader.readLine();

                if (!TextUtils.isEmpty(processName)) {
                    processName = processName.trim();
                }
            } catch (Throwable t) {
                // Eat
            } finally {
                IOUtil.closeQuietly(reader);
            }
        }

        return processName;
    }
}
