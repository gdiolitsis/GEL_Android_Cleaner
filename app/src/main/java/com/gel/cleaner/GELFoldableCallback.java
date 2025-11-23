// GDiolitsis Engine Lab (GEL) — Author & Developer
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import androidx.annotation.NonNull;

/**
 * GELFoldableCallback
 * Step 1 — Unified callback for foldable posture + screen mode.
 */
public interface GELFoldableCallback {

    /**
     * Unified posture enum for all foldable devices.
     * (Samsung / Honor / Pixel Fold / OnePlus Open)
     */
    enum Posture {
        HALF_OPEN,     // hinge at mid-angle (book/laptop)
        TABLETOP,      // device L-shaped on table (Flex mode)
        TENT,          // inverted V / tent mode
        FLAT,          // fully open — inner large display active
        CLOSED,        // fully folded — outer/cover screen
        UNKNOWN        // fallback
    }

    /**
     * Called whenever foldable hinge posture changes.
     * @param posture Detected posture state
     */
    void onPostureChanged(@NonNull Posture posture);

    /**
     * Called whenever screen surface switches:
     * - true  = using large inner (unfolded) display
     * - false = using outer/cover display
     */
    void onScreenChanged(boolean isInner);
}
