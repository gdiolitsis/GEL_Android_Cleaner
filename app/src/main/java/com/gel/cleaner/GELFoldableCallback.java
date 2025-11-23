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
     */
    enum GELPosture {
        HALF_OPEN,   // hinge at mid-angle (book/laptop style)
        TABLETOP,    // L-shape on table (Samsung Flex Mode)
        TENT,        // inverted V (tent mode)
        FLAT,        // fully open, inner large screen active
        CLOSED,      // folded shut, outer/corner display
        UNKNOWN      // fallback for strange/undetected states
    }

    /**
     * Fired whenever the hinge posture changes.
     * @param posture new detected posture
     */
    void onPostureChanged(@NonNull GELPosture posture);

    /**
     * Fired whenever the UI should switch between:
     * inner (big) screen ↔ outer (cover) screen
     * @param isInner true = unfolded large display, false = cover display
     */
    void onScreenChanged(boolean isInner);
}
