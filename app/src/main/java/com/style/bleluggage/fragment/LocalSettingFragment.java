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
import android.widget.EditText;
import android.widget.ListView;

import com.style.bleluggage.ApplicationTrans;
import com.style.bleluggage.BLEDeviceAdapter;
import com.style.bleluggage.BluetoothCenter;
import com.style.bleluggage.DatabaseHelper;
import com.style.bleluggage.R;

/**
 * Created by Administrator on 2016/11/19.
 */

public class LocalSettingFragment extends Fragment implements View.OnClickListener {

    private Activity mThisActivity;
    private EditText metBoxMac, metBaseMac1, metBaseMac2, metBaseMac3, metSendInterval;
    private Button mbtnConn;
    private DatabaseHelper mtDatabase;
    private BluetoothCenter mtBtCenter;
    private BLEDeviceAdapter mtAdapter;

    private int idx = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_setting,null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mThisActivity = getActivity();

        metBoxMac = (EditText) mThisActivity.findViewById(R.id.etBoxMac);
        metBaseMac1 = (EditText) mThisActivity.findViewById(R.id.etLocateMac1);
        metBaseMac2 = (EditText) mThisActivity.findViewById(R.id.etLocateMac2);
        metBaseMac3 = (EditText) mThisActivity.findViewById(R.id.etLocateMac3);
        metSendInterval = (EditText) mThisActivity.findViewById(R.id.etSendInterval);

        mThisActivity.findViewById(R.id.btnQueryLocal).setOnClickListener(this);
        mThisActivity.findViewById(R.id.btnSaveLocal).setOnClickListener(this);
        mbtnConn = (Button) mThisActivity.findViewById(R.id.btnConn);
        mbtnConn.setOnClickListener(this);

        mtBtCenter = ((ApplicationTrans)mThisActivity.getApplication()).getBluetoothCenter();

        // 尝试连接到箱包的蓝牙，如果手机蓝牙尚未打开，则无法连接，在MainActivity的手机蓝牙开启后的代码，也有尝试连接的操作。
        mtDatabase = ((ApplicationTrans)mThisActivity.getApplication()).getDatabaseHelper();
        mtBtCenter.connect(BluetoothCenter.DEV_IDX_BOX, mtDatabase.getDevMac(DatabaseHelper.IDX_BOX_MAC));

        mtAdapter = ((ApplicationTrans)mThisActivity.getApplication()).getBleScanAdapter(mThisActivity);

        ((ListView)mThisActivity.findViewById(R.id.lvDevListParam)).setAdapter(mtAdapter);
        loadData();
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
    public void onClick(View v) {

        if (v.getId() == R.id.btnConn)
        {
            mtBtCenter.startLeScan();
        }
        else if (v.getId() == R.id.btnQueryLocal)
        {
            loadData();
        }
        else if (v.getId() == R.id.btnSaveLocal)
        {
            mtDatabase.setDevMac(DatabaseHelper.IDX_BOX_MAC, metBoxMac.getText().toString().toUpperCase());
            mtDatabase.setDevMac(DatabaseHelper.IDX_LOC1_MAC, metBaseMac1.getText().toString().toUpperCase());
            mtDatabase.setDevMac(DatabaseHelper.IDX_LOC2_MAC, metBaseMac2.getText().toString().toUpperCase());
            mtDatabase.setDevMac(DatabaseHelper.IDX_LOC3_MAC, metBaseMac3.getText().toString().toUpperCase());
            mtDatabase.setSendInterval(Integer.parseInt(metSendInterval.getText().toString()));
        }
    }

    private void loadData()
    {
        metBoxMac.setText(mtDatabase.getDevMac(DatabaseHelper.IDX_BOX_MAC));
        metBaseMac1.setText(mtDatabase.getDevMac(DatabaseHelper.IDX_LOC1_MAC));
        metBaseMac2.setText(mtDatabase.getDevMac(DatabaseHelper.IDX_LOC2_MAC));
        metBaseMac3.setText(mtDatabase.getDevMac(DatabaseHelper.IDX_LOC3_MAC));
        String strInterval = "" + mtDatabase.getSendInterval();
        metSendInterval.setText(strInterval);
    }
}
