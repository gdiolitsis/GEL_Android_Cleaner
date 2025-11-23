// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 5 — Adaptive UI Reflow Manager for Foldables

package com.gel.cleaner;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GELFoldableUIManager {

    private final Activity act;

    public GELFoldableUIManager(Activity activity) {
        this.act = activity;
    }

    // =====================================================
    // PUBLIC MAIN ENTRY
    // =====================================================
    public void applyUI(boolean isInnerScreen) {
        if (isInnerScreen) {
            applyTabletMode();
        } else {
            applyPhoneMode();
        }
    }

    // =====================================================
    // MODE 1 — PHONE MODE (Compact)
    // =====================================================
    private void applyPhoneMode() {
        applyGlobalPadding(dp(12));
        applyGlobalTextSizeSp(14);

        // Single column
        applyColumnMode(false);

        // Extra: shrink headers
        scaleHeaders(16);
    }

    // =====================================================
    // MODE 2 — TABLET MODE (Unfolded)
    // =====================================================
    private void applyTabletMode() {
        applyGlobalPadding(dp(18));
        applyGlobalTextSizeSp(17);

        // Switch to 2-column wherever possible
        applyColumnMode(true);

        // Larger headers
        scaleHeaders(19);
    }

    // =====================================================
    // GLOBAL PADDING
    // =====================================================
    private void applyGlobalPadding(int px) {
        View root = act.findViewById(android.R.id.content);
        if (root != null) applyPaddingRecursive(root, px);
    }

    private void applyPaddingRecursive(View v, int px) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            vg.setPadding(px, px, px, px);

            for (int i = 0; i < vg.getChildCount(); i++) {
                applyPaddingRecursive(vg.getChildAt(i), px);
            }
        }
    }

    // =====================================================
    // GLOBAL TEXT SIZE
    // =====================================================
    private void applyGlobalTextSizeSp(int sp) {
        View root = act.findViewById(android.R.id.content);
        if (root != null) applyTextRecursive(root, sp);
    }

    private void applyTextRecursive(View v, int sp) {
        if (v instanceof TextView) {
            ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        }

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyTextRecursive(vg.getChildAt(i), sp);
            }
        }
    }

    // =====================================================
    // HEADERS SCALE (System / CPU / GPU / etc)
    // =====================================================
    private void scaleHeaders(int sp) {
        int[] headerIds = new int[]{
                R.id.headerSystem,
                R.id.headerAndroid,
                R.id.headerCpu,
                R.id.headerGpu,
                R.id.headerThermal,
                R.id.headerThermalZones,
                R.id.headerVulkan,
                R.id.headerThermalProfiles,
                R.id.headerFpsGovernor,
                R.id.headerRam,
                R.id.headerStorage,
                R.id.headerScreen,
                R.id.headerConnectivity,
                R.id.headerRoot
        };

        for (int id : headerIds) {
            View h = act.findViewById(id);
            if (h != null && h instanceof ViewGroup) {
                ViewGroup header = (ViewGroup) h;
                for (int i = 0; i < header.getChildCount(); i++) {
                    View c = header.getChildAt(i);
                    if (c instanceof TextView) {
                        ((TextView) c).setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                    }
                }
            }
        }
    }

    // =====================================================
    // COLUMN MODE — SINGLE or DUAL LAYOUT
    // =====================================================
    private void applyColumnMode(boolean dual) {
        // ANY LinearLayout vertical container becomes 2 columns
        View root = act.findViewById(android.R.id.content);
        if (root != null) convertColumnsRecursive(root, dual);
    }

    private void convertColumnsRecursive(View v, boolean dual) {
        if (v instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) v;

            if (ll.getOrientation() == LinearLayout.VERTICAL && ll.getChildCount() > 2) {
                if (dual) {
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    ll.setOrientation(LinearLayout.VERTICAL);
                }
            }
        }

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                convertColumnsRecursive(vg.getChildAt(i), dual);
            }
        }
    }

    // =====================================================
    // UTILS
    // =====================================================
    private int dp(int dp) {
        float scale = act.getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }
}
