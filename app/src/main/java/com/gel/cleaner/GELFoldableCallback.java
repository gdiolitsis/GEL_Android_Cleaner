// GDiolitsis Engine Lab (GEL) — Author & Developer
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import androidx.annotation.NonNull;

/**
 * GELFoldableCallback
 * Step 1 — Callback + Posture enum for foldables.
 */
public interface GELFoldableCallback {

    enum Posture {
        HALF_OPEN,   // hinge in mid-angle (book / laptop)
        TABLE_MODE,  // half-open, stable on table
        TENT_MODE,   // inverted V (tent mode)
        FLAT,        // fully open, inner display active
        CLOSED       // folded shut, outer (cover) screen
    }

    /**
     * Called whenever foldable posture changes.
     * @param posture new detected posture
     */
    void onPostureChanged(@NonNull Posture posture);

    /**
     * Called whenever display surface changes (outer ↔ inner)
     * @param isInner true = unfolded large screen, false = cover screen
     */
    void onScreenChanged(boolean isInner);
}
