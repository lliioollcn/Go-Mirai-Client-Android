package cn.lliiooll.gmca.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

public class BrandUtils {

    private static final Map<String, Map<String, String>> brandSfaCenters = new HashMap<String, Map<String, String>>() {{
        put("huawei", new HashMap<String, String>() {{
            put("packageName0", "com.huawei.systemmanager");
            put("packageName1", "com.huawei.systemmanager");
            put("activity0", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            put("activity1", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }});
        put("honor", new HashMap<String, String>() {{
            put("packageName0", "com.huawei.systemmanager");
            put("packageName1", "com.huawei.systemmanager");
            put("activity0", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            put("activity1", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }});
        put("xiaomi", new HashMap<String, String>() {{
            put("packageName0", "com.miui.securitycenter");
            put("activity0", "com.miui.permcenter.autostart.AutoStartManagementActivity");
        }});
        put("letv", new HashMap<String, String>() {{
            put("packageName0", "com.letv.android.letvsafe");
            put("activity0", "com.letv.android.letvsafe.AutobootManageActivity");
        }});
        put("oppo", new HashMap<String, String>() {{
            put("packageName0", "com.coloros.phonemanager");
            put("packageName1", "com.oppo.safe");
            put("packageName2", "com.coloros.oppoguardelf");
            put("packageName3", "com.coloros.safecenter");
        }});
        put("vivo", new HashMap<String, String>() {{
            put("packageName0", "com.iqoo.secure");
        }});
        put("meizu", new HashMap<String, String>() {{
            put("packageName0", "com.meizu.safe");
        }});
        put("smartisan", new HashMap<String, String>() {{
            put("packageName0", "com.smartisanos.security");
        }});
        put("samsung", new HashMap<String, String>() {{
            put("packageName0", "com.samsung.android.sm_cn");
            put("packageName1", "com.samsung.android.sm");
        }});
    }};


    public static void openSafeCenterBrand(Context ctx) {
        brandSfaCenters.forEach((brand, value) -> {
            if (isBrand(brand)) {
                for (int i = 0; i < value.keySet().size(); i++) {
                    try {
                        String packageName = value.getOrDefault("packageName" + i, null);
                        String activity = value.getOrDefault("activity" + i, null);
                        if (packageName != null) {
                            if (activity != null) {
                                openSafeCenterBrand(ctx, packageName, activity);
                            } else {
                                openSafeCenterBrand(ctx, packageName);
                            }
                            break;
                        }
                    } catch (Throwable ignored) {

                    }
                }
            }
        });
    }

    private static void openSafeCenterBrand(Context ctx, String packageName) throws Throwable {
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(packageName);
        ctx.startActivity(intent);
    }

    private static void openSafeCenterBrand(Context ctx, String packageName, String activity) throws Throwable {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activity));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static boolean isBrand(String brand) {
        return Build.BRAND.equalsIgnoreCase(brand);
    }

}
