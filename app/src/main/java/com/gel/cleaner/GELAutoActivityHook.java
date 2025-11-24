// GDiolitsis Engine Lab (GEL) — Author & Developer
// UNIVERSAL MASTER HOOK (GEL v4.5)
// Combines: GELAutoDP + GELFoldableDetector + UIManager + Locale Hook
// + GELFoldableOrchestrator + GELFoldableAnimationPack + DualPaneManager
// NOTE: Full-file patch — Πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class GELAutoActivityHook extends AppCompatActivity
        implements GELFoldableCallback {

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    // Optional engines
    private Object foldOrchestrator;
    private Object foldAnimPack;
    private Object dualPaneManager;

    private boolean lastInner = false;

    // ============================================================
    // CONTEXT HOOK (Locale + scaling)
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GELAutoDP.init(this);

        uiManager    = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        initExtraFoldableEngines();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (foldDetector != null) foldDetector.start();

        safeCall(foldOrchestrator, "onResume");
        safeCall(foldAnimPack, "onResume");
        safeCall(dualPaneManager, "onResume");
    }

    @Override
    protected void onPause() {
        safeCall(foldOrchestrator, "onPause");
        safeCall(foldAnimPack, "onPause");
        safeCall(dualPaneManager, "onPause");

        if (foldDetector != null) foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // CONFIG CHANGES (locale / rotation / fold)
    // ============================================================
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        GELAutoDP.init(this);

        if (uiManager != null) uiManager.applyUI(lastInner);

        safeCall(foldOrchestrator, "onConfigurationChanged",
                new Class[]{Configuration.class}, new Object[]{newConfig});
        safeCall(foldAnimPack, "onConfigurationChanged",
                new Class[]{Configuration.class}, new Object[]{newConfig});
        safeCall(dualPaneManager, "onConfigurationChanged",
                new Class[]{Configuration.class}, new Object[]{newConfig});
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        GELAutoDP.init(this);

        if (uiManager != null) uiManager.applyUI(lastInner);

        safeCall(foldOrchestrator, "onMultiWindowModeChanged",
                new Class[]{boolean.class}, new Object[]{isInMultiWindowMode});
        safeCall(foldAnimPack, "onMultiWindowModeChanged",
                new Class[]{boolean.class}, new Object[]{isInMultiWindowMode});
        safeCall(dualPaneManager, "onMultiWindowModeChanged",
                new Class[]{boolean.class}, new Object[]{isInMultiWindowMode});
    }

    // ============================================================
    // FOLDABLE CALLBACKS (Unified GELFoldablePosture)
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull GELFoldablePosture posture) {
        safeCall(foldOrchestrator, "onPostureChanged",
                new Class[]{GELFoldablePosture.class}, new Object[]{posture});
        safeCall(foldAnimPack, "onPostureChanged",
                new Class[]{GELFoldablePosture.class}, new Object[]{posture});
        safeCall(dualPaneManager, "onPostureChanged",
                new Class[]{GELFoldablePosture.class}, new Object[]{posture});
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner == lastInner) return;

        lastInner = isInner;

        if (uiManager != null) uiManager.applyUI(isInner);

        safeCall(foldOrchestrator, "onScreenChanged",
                new Class[]{boolean.class}, new Object[]{isInner});
        safeCall(foldAnimPack, "onScreenChanged",
                new Class[]{boolean.class}, new Object[]{isInner});
        safeCall(dualPaneManager, "onScreenChanged",
                new Class[]{boolean.class}, new Object[]{isInner});
    }

    // ============================================================
    // EXTRA FOLDABLE ENGINES (Reflection Safe)
    // ============================================================
    private void initExtraFoldableEngines() {

        foldOrchestrator = tryNew(
                "com.gel.cleaner.GELFoldableOrchestrator",
                new Class[]{Context.class, GELFoldableCallback.class},
                new Object[]{this, this}
        );

        foldAnimPack = tryNew(
                "com.gel.cleaner.GELFoldableAnimationPack",
                new Class[]{Context.class},
                new Object[]{this}
        );

        dualPaneManager = tryNew(
                "com.gel.cleaner.DualPaneManager",
                new Class[]{Context.class},
                new Object[]{this}
        );

        safeCall(foldOrchestrator, "bind",
                new Class[]{
                        GELFoldableDetector.class,
                        GELFoldableUIManager.class,
                        Object.class,
                        Object.class
                },
                new Object[]{foldDetector, uiManager, dualPaneManager, foldAnimPack}
        );

        safeCall(foldOrchestrator, "onCreate");
        safeCall(foldAnimPack, "onCreate");
        safeCall(dualPaneManager, "onCreate");
    }

    // ============================================================
    // REFLECTION UTILITIES
    // ============================================================
    private Object tryNew(String name, Class<?>[] sig, Object[] args) {
        try {
            Class<?> c = Class.forName(name);
            Constructor<?> ctor = c.getConstructor(sig);
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (Throwable ignore) {
            try {
                Class<?> c = Class.forName(name);
                Constructor<?> ctor = c.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    private void safeCall(Object target, String method) {
        safeCall(target, method, null, null);
    }

    private void safeCall(Object target, String method,
                          Class<?>[] sig, Object[] args) {
        if (target == null || method == null) return;
        try {
            Method m;
            if (sig == null) {
                m = target.getClass().getMethod(method);
                m.setAccessible(true);
                m.invoke(target);
            } else {
                m = target.getClass().getMethod(method, sig);
                m.setAccessible(true);
                m.invoke(target, args);
            }
        } catch (Throwable ignored) {}
    }

    // ============================================================
    // GLOBAL DP/SP HELPERS
    // ============================================================
    public int dp(int x)     { return GELAutoDP.dp(x); }
    public float sp(float x) { return GELAutoDP.sp(x); }
    public int px(int x)     { return GELAutoDP.px(x); }
}
