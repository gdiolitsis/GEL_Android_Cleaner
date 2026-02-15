// GDiolitsis Engine Lab (GEL) — Author & Developer
// FINAL — AppListAdapter (All Installed Apps + MultiSelect + Sizes)
// SAFE • Foldable ready • AutoScaling preserved

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private final Context ctx;
    private final List<AppListActivity.AppEntry> data;
    private final LayoutInflater inflater;

    private final boolean hasFoldable;
    private GELFoldableAnimationPack animPack;

    public AppListAdapterSafe(Context ctx,
                              List<AppListActivity.AppEntry> data) {

        this.ctx = ctx;
        this.data = data;
        this.inflater = LayoutInflater.from(ctx);

        if (ctx instanceof GELAutoActivityHook) {
            hasFoldable = true;
            animPack = new GELFoldableAnimationPack(ctx);
        } else {
            hasFoldable = false;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // ============================================================
    // HOLDER
    // ============================================================
    static class Holder {
        TextView name;
        TextView pkg;
        TextView size;
        TextView cache;
        ImageView icon;
        CheckBox select;
    }

    // ============================================================
    // VIEW
    // ============================================================
    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        Holder h;

        if (convertView == null) {

            convertView = inflater.inflate(
                    R.layout.list_item_app,
                    parent,
                    false
            );

            h = new Holder();

            h.name   = convertView.findViewById(R.id.appLabel);
            h.pkg    = convertView.findViewById(R.id.appPackage);
            h.icon   = convertView.findViewById(R.id.appIcon);
            h.size   = convertView.findViewById(R.id.appSize);
            h.cache  = convertView.findViewById(R.id.appCache);
            h.select = convertView.findViewById(R.id.appCheck);

            convertView.setTag(h);

            if (ctx instanceof GELAutoActivityHook) {
                GELAutoActivityHook a =
                        (GELAutoActivityHook) ctx;

                h.name.setTextSize(a.sp(15f));
                h.pkg.setTextSize(a.sp(12f));
                h.size.setTextSize(a.sp(12f));
                h.cache.setTextSize(a.sp(12f));

                ViewGroup.LayoutParams lp =
                        h.icon.getLayoutParams();
                lp.width  = a.dp(38);
                lp.height = a.dp(38);
                h.icon.setLayoutParams(lp);

                int pad = a.dp(12);
                convertView.setPadding(pad,pad,pad,pad);
            }

            if (hasFoldable && animPack != null) {
                animPack.applyListItemFade(convertView);
            }

        } else {
            h = (Holder) convertView.getTag();
        }

        // ============================================================
        // BIND DATA
        // ============================================================

        AppListActivity.AppEntry e = data.get(position);
        if (e == null) return convertView;

        PackageManager pm = ctx.getPackageManager();

        // NAME
        h.name.setText(
                TextUtils.isEmpty(e.label)
                        ? "Unknown"
                        : e.label
        );

        // ICON
        try {
            h.icon.setImageDrawable(
                    pm.getApplicationIcon(e.pkg)
            );
        } catch (Exception ignored) {
            h.icon.setImageResource(
                    android.R.drawable.sym_def_app_icon
            );
        }

        // PACKAGE
        h.pkg.setText(e.pkg);

        // TYPE COLOR
        if (e.isSystem) {
            h.name.setTextColor(0xFFFFD700); // gold system
        } else {
            h.name.setTextColor(Color.WHITE);
        }

        // APP SIZE
        h.size.setText(
                "App: " + formatBytes(e.appBytes)
        );

        // CACHE SIZE
        h.cache.setText(
                "Cache: " + formatBytes(e.cacheBytes)
        );

        // CHECKBOX
        h.select.setOnCheckedChangeListener(null);
        h.select.setChecked(e.selected);
        h.select.setOnCheckedChangeListener((b, checked) -> {
            e.selected = checked;
        });

        return convertView;
    }

    // ============================================================
    // FORMAT SIZE
    // ============================================================
    private String formatBytes(long bytes) {

        if (bytes < 0) return "—";

        float kb = bytes / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        DecimalFormat df = new DecimalFormat("0.0");

        if (gb >= 1) return df.format(gb) + " GB";
        if (mb >= 1) return df.format(mb) + " MB";
        if (kb >= 1) return df.format(kb) + " KB";

        return bytes + " B";
    }
}
