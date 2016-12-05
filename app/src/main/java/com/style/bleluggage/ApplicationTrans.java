package com.style.bleluggage;

import android.app.Activity;
import android.app.Application;

public class ApplicationTrans extends Application {

	private ProtocolHelper mProtocolHelper;
	private DatabaseHelper m_tDatabase;
	private BluetoothCenter m_tBtCenter;
	private BLEDeviceAdapter mBleRssiAdapter;
	private BLEDeviceAdapter mBleScanAdapter;

	@Override
	public void onCreate() {
		super.onCreate();
		// 创建协议解析
		mProtocolHelper = new ProtocolHelper();
		// 创建数据库操作，并初始化数据，需要调用getApplicationContext()，所以无法放在构造函数，因为构造函数里面，调用该函数返回null
		m_tDatabase = new DatabaseHelper(getApplicationContext());
		m_tDatabase.initData();
		// 创建用于ListView显示的Adapter

		mBleScanAdapter = new BLEDeviceAdapter();
		mBleRssiAdapter = new BLEDeviceAdapter();
//		mBleRssiAdapter.addDevice(m_tDatabase.getDevMac(DatabaseHelper.IDX_BOX_MAC), "", -128);
//		mBleRssiAdapter.addDevice(m_tDatabase.getDevMac(DatabaseHelper.IDX_LOC1_MAC), "", -128);
//		mBleRssiAdapter.addDevice(m_tDatabase.getDevMac(DatabaseHelper.IDX_LOC2_MAC), "", -128);
//		mBleRssiAdapter.addDevice(m_tDatabase.getDevMac(DatabaseHelper.IDX_LOC3_MAC), "", -128);

		// 创建蓝牙通信模块
		m_tBtCenter = new BluetoothCenter(getApplicationContext());
		m_tBtCenter.setOnBLEDataReadListener(m_tBtCenter.new OnBLEDataReadListener() {
			@Override
			public void onBLEDataRead(byte[] data) {
				mProtocolHelper.parseMsg(data);
			}
		});
	}

	public DatabaseHelper getDatabaseHelper()
	{
		return m_tDatabase;
	}

	public ProtocolHelper getProtocolHelper()
	{
		return mProtocolHelper;
	}

	public BluetoothCenter getBluetoothCenter()
	{
		return m_tBtCenter;
	}



	public BLEDeviceAdapter getBleRssiAdapter(Activity aty)
	{
		mBleRssiAdapter.setAdapterContext(aty);
		return mBleRssiAdapter;
	}

	public BLEDeviceAdapter getBleScanAdapter(Activity aty)
	{
		mBleScanAdapter.setAdapterContext(aty);
		return mBleScanAdapter;
	}
}
