// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListAdapter — STABLE SAFE BUILD (Root-Aware)

package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_APP = 1;

    private final Context ctx;
    private final LayoutInflater inflater;
    private final PackageManager pm;

    private final List<AppListActivity.AppEntry> data = new ArrayList<>();

    public AppListAdapter(Context ctx) {
        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.pm = ctx.getPackageManager();
        setHasStableIds(true);
    }

    // ============================================================
    // SUBMIT LIST (DiffUtil)
    // ============================================================

    public void submitList(List<AppListActivity.AppEntry> newList) {

        DiffUtil.DiffResult diff =
                DiffUtil.calculateDiff(new DiffCallback(this.data, newList));

        this.data.clear();
        this.data.addAll(newList);

        diff.dispatchUpdatesTo(this);
    }

    @Override
    public long getItemId(int position) {
        AppListActivity.AppEntry e = data.get(position);
        if (e.isHeader) return e.headerTitle.hashCode();
        return e.pkg != null ? e.pkg.hashCode() : position;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isHeader ? TYPE_HEADER : TYPE_APP;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ============================================================
    // VIEW HOLDERS
    // ============================================================

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView title;
        HeaderHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.headerTitle);
        }
    }

    static class AppHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView pkg;
        TextView size;
        TextView cache;
        ImageView icon;
        CheckBox select;

        AppHolder(@NonNull View itemView) {
            super(itemView);
            name   = itemView.findViewById(R.id.appLabel);
            pkg    = itemView.findViewById(R.id.appPackage);
            size   = itemView.findViewById(R.id.appSize);
            cache  = itemView.findViewById(R.id.appCache);
            icon   = itemView.findViewById(R.id.appIcon);
            select = itemView.findViewById(R.id.appCheck);
        }
    }

    // ============================================================
    // CREATE VIEW
    // ============================================================

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.list_item_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = inflater.inflate(R.layout.list_item_app, parent, false);
            return new AppHolder(v);
        }
    }

    // ============================================================
    // BIND
    // ============================================================

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position) {

        AppListActivity.AppEntry e = data.get(position);

        if (getItemViewType(position) == TYPE_HEADER) {
            HeaderHolder h = (HeaderHolder) holder;
            h.title.setText(e.headerTitle);
            return;
        }

        AppHolder h = (AppHolder) holder;

        h.name.setText(
                TextUtils.isEmpty(e.label) ? "Unknown" : e.label
        );

        try {
            h.icon.setImageDrawable(pm.getApplicationIcon(e.pkg));
        } catch (Exception ignored) {
            h.icon.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        h.pkg.setText(e.pkg);

        if (e.isSystem) {
            h.name.setTextColor(0xFFFFD700);
        } else {
            h.name.setTextColor(Color.WHITE);
        }

        h.size.setText("App: " + formatBytes(e.appBytes));
        h.cache.setText("Cache: " + formatBytes(e.cacheBytes));

        // ============================================================
        // ROOT PROTECTION LOGIC (ONLY HERE)
        // ============================================================

        h.select.setOnCheckedChangeListener(null);
        h.select.setChecked(e.selected);

        h.select.setOnCheckedChangeListener((b, checked) -> {

            // If trying to select system app in uninstall mode without root
            if (ctx instanceof AppListActivity) {

                AppListActivity activity = (AppListActivity) ctx;

                if (activity.isUninstallMode()
                        && !activity.isDeviceRooted()
                        && e.isSystem) {

                    // Block selection
                    b.setChecked(false);
                    activity.showRootRequiredDialog();
                    return;
                }
            }

            e.selected = checked;
        });
    }

    // ============================================================
    // FORMAT BYTES
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

    // ============================================================
    // DIFF CALLBACK
    // ============================================================

    static class DiffCallback extends DiffUtil.Callback {

        private final List<AppListActivity.AppEntry> oldList;
        private final List<AppListActivity.AppEntry> newList;

        DiffCallback(
                List<AppListActivity.AppEntry> oldList,
                List<AppListActivity.AppEntry> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {

            AppListActivity.AppEntry o = oldList.get(oldPos);
            AppListActivity.AppEntry n = newList.get(newPos);

            if (o.isHeader && n.isHeader)
                return TextUtils.equals(o.headerTitle, n.headerTitle);

            return o.pkg != null && o.pkg.equals(n.pkg);
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {

            AppListActivity.AppEntry o = oldList.get(oldPos);
            AppListActivity.AppEntry n = newList.get(newPos);

            return o.selected == n.selected &&
                   o.cacheBytes == n.cacheBytes &&
                   o.appBytes == n.appBytes &&
                   TextUtils.equals(o.label, n.label);
        }
    }
}
