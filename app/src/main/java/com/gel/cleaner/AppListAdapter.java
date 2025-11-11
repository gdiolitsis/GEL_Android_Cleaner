package com.gel.cleaner;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {

    Context ctx;
    List<AppListActivity.AppInfo> data;
    LayoutInflater inflater;

    public AppListAdapter(Context ctx, List<AppListActivity.AppInfo> data) {
        this.ctx = ctx;
        this.data = data;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
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

        AppListActivity.AppInfo a = data.get(position);

        if (a != null) {

            if (a.resolveInfo != null) {
                CharSequence label =
                        a.resolveInfo.loadLabel(ctx.getPackageManager());
                h.name.setText(label != null ? label : "Unknown");

                h.icon.setImageDrawable(
                        a.resolveInfo.loadIcon(ctx.getPackageManager())
                );
            }

            h.pkg.setText(a.packageName != null ? a.packageName : "");
        }

        return convertView;
    }
}
