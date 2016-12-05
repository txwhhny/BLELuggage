package com.style.bleluggage;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BLEDeviceAdapter extends BaseAdapter {
	
//	private ApplicationTrans app;

	private LayoutInflater m_inflator;
    private ArrayList<DevInfo> mDevInfo;
    private OnRssiReadListener mOnRssiReadListener;

    private class DevInfo
    {
        String mDeviceMac;
        String mDeviceName;
        int mRssi;
        long mLastUpdateRssi = 0;
    }
		
	private class ViewHolder
	{
		TextView DevAddress;
		TextView DevName;
		TextView DevRssi;
	}

    public interface OnRssiReadListener
    {
        public void onRssiRead();
    }
	
	public BLEDeviceAdapter()
	{
        mDevInfo = new ArrayList<>();
	}
	
	public void setAdapterContext(Activity ctx)
	{
		m_inflator = ctx.getLayoutInflater();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
        return mDevInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
        return mDevInfo.get(position).mDeviceMac;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (convertView == null) 
		{
			convertView = m_inflator.inflate(R.layout.lvdevice_item, null);
			viewHolder = new ViewHolder();
			viewHolder.DevAddress = (TextView) convertView	.findViewById(R.id.tvDeviceAddr);
			viewHolder.DevName = (TextView) convertView.findViewById(R.id.tvDeviceName);
			viewHolder.DevRssi = (TextView) convertView.findViewById(R.id.tvRSSI);
			convertView.setTag(viewHolder);
		} 
		else 
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		DevInfo info = mDevInfo.get(position);
		if (null != info)
		{
			if (info.mDeviceMac != null && info.mDeviceMac.length() > 0)
			{
				viewHolder.DevAddress.setText(info.mDeviceMac);
			}
			if (info.mDeviceName != null && info.mDeviceName.length() > 0)
			{
				viewHolder.DevName.setText(info.mDeviceName);
			}
			viewHolder.DevRssi.setText("" + info.mRssi);
		}
		return convertView;
	}

	public void updateRssi(String strDevMac, int rssi)
    {
        if (rssi < -128)    rssi = -128;

		DevInfo info;
		for (int i = 0; i < mDevInfo.size(); i++)
		{
			info = mDevInfo.get(i);
			if (info.mDeviceMac.equals(strDevMac))
			{
				info.mRssi = rssi;
				info.mLastUpdateRssi = System.currentTimeMillis();
				if (0 == i && null != mOnRssiReadListener)
				{
					mOnRssiReadListener.onRssiRead();
				}
			}
			else if (System.currentTimeMillis() - info.mLastUpdateRssi > 5000)
			{
				info.mRssi = -128;
				info.mLastUpdateRssi = Long.MAX_VALUE;
			}
		}

        notifyDataSetChanged();
    }

	public void addDevice(String strDeviceMac, String strDevName, int rssi)
	{
        if (rssi < -128)    rssi = -128;

		DevInfo info;
		int i = 0;
		for (; i < mDevInfo.size(); i++)
		{
			info = mDevInfo.get(i);
			if (info.mDeviceMac.equals(strDeviceMac))
			{
				info.mDeviceName = strDevName;
				info.mRssi = rssi;
				break;
			}
		}

		if (i >= mDevInfo.size())	// 新设备
		{
			info = new DevInfo();
			info.mDeviceMac = strDeviceMac;
			info.mDeviceName = strDevName;
			info.mRssi = rssi;
			mDevInfo.add(info);
		}

		notifyDataSetChanged();
	}

    public void setOnRssiReadListener(OnRssiReadListener l)
    {
        mOnRssiReadListener = l;
    }
	
	public int getRSSI(String strAddress)
	{
		DevInfo info;
		for (int i = 0; i < mDevInfo.size(); i++)
		{
			info = mDevInfo.get(i);
			if (info.mDeviceMac.equals(strAddress))
			{
				return info.mRssi;
			}
		}
		return -128;
	}

}
