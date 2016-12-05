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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.style.bleluggage.ApplicationTrans;
import com.style.bleluggage.BLEDeviceAdapter;
import com.style.bleluggage.BluetoothCenter;
import com.style.bleluggage.DatabaseHelper;
import com.style.bleluggage.ProtocolHelper;
import com.style.bleluggage.R;

/**
 * Created by Administrator on 2016/11/21.
 */

public class FollowFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, BLEDeviceAdapter.OnRssiReadListener, View.OnClickListener {

    private class ConnStatMsg
    {
        int idxConn;
        String mac;
        String name;
        boolean newStat;
    }

    private static final int IDX_BOX_CONN = 0;
    private static final int IDX_LOC1_CONN = 1;
    private static final int IDX_LOC2_CONN = 2;
    private static final int IDX_LOC3_CONN = 3;

    private boolean mAutoReconnsEnable = false;

    private Activity mThisActivity;
    private BluetoothCenter mtBtCenter;
    private BLEDeviceAdapter mtBleDevAdapter;
    private DatabaseHelper mtDatabaseHelper;
    private ProtocolHelper mtProtocolHelper;
    private Button mBtnBoxConn;
    private Button mBtnLocateMac1;
    private Button mBtnLocateMac2;
    private Button mBtnLocateMac3;
    private TextView mTvBoxMac;
    private TextView mTvLocateMac1;
    private TextView mTvLocateMac2;
    private TextView mTvLocateMac3;
    private boolean mbEnableFollow = false;
    private boolean [] mAutoReconns;
    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAutoReconns = new boolean[4];
        mAutoReconns[IDX_BOX_CONN] = true;
        mAutoReconns[IDX_LOC1_CONN] = false;
        mAutoReconns[IDX_LOC2_CONN] = false;
        mAutoReconns[IDX_LOC3_CONN] = false;
        return inflater.inflate(R.layout.fragment_follow,null);
    }

    @Override
    public void onStart() {     // 每次解锁后都会执行此函数，所以有些只能被赋一次初值的和不能被多次调用的函数需要特别注意
        super.onStart();
        mThisActivity = getActivity();
        ApplicationTrans app = (ApplicationTrans)mThisActivity.getApplication();
        mtProtocolHelper = app.getProtocolHelper();

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                ConnStatMsg info = (ConnStatMsg) msg.obj;
                mtBleDevAdapter.addDevice(info.mac, info.name, -128);
                switch (info.idxConn)
                {
                    case IDX_BOX_CONN:
                        setBtnText(mBtnBoxConn, info.newStat);
                        break;
                    case IDX_LOC1_CONN:
                        setBtnText(mBtnLocateMac1, info.newStat);
                        break;
                    case IDX_LOC2_CONN:
                        setBtnText(mBtnLocateMac2, info.newStat);
                        break;
                    case IDX_LOC3_CONN:
                        setBtnText(mBtnLocateMac3, info.newStat);
                        break;
                }
            }
        };

        mtBleDevAdapter = app.getBleRssiAdapter(mThisActivity);
        mtBleDevAdapter.setOnRssiReadListener(this);
        mtBtCenter = app.getBluetoothCenter();
        BluetoothCenter.OnConnectionStateChangeListener l = mtBtCenter.new OnConnectionStateChangeListener()
        {
            @Override
            public void onConnectionStateChange(int idxConn, String mac, String name, boolean newStat) {
                super.onConnectionStateChange(idxConn, mac, name, newStat);
                Message msg = new Message();
                ConnStatMsg info = new ConnStatMsg();
                info.idxConn = idxConn;
                info.mac = mac;
                info.name = name;
                info.newStat = newStat;
                msg.obj = info;
                mHandler.sendMessage(msg);
            }
        };
        mtBtCenter.setOnConnectionStateChangeListener(l);

        mtDatabaseHelper = app.getDatabaseHelper();
        ((ListView)mThisActivity.findViewById(R.id.lvDevListFollow)).setAdapter(mtBleDevAdapter);
        ((CheckBox)mThisActivity.findViewById(R.id.chkFollow)).setOnCheckedChangeListener(this);
        mTvBoxMac = (TextView) mThisActivity.findViewById(R.id.tvBoxMac);
        mTvLocateMac1 = (TextView) mThisActivity.findViewById(R.id.tvLocateMac1);
        mTvLocateMac2 = (TextView) mThisActivity.findViewById(R.id.tvLocateMac2);
        mTvLocateMac3 = (TextView) mThisActivity.findViewById(R.id.tvLocateMac3);
        mBtnBoxConn = (Button) mThisActivity.findViewById(R.id.btnBoxConn);
        mBtnLocateMac1 = (Button) mThisActivity.findViewById(R.id.btnLocateMac1);
        mBtnLocateMac2 = (Button) mThisActivity.findViewById(R.id.btnLocateMac2);
        mBtnLocateMac3 = (Button) mThisActivity.findViewById(R.id.btnLocateMac3);
        mBtnBoxConn.setOnClickListener(this);
        mBtnLocateMac1.setOnClickListener(this);
        mBtnLocateMac2.setOnClickListener(this);
        mBtnLocateMac3.setOnClickListener(this);
        loadData();
        if (!mAutoReconnsEnable)        // 每次手机解锁都会执行onStart函数，为了避免autoConnect被多次调用
        {
            autoConnect();
            mAutoReconnsEnable = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
        {
            loadData();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mbEnableFollow = isChecked;
        if (isChecked)
        {
//            mtBtCenter.startLeScan(mtDatabaseHelper.getSendInterval(), 10);
            mtBtCenter.startReadRssi(mtDatabaseHelper.getSendInterval());
        }
        else
        {
//            mtBtCenter.stopLeScan();
            mtBtCenter.stopReadRssi();
        }

        for (int i = IDX_LOC1_CONN ; i <= IDX_LOC3_CONN; i++)
        {
            mAutoReconns[i] = isChecked;
        }
    }

    @Override
    public void onRssiRead()
    {
        if (!mbEnableFollow)        return;
        byte[] rssiArray = new byte[3];
        rssiArray[0] = (byte) mtBleDevAdapter.getRSSI(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC1_MAC));
        rssiArray[1] = (byte) mtBleDevAdapter.getRSSI(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC2_MAC));
        rssiArray[2] = (byte) mtBleDevAdapter.getRSSI(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC3_MAC));

        byte[] msg = mtProtocolHelper.setRssiMsg(rssiArray[0], rssiArray[1], rssiArray[2]);
        mtBtCenter.writeCharacteristic(msg);
    }

    private void loadData()
    {
        mTvBoxMac.setText(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_BOX_MAC));
        mTvLocateMac1.setText(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC1_MAC));
        mTvLocateMac2.setText(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC2_MAC));
        mTvLocateMac3.setText(mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC3_MAC));

        setBtnText(mBtnBoxConn, mtBtCenter.getConnectionStat(BluetoothCenter.DEV_IDX_BOX));
        setBtnText(mBtnLocateMac1, mtBtCenter.getConnectionStat(BluetoothCenter.DEV_IDX_LOC1));
        setBtnText(mBtnLocateMac2, mtBtCenter.getConnectionStat(BluetoothCenter.DEV_IDX_LOC2));
        setBtnText(mBtnLocateMac3, mtBtCenter.getConnectionStat(BluetoothCenter.DEV_IDX_LOC3));
    }

    private void setBtnText(Button btn, boolean stat)
    {
        String [] connText = {getString(R.string.disconnText), getString(R.string.connText)};
        int [] connColor = {0xFFFF0000, 0xFF00C000};
        int idx = stat ? 1 : 0;
        btn.setText(connText[idx]);
        btn.setTextColor(connColor[idx]);
    }

    @Override
    public void onClick(View v) {
        int idxDb = 0;
        int idxBt = 0;
        int idxAutoConn = 0;
        String macToConn = "";
        switch (v.getId())
        {
            case R.id.btnBoxConn:
                idxDb = DatabaseHelper.IDX_BOX_MAC;
                idxBt = BluetoothCenter.DEV_IDX_BOX;
                idxAutoConn = IDX_BOX_CONN;
                break;
            case R.id.btnLocateMac1:
                idxDb = DatabaseHelper.IDX_LOC1_MAC;
                idxBt = BluetoothCenter.DEV_IDX_LOC1;
                idxAutoConn = IDX_LOC1_CONN;
                break;
            case R.id.btnLocateMac2:
                idxDb = DatabaseHelper.IDX_LOC2_MAC;
                idxBt = BluetoothCenter.DEV_IDX_LOC2;
                idxAutoConn = IDX_LOC2_CONN;
                break;
            case R.id.btnLocateMac3:
                idxDb = DatabaseHelper.IDX_LOC3_MAC;
                idxBt = BluetoothCenter.DEV_IDX_LOC3;
                idxAutoConn = IDX_LOC3_CONN;
                break;
        }
        macToConn = mtDatabaseHelper.getDevMac(idxDb);
        if (mtBtCenter.getConnectionStat(idxBt))
        {
            mtBtCenter.disConnect(idxBt);
            mAutoReconns[idxAutoConn] = false;
        }
        else
        {
            mtBtCenter.connect(idxBt, macToConn);
            mAutoReconns[idxAutoConn] = true;
        }
    }

    private void autoConnect()
    {
        if (mAutoReconns[IDX_BOX_CONN])
        {
            mtBtCenter.connect(BluetoothCenter.DEV_IDX_BOX, mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_BOX_MAC));
        }
        if (mAutoReconns[IDX_LOC1_CONN])
        {
            mtBtCenter.connect(BluetoothCenter.DEV_IDX_LOC1, mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC1_MAC));
        }
        if (mAutoReconns[IDX_LOC2_CONN])
        {
            mtBtCenter.connect(BluetoothCenter.DEV_IDX_LOC2, mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC2_MAC));
        }
        if (mAutoReconns[IDX_LOC3_CONN])
        {
            mtBtCenter.connect(BluetoothCenter.DEV_IDX_LOC3, mtDatabaseHelper.getDevMac(DatabaseHelper.IDX_LOC3_MAC));
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoConnect();
            }
        }, 3000);
    }
}
