// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableCallback — v1.3 (Legacy Enum Aliases + Unified)
// Foldable Ready • Orchestrator Safe • Legacy Bridge Supported
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner.base;

import androidx.annotation.NonNull;

public interface GELFoldableCallback {

    /**
     * Unified posture enum for all foldable devices.
     *
     * v1.3:
     *  🔹 Προστέθηκαν LEGACY aliases:
     *      FULLY_OPEN
     *      TABLE_MODE
     *      INNER_SCREEN
     *      OUTER_SCREEN
     *      HALF_OPENED
     *    -> Χωρίς αυτά το app δεν κάνει compile.
     */
    enum Posture {

        // ===== MAIN MODERN ENUM =====
        HALF_OPEN,     // hinge at mid-angle (book/laptop)
        TABLETOP,      // device L-shaped (Flex mode)
        TENT,          // inverted V
        FLAT,          // fully open — inner display
        CLOSED,        // folded — cover screen
        UNKNOWN,       // fallback

        // ===== LEGACY ENUM VALUES (for compatibility ONLY) =====
        FULLY_OPEN,    // legacy name of FLAT
        TABLE_MODE,    // legacy name of TABLETOP
        INNER_SCREEN,  // legacy "inner display"
        OUTER_SCREEN,  // legacy "outer display"
        HALF_OPENED    // legacy duplicate of HALF_OPEN
    }

    void onPostureChanged(@NonNull Posture posture);

    void onScreenChanged(boolean isInner);

    // ============================================================
    // LEGACY BRIDGE (Backward Compatibility)
    // ============================================================
    default void onPostureChanged(boolean isUnfolded, float hingeAngle) {
        Posture p = postureFrom(isUnfolded, hingeAngle);

        onPostureChanged(p);

        boolean inner =
                p == Posture.FLAT ||
                p == Posture.FULLY_OPEN ||
                p == Posture.INNER_SCREEN ||
                isUnfolded ||
                hingeAngle >= 140f;

        onScreenChanged(inner);
    }

    // ============================================================
    // POSTURE MAPPER
    // ============================================================
    @NonNull
    static Posture postureFrom(boolean isUnfolded, float hingeAngle) {

        if (!isUnfolded && hingeAngle <= 15f)
            return Posture.CLOSED;

        float a = Math.max(0f, Math.min(180f, hingeAngle));

        if (isUnfolded || a >= 155f)
            return Posture.FLAT;

        if (a >= 105f)
            return Posture.HALF_OPEN;   // laptops / high angle

        if (a >= 60f)
            return Posture.TABLETOP;    // flex mode

        if (a >= 25f)
            return Posture.HALF_OPEN;

        return Posture.UNKNOWN;
    }
}
