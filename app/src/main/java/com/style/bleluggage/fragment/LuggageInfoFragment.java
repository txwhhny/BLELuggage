package com.style.bleluggage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.style.bleluggage.ApplicationTrans;
import com.style.bleluggage.BluetoothCenter;
import com.style.bleluggage.ProtocolHelper;
import com.style.bleluggage.R;

/**
 * Created by Administrator on 2016/11/18.
 */

public class LuggageInfoFragment extends Fragment implements View.OnClickListener {

    private final int MSG_DATA_MAXSPEED = 0;
    private final int MSG_DATA_MILEAGE = 1;
    private final int MSG_DATA_POWER = 2;
    private final int MSG_DATA_WEIGHTNET = 3;
    private final int MSG_DATA_WEIGHTGROSS = 4;
    private final int MSG_DATA_ALARM_RAD = 5;
    private final int MSG_DATA_ALARM_TEL = 6;
    private final int MSG_DATA_ALARM_SET = 7;

    private ProtocolHelper mProtocolHelper;
    private BluetoothCenter mtBtCenter;
    private Activity mThisActivity;
    private Handler mHandler;
    private ProtocolHelper.OnQueryReplyListener mQueryReplyListener;
    private ProtocolHelper.OnSetReplyListener mSetReplyListener;

    private EditText metAlarmRad, metAlarmTel, metMaxSpeed;
    private TextView mtvMileage, mtvPower, mtvWeightNet, mtvWeightGross;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_luggage_info,null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mThisActivity = getActivity();
        metAlarmRad = (EditText) mThisActivity.findViewById(R.id.etAlarmRad);
        metAlarmTel = (EditText) mThisActivity.findViewById(R.id.etAlarmTel);
        metMaxSpeed = (EditText) mThisActivity.findViewById(R.id.etMaxSpeed);
        mtvMileage = (TextView) mThisActivity.findViewById(R.id.tvMileage);
        mtvPower = (TextView) mThisActivity.findViewById(R.id.tvPower);
        mtvWeightNet = (TextView) mThisActivity.findViewById(R.id.tvWeightNet);
        mtvWeightGross = (TextView) mThisActivity.findViewById(R.id.tvWeightGross);
        mThisActivity.findViewById(R.id.btnQuery).setOnClickListener(this);
        mThisActivity.findViewById(R.id.btnSet).setOnClickListener(this);

        mtBtCenter = ((ApplicationTrans)mThisActivity.getApplication()).getBluetoothCenter();

        mProtocolHelper = ((ApplicationTrans)mThisActivity.getApplication()).getProtocolHelper();
        mQueryReplyListener = mProtocolHelper.new OnQueryReplyListener()
        {
            @Override
            public void onMaxSpeedGot(int iMaxSpeed) {
                super.onMaxSpeedGot(iMaxSpeed);
                Message msg = new Message();
                msg.what = MSG_DATA_MAXSPEED;
                msg.obj = iMaxSpeed;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onMileageGot(int iMileage) {
                super.onMileageGot(iMileage);
                Message msg = new Message();
                msg.what = MSG_DATA_MILEAGE;
                msg.obj = iMileage;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onPowerGot(int iPower) {
                super.onPowerGot(iPower);
                Message msg = new Message();
                msg.what = MSG_DATA_POWER;
                msg.obj = iPower;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onWeightGrossGot(int iWeightGross) {
                super.onWeightGrossGot(iWeightGross);
                Message msg = new Message();
                msg.what = MSG_DATA_WEIGHTGROSS;
                msg.obj = iWeightGross;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onWeightNetGot(int iWeightNet) {
                super.onWeightNetGot(iWeightNet);
                Message msg = new Message();
                msg.what = MSG_DATA_WEIGHTNET;
                msg.obj = iWeightNet;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAlarmRadGot(int iAlarmRad) {
                super.onAlarmRadGot(iAlarmRad);
                Message msg = new Message();
                msg.what = MSG_DATA_ALARM_RAD;
                msg.obj = iAlarmRad;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAlarmTelGot(String strTelephone) {
                super.onAlarmTelGot(strTelephone);
                Message msg = new Message();
                msg.what = MSG_DATA_ALARM_TEL;
                msg.obj = strTelephone;
                mHandler.sendMessage(msg);
            }
        };
        mProtocolHelper.setOnQueryReplyListener(mQueryReplyListener);
        mSetReplyListener = mProtocolHelper.new OnSetReplyListener()
        {
            @Override
            public void onSetReply(String strObject) {
                super.onSetReply(strObject);
                Message msg = new Message();
                msg.what = MSG_DATA_ALARM_SET;
                msg.obj = strObject;
                mHandler.sendMessage(msg);
            }
        };
        mProtocolHelper.setOnSetReplyListener(mSetReplyListener);

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case MSG_DATA_MAXSPEED:
                        metMaxSpeed.setText("" + msg.obj);
                        break;
                    case MSG_DATA_MILEAGE:
                        mtvMileage.setText("" + msg.obj);
                        break;
                    case MSG_DATA_POWER:
                        mtvPower.setText("" + msg.obj);
                        break;
                    case MSG_DATA_WEIGHTNET:
                        mtvWeightNet.setText("" + msg.obj);
                        break;
                    case MSG_DATA_WEIGHTGROSS:
                        mtvWeightGross.setText("" + msg.obj);
                        break;
                    case MSG_DATA_ALARM_RAD:
                        metAlarmRad.setText("" + msg.obj);
                        break;
                    case MSG_DATA_ALARM_TEL:
                        metAlarmTel.setText(msg.obj.toString());
                        break;
                    case MSG_DATA_ALARM_SET:
                        Toast.makeText(mThisActivity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
        {
            mProtocolHelper.setOnQueryReplyListener(mQueryReplyListener);
            mProtocolHelper.setOnSetReplyListener(mSetReplyListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnQuery:
                mtBtCenter.writeCharacteristic(mProtocolHelper.queryAlarmRadAndTelMsg());
                mtBtCenter.writeCharacteristic(mProtocolHelper.queryWeightNetAndGrossMsg());
                mtBtCenter.writeCharacteristic(mProtocolHelper.queryMaxSpeedMsg());
                mtBtCenter.writeCharacteristic(mProtocolHelper.queryMileageMsg());
                mtBtCenter.writeCharacteristic(mProtocolHelper.queryPowerMsg());
                break;
            case R.id.btnSet:
                int iMaxSpeed = Integer.parseInt(metMaxSpeed.getText().toString());
                mtBtCenter.writeCharacteristic(mProtocolHelper.setMaxSpeedMsg(iMaxSpeed));
                int iRad = Integer.parseInt(metAlarmRad.getText().toString());
                String strTel = metAlarmTel.getText().toString();
                mtBtCenter.writeCharacteristic(mProtocolHelper.setAlarmRadAndTel(iRad, strTel));
                break;
        }
    }
}
