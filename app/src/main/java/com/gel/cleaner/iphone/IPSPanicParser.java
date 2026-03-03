// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPSPanicParser.java — iPhone Panic Signature Parser (Proxy)
// FINAL • Delegates to Core Parser • NO DUPLICATION

package com.gel.cleaner.iphone;

import android.content.Context;

public final class IPSPanicParser {

    private IPSPanicParser() {}

    public static class Result
            extends com.gel.cleaner.IPSPanicParser.Result {}

    public static Result analyze(Context ctx, String panicLogText) {

        com.gel.cleaner.IPSPanicParser.Result core =
                com.gel.cleaner.IPSPanicParser.analyze(ctx, panicLogText);

        if (core == null) return null;

        Result out = new Result();
        out.patternId      = core.primary.patternId;
        out.domain         = core.primary.domain;
        out.cause          = core.primary.cause;
        out.severity       = core.primary.severity;
        out.confidence     = core.primary.confidence;
        out.recommendation = core.primary.recommendation;

        return out;
    }
}
