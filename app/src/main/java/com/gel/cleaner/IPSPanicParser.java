// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPSPanicParser.java — iOS Panic Log Signature Parser (Multi-Match)
// FINAL • NO AI • RULE-BASED • SERVICE-GRADE

package com.gel.cleaner;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class IPSPanicParser {

    // ============================================================
    // RESULT MODEL (Primary + Secondary)
    // ============================================================
    public static class Result {
        // container
        public Result primary;
        public List<Result> secondary = new ArrayList<>();

        // single signature fields
        public String patternId;
        public String domain;
        public String cause;
        public String severity;
        public String confidence;
        public String recommendation;
    }

    // ============================================================
    // ENTRY POINT
    // ============================================================
    public static Result analyze(Context ctx, String panicText) {

        if (panicText == null || panicText.length() < 32)
            return null;

        try {
            JSONObject root = loadPatterns(ctx);
            JSONArray patterns = root.getJSONArray("patterns");

            List<Result> matches = new ArrayList<>();

            for (int i = 0; i < patterns.length(); i++) {
                JSONObject p = patterns.getJSONObject(i);

                JSONArray tokens = p.getJSONArray("match");
                boolean allFound = true;

                for (int t = 0; t < tokens.length(); t++) {
                    String token = tokens.getString(t);
                    if (!panicText.contains(token)) {
                        allFound = false;
                        break;
                    }
                }

                if (allFound) {
                    Result r = new Result();
                    r.patternId      = p.optString("id", "unknown");
                    r.domain         = p.optString("domain", "Unknown");
                    r.cause          = p.optString("cause", "Unknown");
                    r.severity       = p.optString("severity", "LOW");
                    r.confidence     = p.optString("confidence", "Low");
                    r.recommendation = p.optString("recommendation", "Further inspection recommended.");
                    matches.add(r);
                }
            }

            if (matches.isEmpty()) return null;

            // Sort by severity (DESC)
            matches.sort((a, b) ->
                    severityRank(b.severity) - severityRank(a.severity)
            );

            Result out = new Result();
            out.primary = matches.get(0);

            for (int i = 1; i < matches.size(); i++) {
                out.secondary.add(matches.get(i));
            }

            return out;

        } catch (Throwable e) {
            return null;
        }
    }

    // ============================================================
    // SEVERITY RANKING
    // ============================================================
    private static int severityRank(String s) {
        if (s == null) return 0;
        switch (s.toUpperCase()) {
            case "CRITICAL": return 4;
            case "HIGH":     return 3;
            case "MEDIUM":   return 2;
            case "LOW":      return 1;
            default:         return 0;
        }
    }

    // ============================================================
    // LOAD JSON PATTERNS (assets)
    // ============================================================
    private static JSONObject loadPatterns(Context ctx) throws Exception {
        InputStream is = ctx.getAssets().open("ips_panic_patterns.json");
        byte[] buf = new byte[is.available()];
        is.read(buf);
        is.close();
        String json = new String(buf, "UTF-8");
        return new JSONObject(json);
    }
}
