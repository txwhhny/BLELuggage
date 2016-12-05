package com.style.bleluggage.fragment;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.style.bleluggage.ApplicationTrans;
import com.style.bleluggage.BluetoothCenter;
import com.style.bleluggage.ProtocolHelper;
import com.style.bleluggage.R;
import com.style.hxc.libnavcontroller.NavController;


import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/11/14.
 */

public class ManualFragment extends Fragment implements NavController.OnNavMovingListener, CompoundButton.OnCheckedChangeListener {

    private static final int ENABLE_BT_REQUEST_ID = 1;
    private static final int MSG_DATA_MILEAGE = 0;
    private TextView mtvSpeed;
    private TextView mtvRadian;
    private TextView mtvMileage;
    private CheckBox mchkLocked;
    private NavController mnavController;
    private Activity mThisActivity;
    private BluetoothCenter mtBtCenter;
    private ProtocolHelper mProtocolHelper;
    private Handler mHandler;
    private ProtocolHelper.OnQueryReplyListener mQueryReplyListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual,null);
    }


    @Override
    public void onStart() {
        super.onStart();
        mThisActivity = getActivity();
        mtvSpeed = (TextView) mThisActivity.findViewById(R.id.tvSpeed);
        mtvRadian = (TextView) mThisActivity.findViewById(R.id.tvRadian);
        mtvMileage = (TextView) mThisActivity.findViewById(R.id.tvMileage2);
        mchkLocked = (CheckBox) mThisActivity.findViewById(R.id.chkLocked);
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_DATA_MILEAGE)
                {
                    mtvMileage.setText("总里程：" + msg.obj + "m");
                }
            }
        };

        mnavController = (NavController)mThisActivity.findViewById(R.id.navController2);
        mnavController.setOnNavMovingListener(this);
        mchkLocked.setOnCheckedChangeListener(this);
        mchkLocked.setChecked(mnavController.getIsLocked());

        ApplicationTrans app = (ApplicationTrans) mThisActivity.getApplication();
        mtBtCenter = app.getBluetoothCenter();
        mProtocolHelper = app.getProtocolHelper();

        mQueryReplyListener = mProtocolHelper.new  OnQueryReplyListener()
        {
            @Override
            public void onMileageGot(int iMileage) {
                super.onMileageGot(iMileage);
                Message msg = new Message();
                msg.what = MSG_DATA_MILEAGE;
                msg.obj = iMileage;
                mHandler.sendMessage(msg);
            }
        };
        mProtocolHelper.setOnQueryReplyListener(mQueryReplyListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
        {
            mProtocolHelper.setOnQueryReplyListener(mQueryReplyListener);
        }
    }
    @Override
    public void onNavMoving(int iRadian, int iSpeedPower)
    {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("000");
        mtvRadian.setText("方位：" + df.format(iRadian) + "°");
        mtvSpeed.setText("速度：" + df.format(iSpeedPower) + "%");
        byte [] data = mProtocolHelper.setSpeedAndAngleMsg(iSpeedPower, iRadian);
        mtBtCenter.writeCharacteristic(data);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mnavController.setIsLocked(isChecked);
    }
}
