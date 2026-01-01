// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPSPanicParser.java — iPhone Panic Signature Parser v1.0 FINAL
// Offline • Deterministic • Service-Grade

package com.gel.cleaner.iphone;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IPSPanicParser {

    // ============================================================
    // RESULT MODEL
    // ============================================================
    public static class Result {
        public String patternId;
        public String domain;
        public String cause;
        public String severity;
        public String confidence;
        public String recommendation;
    }

    // ============================================================
    // PUBLIC ENTRY POINT
    // ============================================================
    public static Result analyze(Context ctx, String panicLogText) {

        try {
            JSONObject json = loadPatterns(ctx);
            JSONArray patterns = json.getJSONArray("patterns");

            List<Result> matches = new ArrayList<>();

            for (int i = 0; i < patterns.length(); i++) {
                JSONObject p = patterns.getJSONObject(i);

                JSONArray tokens = p.getJSONArray("match");
                boolean allFound = true;

                for (int t = 0; t < tokens.length(); t++) {
                    String token = tokens.getString(t);
                    if (!panicLogText.contains(token)) {
                        allFound = false;
                        break;
                    }
                }

                if (allFound) {
                    Result r = new Result();
                    r.patternId      = p.optString("id");
                    r.domain         = p.optString("domain");
                    r.cause          = p.optString("cause");
                    r.severity       = p.optString("severity");
                    r.confidence     = p.optString("confidence");
                    r.recommendation = p.optString("recommendation");
                    matches.add(r);
                }
            }

            if (matches.isEmpty()) return null;

            return pickMostSevere(matches);

        } catch (Exception e) {
            return null;
        }
    }

    // ============================================================
    // LOAD JSON FROM ASSETS
    // ============================================================
    private static JSONObject loadPatterns(Context ctx) throws Exception {
        InputStream is = ctx.getAssets().open("ips_patterns_v1.json");
        Scanner sc = new Scanner(is, StandardCharsets.UTF_8.name());
        String json = sc.useDelimiter("\\A").next();
        sc.close();
        return new JSONObject(json);
    }

    // ============================================================
    // SEVERITY ORDER
    // ============================================================
    private static Result pickMostSevere(List<Result> list) {

        Result best = null;

        for (Result r : list) {
            if (best == null) {
                best = r;
                continue;
            }
            if (severityRank(r.severity) > severityRank(best.severity)) {
                best = r;
            }
        }
        return best;
    }

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
}
