package com.style.menu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/26.
 */

public class MenuListAdapter extends BaseAdapter{

    private class ViewHolder
    {
        ImageView ivTitle;
        TextView tvTitle;
    }


    public static final int ITEM_CATEGORY = 1;
    public static final int ITEM_SUBMENU = 2;

    private ArrayList<Integer> mLstMenuIcon;
    private ArrayList<String> mLstMenuItem;
    private ArrayList<Integer> mLstMenuType;
    private LayoutInflater minflator;


    @Override
    public int getCount() {
        return mLstMenuItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mLstMenuItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            if (mLstMenuType.get(position) == ITEM_SUBMENU)
            {
                convertView = minflator.inflate(R.layout.menu_item2, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.menu_item2_tv);
                viewHolder.ivTitle = (ImageView) convertView.findViewById(R.id.menu_item2_iv);
            }
            else
            {
                convertView = minflator.inflate(R.layout.menu_item1, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.menu_item1_tv);
                viewHolder.ivTitle = null;
            }
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (null != viewHolder.ivTitle)
        {
            viewHolder.ivTitle.setBackgroundResource(mLstMenuIcon.get(position));
        }
        viewHolder.tvTitle.setText(mLstMenuItem.get(position));
        return convertView;
    }


    public MenuListAdapter(Activity aty)
    {
        minflator = aty.getLayoutInflater();
        mLstMenuIcon = new ArrayList<>();
        mLstMenuItem = new ArrayList<>();
        mLstMenuType = new ArrayList<>();
    }

    public void addMenuItem(int iRes, String strMenu, int iType)
    {
        mLstMenuIcon.add(iRes);
        mLstMenuItem.add(strMenu);
        mLstMenuType.add(iType);
        notifyDataSetChanged();
    }
}
