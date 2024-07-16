package com.kirara.customdrawer;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawerAdapter extends ArrayAdapter<DrawerAdapter.DrawerRow> {
    public static final int HEADER_MENU = 0;
    public static final int ITEM_HOME = 1;
    public static final int ITEM_ARTICLE = 2;
    public static final int ITEM_GRAPH = 3;
    public static final int HEADER_GENERAL = 4;
    public static final int ITEM_SETTINGS = 5;

    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    public static class DrawerRow {
        int id;
        String title;

        private DrawerRow(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public static class DrawerHeader extends DrawerRow {

        public DrawerHeader(int id, String title) {
            super(id, title);
        }
    }

    public static class DrawerItem extends DrawerRow {
        int icon;

        public DrawerItem(int id, String title, int icon) {
            super(id, title);
            this.icon = icon;
        }
    }

    public DrawerAdapter(Context context){
        super(context, 0);
        add(new DrawerAdapter.DrawerHeader(HEADER_MENU, context.getString(R.string.menu)));
        add(new DrawerItem(ITEM_HOME, context.getString(R.string.home), R.drawable.home));
        add(new DrawerItem(ITEM_ARTICLE, context.getString(R.string.article), R.drawable.ic_article));
        add(new DrawerItem(ITEM_GRAPH, context.getString(R.string.graph), R.drawable.ic_graph));
        add(new DrawerAdapter.DrawerHeader(HEADER_GENERAL, context.getString(R.string.general)));
        add(new DrawerItem(ITEM_SETTINGS, context.getString(R.string.settings), R.drawable.ic_settings));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        int viewType = getItemViewType(position);

        if (v == null) {
            if (viewType == HEADER_TYPE) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.list_drawer_header, parent, false);
            } else if (viewType == ITEM_TYPE) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.list_drawer_item, parent, false);
            }
        }

        if (viewType == HEADER_TYPE) {
            DrawerHeader header = (DrawerHeader) getItem(position);
            TextView title = v.findViewById(R.id.drawer_header_title);
            title.setText(header.title);
        } else if (viewType == ITEM_TYPE) {
            DrawerItem item = (DrawerItem) getItem(position);
            TextView title = v.findViewById(R.id.drawer_item_title);
            ImageView icon = v.findViewById(R.id.drawer_item_icon);
            title.setText(item.title);
            icon.setImageResource(item.icon);

            int textColor = title.getCurrentTextColor();
            textColor &= 0x00FFFFFF; // Clear alpha bits
            textColor |=  0xFF000000; // Set alpha bits depending on whether the state is enabled or disabled
            title.setTextColor(textColor);
            icon.setColorFilter(textColor, PorterDuff.Mode.MULTIPLY);

        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public DrawerRow getItemWithId(int id) {
        for(int x=0;x<getCount();x++) {
            DrawerRow row = getItem(x);
            if(row.id == id) return row;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if(item instanceof DrawerHeader)
            return HEADER_TYPE;
        else if(item instanceof DrawerItem)
            return ITEM_TYPE;
        return ITEM_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
