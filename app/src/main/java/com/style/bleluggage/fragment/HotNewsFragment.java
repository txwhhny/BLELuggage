package com.style.bleluggage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.style.bleluggage.R;

import com.style.test.TestView;
import com.style.hxc.libnavcontroller.NavController;

import org.w3c.dom.Text;

/**
 * Created by nanchen on 2016/6/24.
 */
public class HotNewsFragment extends Fragment implements View.OnClickListener, TestView.OnTestClickedListener, NavController.OnNavMovingListener {

    private boolean mIsLocked = false;
    private NavController mNavCtrl;
    private TestView mtvDisplay;
    private Activity mThisActivity;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot_news,null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mThisActivity = getActivity();
        mThisActivity.findViewById(R.id.btnTest).setOnClickListener(this);
        mtvDisplay = (TestView)mThisActivity.findViewById(R.id.testView);
        mtvDisplay.setTestOnClickedListener(this);
        mNavCtrl = (NavController)mThisActivity.findViewById(R.id.btnNavCtrl);
        mNavCtrl.setOnNavMovingListener(this);
        mIsLocked = mNavCtrl.getIsLocked();
    }

    @Override
    public void onClick(View v) {
        ((TextView)(mThisActivity.findViewById(R.id.tvTest))).setText("sdfsdf sdfsdf");
        Toast.makeText(mThisActivity, "提示信息", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnClicked() {
        Toast.makeText(mThisActivity, "自定义事件", Toast.LENGTH_SHORT).show();
        mNavCtrl.setInnerColor(0x20FF0000);
        mNavCtrl.setBackGroundColor(0x80000000);
        mNavCtrl.setOuterColor(0x400000FF);
        mIsLocked = !mIsLocked;
        mNavCtrl.setIsLocked(mIsLocked);
    }

    @Override
    public void onNavMoving(int i, int i1) {
        Log.e("TaG", "ra1d:" + i + " Len:" + i1);
        mtvDisplay.setText("ra1d:" + i + " Len:" + i1);
    }
}