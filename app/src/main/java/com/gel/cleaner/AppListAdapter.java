package com.gel.cleaner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private final Context ctx;
    private final LayoutInflater inflater;
    private final List<AppListActivity.AppInfo> data;

    public AppListAdapter(Context c, List<AppListActivity.AppInfo> d) {
        this.ctx = c;
        this.inflater = LayoutInflater.from(c);
        this.data = d;
    }

    @Override public int getCount() { return data.size(); }
    @Override public Object getItem(int position) { return data.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder h;
        View v = convertView;
        if (v == null) {
            // Χρησιμοποιούμε row_app.xml (πρέπει να υπάρχει ήδη)
            v = inflater.inflate(R.layout.row_app, parent, false);
            h = new RowHolder(v);
            v.setTag(h);
        } else {
            h = (RowHolder) v.getTag();
        }

        AppListActivity.AppInfo a = data.get(position);
        h.name.setText(a.label);
        h.pkg.setText(a.packageName);
        h.icon.setImageDrawable(a.resolveInfo.loadIcon(ctx.getPackageManager()));
        return v;
    }

    private static class RowHolder {
        final ImageView icon;
        final TextView  name;
        final TextView  pkg;

        RowHolder(View v) {
            icon = v.findViewById(R.id.appIcon);
            name = v.findViewById(R.id.appName);
            pkg  = v.findViewById(R.id.appPkg);
        }
    }
}
