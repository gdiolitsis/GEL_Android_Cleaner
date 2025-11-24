// GDiolitsis Engine Lab (GEL) — Author & Developer
// FINAL — AppListAdapter (GEL Auto-Scaling + Foldable Safe)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private final Context ctx;
    private final List<ResolveInfo> data;
    private final LayoutInflater inflater;

    // Foldable managers (safe: optional if activity supports them)
    private final boolean hasFoldable;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animPack;

    public AppListAdapter(Context ctx, List<ResolveInfo> data) {
        this.ctx = ctx;
        this.data = data;
        this.inflater = LayoutInflater.from(ctx);

        // ============================================================
        // AUTO DETECT IF ACTIVITY USES THE FOLDABLE ENGINE
        // ============================================================
        if (ctx instanceof GELAutoActivityHook) {
            this.hasFoldable = true;
            this.uiManager = new GELFoldableUIManager(ctx);
            this.animPack = new GELFoldableAnimationPack(ctx);
        } else {
            this.hasFoldable = false;
        }
    }

    @Override
    public int getCount() { return (data == null ? 0 : data.size()); }

    @Override
    public Object getItem(int position) { return (data == null ? null : data.get(position)); }

    @Override
    public long getItemId(int position) { return position; }

    // ============================================================
    // HOLDER
    // ============================================================
    static class Holder {
        TextView name;
        TextView pkg;
        ImageView icon;
    }

    // ============================================================
    // RENDER EACH ROW
    // ============================================================
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder h;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_app, parent, false);
            h = new Holder();

            h.name = convertView.findViewById(R.id.appLabel);
            h.pkg  = convertView.findViewById(R.id.appPackage);
            h.icon = convertView.findViewById(R.id.appIcon);

            convertView.setTag(h);

            // ============================================================
            // GEL UNIVERSAL AUTO-SCALING (once per new row)
            // ============================================================
            if (ctx instanceof GELAutoActivityHook) {
                GELAutoActivityHook a = (GELAutoActivityHook) ctx;

                // Text scaling
                h.name.setTextSize(a.sp(15f));
                h.pkg.setTextSize(a.sp(12f));

                // Icon scaling
                ViewGroup.LayoutParams lp = h.icon.getLayoutParams();
                lp.width  = a.dp(38);
                lp.height = a.dp(38);
                h.icon.setLayoutParams(lp);

                // Row padding
                int pad = a.dp(12);
                convertView.setPadding(pad, pad, pad, pad);
            }

            // ============================================================
            // FOLDABLE ANIMATION BOOSTER (Fade-in per row)
            // ============================================================
            if (hasFoldable && animPack != null) {
                animPack.applyListItemFade(convertView);
            }

        } else {
            h = (Holder) convertView.getTag();
        }

        // ============================================================
        // BIND DATA
        // ============================================================
        ResolveInfo r = data.get(position);
        if (r != null) {

            // Name
            CharSequence label = null;
            try {
                label = r.loadLabel(ctx.getPackageManager());
            } catch (Exception ignored) {}

            h.name.setText(label != null ? label : "Unknown");

            // Icon
            try {
                h.icon.setImageDrawable(r.loadIcon(ctx.getPackageManager()));
            } catch (Exception ignored) {
                h.icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            // Package name
            String pkg = "";
            if (r.activityInfo != null && r.activityInfo.packageName != null) {
                pkg = r.activityInfo.packageName;
            }
            h.pkg.setText(pkg);
        }

        return convertView;
    }
}
