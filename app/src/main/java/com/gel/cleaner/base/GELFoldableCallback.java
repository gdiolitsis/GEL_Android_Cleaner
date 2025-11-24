// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableCallback — v1.2 (Unified + Backward Compatible)
// Foldable Ready • Orchestrator Safe • Legacy Bridge Supported
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner.base;

import androidx.annotation.NonNull;

/**
 * GELFoldableCallback
 * Step 1 — Unified callback for foldable posture + screen mode.
 *
 * v1.2 adds:
 *  ✅ Legacy overload for old detectors/bridges (boolean + hingeAngle)
 *  ✅ Static posture mapper helper
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

    // ============================================================
    // LEGACY BRIDGE (Backward Compatibility)
    // Allows older modules to still compile:
    //  - GELFoldableDetector old signature
    //  - GELFoldableActivityBridge v1.0
    // ============================================================

    /**
     * Legacy posture callback (boolean + angle).
     * Default bridges into the unified Posture callback.
     *
     * @param isUnfolded true if inner display / unfolded state detected
     * @param hingeAngle hinge angle in degrees (0..180)
     */
    default void onPostureChanged(boolean isUnfolded, float hingeAngle) {
        Posture p = postureFrom(isUnfolded, hingeAngle);
        onPostureChanged(p);
        onScreenChanged(p == Posture.FLAT || isUnfolded || hingeAngle >= 140f);
    }

    // ============================================================
    // STATIC HELPER: map raw hinge data -> Posture
    // Used by Orchestrator / Detector / Bridges.
    // ============================================================

    /**
     * Maps unfold + hinge angle to a unified posture.
     * Safe across OEM differences.
     */
    @NonNull
    static Posture postureFrom(boolean isUnfolded, float hingeAngle) {

        // Hard cases first
        if (!isUnfolded && hingeAngle <= 15f) return Posture.CLOSED;

        // Normalize angle
        float a = Math.max(0f, Math.min(180f, hingeAngle));

        // Fully open inner screen
        if (isUnfolded || a >= 155f) return Posture.FLAT;

        // Tent / tabletop / half-open heuristics
        if (a >= 105f && a < 155f) return Posture.HALF_OPEN;
        if (a >= 60f  && a < 105f) return Posture.TABLETOP;
        if (a >= 25f  && a < 60f)  return Posture.HALF_OPEN;

        return Posture.UNKNOWN;
    }
}

