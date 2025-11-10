package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
    LayoutInflater inf;
    PackageManager pm;

    public AppListAdapter(Context c, List<AppListActivity.AppInfo> d) {
        ctx = c;
        data = d;
        inf = LayoutInflater.from(c);
        pm = c.getPackageManager();
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

    @Override
    public View getView(int pos, View v, ViewGroup parent) {

        Holder h;
        if (v == null) {
            v = inf.inflate(R.layout.item_app, parent, false);
            h = new Holder(v);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }

        AppListActivity.AppInfo a = data.get(pos);

        Drawable icon;
        try {
            icon = pm.getApplicationIcon(a.pkg);
        } catch (Exception e) {
            icon = ctx.getDrawable(android.R.drawable.sym_def_app_icon);
        }

        h.icon.setImageDrawable(icon);
        h.label.setText(a.label);
        h.pkg.setText(a.pkg);

        return v;
    }

    static class Holder {
        ImageView icon;
        TextView label, pkg;

        Holder(View v){
            icon = v.findViewById(R.id.appIcon);
            label = v.findViewById(R.id.appLabel);
            pkg = v.findViewById(R.id.appPkg);
        }
    }
}
