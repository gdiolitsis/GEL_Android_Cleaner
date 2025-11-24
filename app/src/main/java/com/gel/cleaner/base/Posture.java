// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/base/Posture.java
// Posture Enum — Foldable States v1.2 (GEL Safe Sync)
// ------------------------------------------------------------
// ✔ Adds OUTER_SCREEN / TABLE_MODE / FULLY_OPEN (fixes enum errors)
// ✔ Keeps generic states for future devices
// ✔ No behavior change by itself
// ------------------------------------------------------------

package com.gel.cleaner.base;

public enum Posture {
    UNKNOWN,

    // Outer / cover display
    OUTER_SCREEN,

    // Inner / main display
    INNER_SCREEN,

    // Half-open / laptop-style hinge
    HALF_OPENED,

    // Table-top / tent-like mode (OEM naming varies)
    TABLE_MODE,

    // Fully open tablet-like
    FULLY_OPEN
}
