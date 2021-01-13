package com.rowe.book.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by tamrylei on 2016/12/16.
 *
 * 网络相关接口
 */
public final class UPNetworkUtil {

    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_WIFI = 1;
    public static final int NETWORK_TYPE_2G = 2;
    public static final int NETWORK_TYPE_3G = 3;
    public static final int NETWORK_TYPE_4G = 4;

    public static boolean isNetworkAvailable(Context context) {
        try {
            final ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo info = cm.getActiveNetworkInfo();

            return info != null && info.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        NetworkInfo info = null;

        try {
            final ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            info = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    public static int getNetworkType(Context context) {
        final NetworkInfo info = getNetworkInfo(context);

        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NETWORK_TYPE_WIFI;
                case ConnectivityManager.TYPE_MOBILE: {
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            return NETWORK_TYPE_2G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_TYPE_4G;
                        default:
                            return NETWORK_TYPE_3G;
                    }
                }
            }
        }

        return NETWORK_TYPE_NONE;
    }

    @SuppressLint("DefaultLocale")
    public static String getLocalNetworkIP(Context context) {
        final NetworkInfo info = getNetworkInfo(context);
        if (info == null) {
            return null;
        }

        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            try {
                final WifiManager wifi = (WifiManager)
                        context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                final WifiInfo wifiInfo = wifi.getConnectionInfo();
                final int ipAddress = wifiInfo.getIpAddress();

                return InetAddress.getByName(
                        String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                                (ipAddress >> 24 & 0xff))).getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

                while (en.hasMoreElements()) {
                    final NetworkInterface ni = en.nextElement();
                    final Enumeration<InetAddress> enIp = ni.getInetAddresses();

                    while (enIp.hasMoreElements()) {
                        final InetAddress inet = enIp.nextElement();

                        if (!inet.isLoopbackAddress() && (inet instanceof Inet4Address)) {
                            return inet.getHostAddress();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
