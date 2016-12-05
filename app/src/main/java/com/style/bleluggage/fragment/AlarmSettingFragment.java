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
import android.widget.Toast;

import com.style.bleluggage.ApplicationTrans;
import com.style.bleluggage.BluetoothCenter;
import com.style.bleluggage.ProtocolHelper;
import com.style.bleluggage.R;

/**
 * Created by Administrator on 2016/11/16.
 */

public class AlarmSettingFragment extends Fragment implements View.OnClickListener{

    private final int MSG_DATA_ALARM_RAD = 0;
    private final int MSG_DATA_ALARM_TEL = 1;
    private final int MSG_DATA_ALARM_SET = 2;

    private EditText etAlarmRad, etAlarmTel;
    private Activity mThisActivity;
    private Handler mHandler;
    private ProtocolHelper mProtocolHelper;
    private BluetoothCenter mtBtCenter;
    private ProtocolHelper.OnQueryReplyListener mQueryReplyListener;
    private ProtocolHelper.OnSetReplyListener mSetReplyListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_setting,null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mThisActivity = getActivity();
        etAlarmRad = (EditText) mThisActivity.findViewById(R.id.etAlarmRad);
        etAlarmTel = (EditText) mThisActivity.findViewById(R.id.etAlarmTel);
        mThisActivity.findViewById(R.id.btnQueryAlarm).setOnClickListener(this);
        mThisActivity.findViewById(R.id.btnSetAlarm).setOnClickListener(this);
        mProtocolHelper = ((ApplicationTrans)mThisActivity.getApplication()).getProtocolHelper();
        mtBtCenter = ((ApplicationTrans)mThisActivity.getApplication()).getBluetoothCenter();

        mQueryReplyListener = mProtocolHelper.new OnQueryReplyListener()
        {
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
                msg.obj = strObject + "设置成功！";
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
                    case MSG_DATA_ALARM_RAD:
                        etAlarmRad.setText("" + msg.obj);
                        break;
                    case MSG_DATA_ALARM_TEL:
                        etAlarmTel.setText(msg.obj.toString());
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
        mProtocolHelper.setOnQueryReplyListener(mQueryReplyListener);
        mProtocolHelper.setOnSetReplyListener(mSetReplyListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnQueryAlarm:
                mtBtCenter.writeCharacteristic(mProtocolHelper.queryAlarmRadAndTelMsg());
                break;
            case R.id.btnSetAlarm:
                int iRad = Integer.parseInt(etAlarmRad.getText().toString());
                String strTel = etAlarmTel.getText().toString();
                mtBtCenter.writeCharacteristic(mProtocolHelper.setAlarmRadAndTel(iRad, strTel));
                break;
        }
    }
}
