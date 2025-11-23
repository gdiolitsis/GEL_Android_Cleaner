// GDiolitsis Engine Lab (GEL) — Author & Developer
// Unified Foldable Posture Enum (Samsung / Honor / OnePlus / Pixel Fold)

package com.gel.cleaner;

public enum GELFoldablePosture {
    CLOSED,       // fully folded — outer display
    HALF_OPEN,    // book / laptop position
    FLAT,         // fully open — inner display
    TABLETOP,     // L-shape on table
    TENT,         // inverted V shape
    UNKNOWN       // fallback
}
