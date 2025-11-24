// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/base/GELFoldableCallback.java
// GELFoldableCallback — v1.3 (Unified with base Posture + Backward Compatible)
// ------------------------------------------------------------
// ✔ Uses BASE enum Posture (no duplicate enums, fixes override errors)
// ✔ Keeps legacy boolean+angle overload
// ✔ Static mapper returns BASE Posture
// ✔ Zero-crash fallbacks
// ------------------------------------------------------------

package com.gel.cleaner.base;

import androidx.annotation.NonNull;

/**
 * Unified callback for foldable posture + screen mode.
 *
 * IMPORTANT:
 * - Uses com.gel.cleaner.base.Posture (top-level enum).
 * - Older detectors can still call legacy onPostureChanged(boolean,float).
 */
public interface GELFoldableCallback {

    /**
     * Called whenever foldable hinge posture changes.
     * @param posture Detected posture state (BASE enum)
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

        boolean inner =
                (p == Posture.INNER_SCREEN ||
                 p == Posture.FULLY_OPEN ||
                 p == Posture.TABLE_MODE ||
                 p == Posture.HALF_OPENED);

        onScreenChanged(inner);
    }

    // ============================================================
    // STATIC HELPER: map raw hinge data -> BASE Posture
    // ============================================================

    /**
     * Maps unfold + hinge angle to BASE Posture.
     * Safe across OEM differences.
     */
    @NonNull
    static Posture postureFrom(boolean isUnfolded, float hingeAngle) {

        // Normalize angle
        float a = Math.max(0f, Math.min(180f, hingeAngle));

        // Outer / cover display hard case
        if (!isUnfolded && a <= 15f) return Posture.OUTER_SCREEN;

        // Fully open inner screen
        if (isUnfolded || a >= 155f) return Posture.FULLY_OPEN;

        // Table / half open heuristics
        if (a >= 105f && a < 155f) return Posture.HALF_OPENED;
        if (a >= 60f  && a < 105f) return Posture.TABLE_MODE;
        if (a >= 25f  && a < 60f)  return Posture.HALF_OPENED;

        return Posture.UNKNOWN;
    }
}
