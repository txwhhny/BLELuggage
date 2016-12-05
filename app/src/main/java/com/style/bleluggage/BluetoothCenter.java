package com.style.bleluggage;

import java.util.List;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BluetoothCenter {

	public class OnBLEDataReadListener
	{
		public void onBLEDataRead(byte[] data)
		{
			Log.e("testhxc..222.:", new String(data));
        }
	}

	public class OnConnectionStateChangeListener
	{
		public void onConnectionStateChange(int idxConn, String mac, String name, boolean newStat){}
	}

    public class OnRssiReadListener
    {
        public void onRssiRead(String devMac, int rssi){};
    }

	public class OnDeviceFoundListener
	{
		public void onDeviceFound(String devAddress, String devName, int rssi){}
	}

    private class ConnectInfo
    {
        BluetoothGatt mGatt = null;
        boolean mConnectStat = false;
    }

	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";

	// member data
	private final String TAG = this.getClass().getSimpleName();

	public static final int DEV_IDX_BOX = 0;
	public static final int DEV_IDX_LOC1 = 1;
	public static final int DEV_IDX_LOC2 = 2;
	public static final int DEV_IDX_LOC3 = 3;

	public BluetoothAdapter m_btAdapter = null;
	private BluetoothManager m_btManager = null;
    private ConnectInfo [] mConn;
    private int mIndexRssiReq = 0;
    private boolean mEnableRssiRead = false;
	private Context m_tContext;
    private BluetoothGattCharacteristic m_rwChar = null;

	private boolean mbEnableScan = false;
	private boolean mbScanning = false;
	private Handler mHandler;
	private OnBLEDataReadListener mDataReadListener;
	private OnConnectionStateChangeListener mConnectListener;
    public OnRssiReadListener mRssiReadListener;
	private OnDeviceFoundListener mDeviceFoundListener;

	// 扫描回调
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
	{
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (null != mDeviceFoundListener)
			{
				mDeviceFoundListener.onDeviceFound(device.getAddress(), device.getName(), rssi);
			}
		}
	};

	// 连接到BLE时，检测到所支持的服务的回调
	private final BluetoothGattCallback m_btGattCallback = new BluetoothGattCallback()
	{
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onServicesDiscovered(gatt, status);
            if (gatt == mConn[DEV_IDX_BOX].mGatt)
            {
                displayGattServices(gatt.getServices());
            }
        }

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(gatt, status, newState);

            int idx = 0;
            boolean connStat = (newState == BluetoothProfile.STATE_CONNECTED && 0 == status);
            if(gatt == mConn[DEV_IDX_BOX].mGatt)
            {
                idx = DEV_IDX_BOX;
            }
            else if (gatt == mConn[DEV_IDX_LOC1].mGatt)
            {
                idx = DEV_IDX_LOC1;
            }
            else if (gatt == mConn[DEV_IDX_LOC2].mGatt)
            {
                idx = DEV_IDX_LOC2;
            }
            else if (gatt == mConn[DEV_IDX_LOC3].mGatt)
            {
                idx = DEV_IDX_LOC3;
            }

            mConn[idx].mConnectStat = connStat;
            if (false == connStat)
            {
                mConn[idx].mGatt.close();
				mConn[idx].mGatt = null;
            }
            else if(gatt == mConn[DEV_IDX_BOX].mGatt)
            {
                Log.i(TAG, "Attempting to start service discovery:" +  gatt.discoverServices());		// Step 5.
            }

            if (null != mConnectListener)
			{
				BluetoothDevice dev = gatt.getDevice();
				mConnectListener.onConnectionStateChange(idx, dev.getAddress(), dev.getName(), connStat);
			}
		}

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            if (BluetoothGatt.GATT_SUCCESS != status)   return;
            if (null != mRssiReadListener)
            {
                mRssiReadListener.onRssiRead(gatt.getDevice().getAddress(), rssi);
				Log.e("rssi_conn", gatt.getDevice().getAddress() + ":" + rssi);
            }
        }

        @Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicWrite(gatt, characteristic, status);
			
			if (BluetoothGatt.GATT_SUCCESS  == status)
			{
				Log.i(TAG, "onCharacteristicWrite success!");
			}
			else
			{
				Log.e(TAG, "onCharacteristicWrite error!");
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);

			// 读取来自已连接的蓝牙设备的数据
			if (mDataReadListener != null)
			{
				mDataReadListener.onBLEDataRead(characteristic.getValue());
			}
		}
	};
	
	// member function
	public BluetoothCenter(Context ctx) 
	{
		// TODO Auto-generated constructor stub
		m_tContext =  ctx;
		mHandler = new Handler();
        mConn = new ConnectInfo[4];
        for (int i = 0; i < mConn.length; ++i)
        {
            mConn[i] = new ConnectInfo();
        }
	}

	// Step 1.
	public boolean initialize()
	{
		if (m_btManager == null)
		{
			m_btManager = (BluetoothManager) m_tContext.getSystemService(Context.BLUETOOTH_SERVICE);
			if (null == m_btManager)
			{
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}
		
		m_btAdapter = m_btManager.getAdapter();
		if (null == m_btAdapter)
		{
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		return true;
	}

	public boolean getAdapterEnableStat()
	{
		return m_btAdapter.isEnabled();
	}

	// Step 2.
	public void setOnBLEDataReadListener(OnBLEDataReadListener l)
	{
		mDataReadListener = l;
	}

	public void setOnConnectionStateChangeListener(OnConnectionStateChangeListener l)
	{
		mConnectListener = l;
	}

    public void setOnRssiReadListener(OnRssiReadListener l)
    {
        mRssiReadListener = l;
    }

	public void setOnDeviceFoundListener(OnDeviceFoundListener l)
	{
		mDeviceFoundListener = l;
	}

	// Step 3.
    public boolean connect(int idx, String mac)
    {
		// 如果原来已经连接，则直接返回false，不管mac是否与原来一致，均需要先断开连接，再调用此函数。
        if (mConn[idx].mConnectStat || m_btAdapter == null || !m_btAdapter.isEnabled())    return false;
        if (null == mac || mac.length() <= 0)     return false;
        if (idx < DEV_IDX_BOX || idx > DEV_IDX_LOC3) return false;

        BluetoothDevice device;
        device = m_btAdapter.getRemoteDevice(mac);
        if (null == device) return false;
        if (null != mConn[idx].mGatt)
        {
            mConn[idx].mGatt.close();
        }
        mConn[idx].mGatt = device.connectGatt(m_tContext, false, m_btGattCallback);
        return true;
    }

    public void disConnect(int idx)
    {
        if (idx < DEV_IDX_BOX || idx > DEV_IDX_LOC3) return;
        if (mConn[idx].mConnectStat)
        {
            mConn[idx].mGatt.disconnect();
        }
    }

	public boolean getConnectionStat(int idx)
	{
        if(idx < DEV_IDX_BOX || idx > DEV_IDX_LOC3)  return false;
        return mConn[idx].mConnectStat;
	}
    
    private void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (m_btAdapter == null || mConn[DEV_IDX_BOX].mGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
		mConn[DEV_IDX_BOX].mGatt.readCharacteristic(characteristic);
    }

	public void writeCharacteristic(byte[] data)
	{
        if (m_rwChar == null || !mConn[DEV_IDX_BOX].mConnectStat)   return;
        m_rwChar.setValue(data);
        mConn[DEV_IDX_BOX].mGatt.writeCharacteristic(m_rwChar);
	}
    
	private void displayGattServices(List<BluetoothGattService> gattServices) 
	{
	    if (gattServices == null) return;
	
	    for (BluetoothGattService gattService : gattServices) {
	    	int type = gattService.getType();
	        Log.e(TAG,"-->service type:"+Utils.getServiceType(type));
	        Log.e(TAG,"-->includedServices size:"+gattService.getIncludedServices().size());
	        Log.e(TAG,"-->service uuid:"+gattService.getUuid());
	        
	        List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
	        for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
	            Log.e(TAG,"---->char uuid:"+gattCharacteristic.getUuid());
	            
	            int permission = gattCharacteristic.getPermissions();
	            Log.e(TAG,"---->char permission:"+Utils.getCharPermission(permission));
	            
	            int property = gattCharacteristic.getProperties();
	            Log.e(TAG,"---->char property:"+Utils.getCharPropertie(property));
	
	            byte[] data = gattCharacteristic.getValue();
	    		if (data != null && data.length > 0) {
	    			Log.e(TAG,"---->char value:"+new String(data));
	    		}
	
	    		if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA))
	    		{        			
	    			mHandler.postDelayed(new Runnable()
	    			{
	                    @Override
	                    public void run()
	                    {
	                    	readCharacteristic(gattCharacteristic);
	                    }
	                }, 500);

	    			mConn[DEV_IDX_BOX].mGatt.setCharacteristicNotification(gattCharacteristic, true);
                    m_rwChar = gattCharacteristic;
	    		}
	    		
				List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
				for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) 
				{
					Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
					int descPermission = gattDescriptor.getPermissions();
					Log.e(TAG,"-------->desc permission:"+ Utils.getDescPermission(descPermission));
					
					byte[] desData = gattDescriptor.getValue();
					if (desData != null && desData.length > 0) {
						Log.e(TAG, "-------->desc value:"+ new String(desData));
					}
	    		 }
	        }
	    }//
	}

    // -----------------读取已连接设备的rssi----------------------
    public void startReadRssi(int interval)
    {
        mEnableRssiRead = true;
        readRssiReq(interval);
    }
    public void stopReadRssi()
    {
        mEnableRssiRead = false;
    }
    private void readRssiReq(final int interval)
    {
        if (!mEnableRssiRead)   return;
        if (mConn[mIndexRssiReq].mConnectStat)
        {
            mConn[mIndexRssiReq].mGatt.readRemoteRssi();
        }
        mIndexRssiReq++;
        if (mIndexRssiReq >= 4) mIndexRssiReq = 0;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                readRssiReq(interval);
            }
        }, interval / 4);
    }
	// -----------------扫描检测周边的BLE设备--------------------
    public void startLeScan()
    {
        m_btAdapter.startLeScan(mLeScanCallback);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_btAdapter.stopLeScan(mLeScanCallback);
            }
        }, 5000);
    }
	public void startLeScan(int iScanInterval, int iWaitInterval)
	{
		if (!m_btAdapter.isEnabled())   return;
		mbScanning = false;
		mbEnableScan = true;
		leScanLoop(iScanInterval, iWaitInterval);
	}

	public void stopLeScan()
	{
		mbEnableScan = false;
	}

	private void leScanLoop(final int iScanInterval, final int iWaitInterval)
	{
		if (!mbEnableScan && !mbScanning)       return;

		if (!mbScanning)
		{
            mbScanning = true;
			m_btAdapter.startLeScan(mLeScanCallback);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					leScanLoop(iScanInterval, iWaitInterval);
				}
			}, iScanInterval);
		}
		else
		{
            mbScanning = false;
			m_btAdapter.stopLeScan(mLeScanCallback);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					leScanLoop(iScanInterval, iWaitInterval);
				}
			}, iWaitInterval);
		}
	}
	// -------------------------------------------------------------
}


