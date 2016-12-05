package com.style.bleluggage;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.style.bleluggage.fragment.FollowFragment;
import com.style.bleluggage.fragment.HotNewsFragment;
import com.style.bleluggage.fragment.LateNewsFragment;
import com.style.bleluggage.fragment.LocalSettingFragment;
import com.style.bleluggage.fragment.LuggageInfoFragment;
import com.style.bleluggage.fragment.ManualFragment;
import com.style.menu.MenuListAdapter;

import java.util.ArrayList;

/**
 * @author  style
 * @version Version1.0
 */
public class MainActivity extends AppCompatActivity {

    private static final int ENABLE_BT_REQUEST_ID = 'b' | 'l' | 'e';

    private ListView lv;
    private FrameLayout fl;
    private ArrayList<String> list;
    private ArrayList<Integer> lstType;
    private Fragment fragment_hot_news, fragment_late_news, fragment_follow, fragment_manual, fragment_luggage_info, fragment_local_setting;
    private MenuListAdapter adapter;
    private DrawerLayout dl;
    private FragmentTransaction ft;
    private BluetoothCenter mtBtCenter;
    private DatabaseHelper mtDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawlayout_main);

        mtDatabaseHelper = ((ApplicationTrans)getApplication()).getDatabaseHelper();
        turnOnBluetooth();
        fl = (FrameLayout) findViewById(R.id.main_content_frame);
        lv = (ListView) findViewById(R.id.main_left_drawer_lv);
        dl = (DrawerLayout) findViewById(R.id.main_dl);

        adapter = new MenuListAdapter(this);
        initList(adapter);

        lv.setAdapter(adapter);

        //创建Fragment管理事务
        createFragment();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dl.closeDrawer(lv);
                switch (position)
                {
                    case 1:
                        showFragment(fragment_follow);
                        break;
                    case 2:
                        showFragment(fragment_manual);
                        break;
                    case 3:
                        showFragment(fragment_luggage_info);
                        break;
                    case 5:
                        showFragment(fragment_local_setting);
                        break;
                    case 6:
                        showFragment(fragment_late_news);
                        break;
                }
            }
        });


//        mtBtCenter.startLeScan(mtDatabaseHelper.getSendInterval(), 10);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.msgInfoTitle)
                .setMessage(R.string.exitMsg)
                .setPositiveButton(R.string.btnReturn, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.btnStop_Text, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                        System.exit(0);
                    }
                }).show();
    }

    // -------------------确认是否有蓝牙模块，并且已经启用------------------------
    private void turnOnBluetooth()
    {
        ApplicationTrans app = (ApplicationTrans) getApplication();
        mtBtCenter = app.getBluetoothCenter();
        if (!mtBtCenter.initialize())
        {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!mtBtCenter.getAdapterEnableStat())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
        }

        BluetoothCenter.OnDeviceFoundListener l = mtBtCenter.new OnDeviceFoundListener()
        {
            @Override
            public void onDeviceFound(final String devAddress, final String devName, final int rssi) {
                super.onDeviceFound(devAddress, devName, rssi);
                runOnUiThread(new Runnable() {
                    BLEDeviceAdapter adp = ((ApplicationTrans)getApplication()).getBleScanAdapter(MainActivity.this);
                    @Override
                    public void run() {
                        adp.addDevice(devAddress, devName, rssi);
                    }
                });
            }
        };
        mtBtCenter.setOnDeviceFoundListener(l);

        BluetoothCenter.OnRssiReadListener l2 = mtBtCenter.new OnRssiReadListener()
        {
            @Override
            public void onRssiRead(final String devMac, final int rssi) {
                super.onRssiRead(devMac, rssi);
                runOnUiThread(new Runnable() {
                    BLEDeviceAdapter adp = ((ApplicationTrans)getApplication()).getBleRssiAdapter(MainActivity.this);
                    @Override
                    public void run() {
                        adp.updateRssi(devMac, rssi);
                    }
                });
            }
        };
        mtBtCenter.setOnRssiReadListener(l2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BT_REQUEST_ID)
        {
            if(resultCode == AppCompatActivity.RESULT_CANCELED)
            {
                finish();
            }
            // 手机蓝牙打开之后，尝试连接到箱包的蓝牙，此外在LocalSetting页面也会尝试连接
            ApplicationTrans app = (ApplicationTrans) getApplication();
            mtBtCenter.connect(BluetoothCenter.DEV_IDX_BOX, app.getDatabaseHelper().getDevMac(DatabaseHelper.IDX_BOX_MAC));
//            mtBtCenter.startLeScan(mtDatabaseHelper.getSendInterval(), 10);
        }
    }
    // ---------------------------------------------------------------------------------

    private  void createFragment()
    {
        fragment_hot_news = new HotNewsFragment();
        fragment_late_news = new LateNewsFragment();
        fragment_follow = new FollowFragment();
        fragment_manual = new ManualFragment();
        fragment_luggage_info = new LuggageInfoFragment();
        fragment_local_setting = new LocalSettingFragment();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_content_frame, fragment_hot_news);
        ft.add(R.id.main_content_frame, fragment_late_news);
        ft.add(R.id.main_content_frame, fragment_follow);
        ft.add(R.id.main_content_frame, fragment_manual);
        ft.add(R.id.main_content_frame, fragment_luggage_info);
        ft.add(R.id.main_content_frame, fragment_local_setting);
        ft.commit();
    }

    /**
     * 隐藏已经初始化的Fragment
     */
    private void showFragment(Fragment page)
    {
        ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragment_hot_news);
        ft.hide(fragment_late_news);
        ft.hide(fragment_follow);
        ft.hide(fragment_manual);
        ft.hide(fragment_luggage_info);
        ft.hide(fragment_local_setting);
        ft.show(page);
        ft.commit();
    }

    private void initList(MenuListAdapter adp) {
        adp.addMenuItem(R.mipmap.hot, "智能箱包", MenuListAdapter.ITEM_CATEGORY);
        adp.addMenuItem(R.mipmap.test2, "跟随模式", MenuListAdapter.ITEM_SUBMENU);
        adp.addMenuItem(R.mipmap.test2, "手动模式", MenuListAdapter.ITEM_SUBMENU);
        adp.addMenuItem(R.mipmap.test3, "箱包参数", MenuListAdapter.ITEM_SUBMENU);
        adp.addMenuItem(R.mipmap.hot, "本地设置", MenuListAdapter.ITEM_CATEGORY);
        adp.addMenuItem(R.mipmap.hot, "地址信息", MenuListAdapter.ITEM_SUBMENU);
        adp.addMenuItem(R.mipmap.test1, "最新新闻", MenuListAdapter.ITEM_SUBMENU);
        adp.addMenuItem(R.mipmap.test4, "48小时阅读排行", MenuListAdapter.ITEM_SUBMENU);
    }
}