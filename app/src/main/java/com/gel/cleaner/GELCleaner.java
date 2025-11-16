// ====================================================================
// TEMP FILES → MIUI CLEANUP PAGE (your screenshot)
// ====================================================================
public static void cleanTempFiles(Context ctx, LogCallback cb) {
    try {
        String brand = Build.BRAND == null ? "" : Build.BRAND.toLowerCase();
        String manu  = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase();

        boolean isXiaomi = brand.contains("xiaomi") || brand.contains("redmi") ||
                           manu.contains("xiaomi")  || manu.contains("redmi");

        // ------------------------------------------------------------
        // ⭐ MIUI CLEANUP PAGE (THE PAGE IN YOUR PHOTO)
        // ------------------------------------------------------------
        if (isXiaomi) {

            // MIUI Cleaner main screen (your screenshot)
            Intent miui = new Intent();
            miui.setClassName(
                    "com.miui.cleaner",
                    "com.miui.cleaner.MainActivity"
            );
            miui.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                ctx.startActivity(miui);
                ok(cb, "Άνοιξα το MIUI Cleanup (Temp Files).");
                return;
            } catch (Exception ignored) {}

            // Backup MIUI cleanup screen
            Intent sec = new Intent();
            sec.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.securityscan.MainActivity"
            );
            sec.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                ctx.startActivity(sec);
                ok(cb, "Άνοιξα το MIUI Security Cleaner.");
                return;
            } catch (Exception ignored) {}
        }

        // ------------------------------------------------------------
        // Other devices → Safe internal cleanup only
        // ------------------------------------------------------------
        File temp = new File(ctx.getFilesDir(), "temp");
        long before = folderSize(temp);
        deleteFolder(temp);

        ok(cb, "Temp files removed: " + readable(before));
        info(cb, "Temp cleanup completed safely.");

    } catch (Exception e) {
        err(cb, "tempFiles failed: " + e.getMessage());
    }
}
