package com.gel.cleaner;

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

    public AppListAdapter(Context ctx, List<ResolveInfo> data) {
        this.ctx = ctx;
        this.data = data;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return (data == null ? 0 : data.size());
    }

    @Override
    public Object getItem(int i) {
        return (data == null ? null : data.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class Holder {
        TextView name;
        TextView pkg;
        ImageView icon;
    }

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
        } else {
            h = (Holder) convertView.getTag();
        }

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
                h.icon.setImageDrawable(
                        r.loadIcon(ctx.getPackageManager())
                );
            } catch (Exception ignored) {
                h.icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            // Package
            String pkg = "";
            if (r.activityInfo != null && r.activityInfo.packageName != null) {
                pkg = r.activityInfo.packageName;
            }
            h.pkg.setText(pkg);
        }

        return convertView;
    }
}
