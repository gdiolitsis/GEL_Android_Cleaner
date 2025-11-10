package com.gel.cleaner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {

    Context ctx;
    List<AppEntry> data;

    public AppListAdapter(Context ctx, List<AppEntry> data) {
        this.ctx = ctx;
        this.data = data;
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
    public View getView(int i, View v, ViewGroup parent) {

        if (v == null) {
            v = LayoutInflater.from(ctx).inflate(R.layout.row_app, parent, false);
        }

        TextView name = v.findViewById(R.id.txtName);
        TextView pkg  = v.findViewById(R.id.txtPkg);

        AppEntry e = data.get(i);

        name.setText(e.name);
        pkg.setText(e.pkg);

        return v;
    }
}
