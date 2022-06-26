package com.hec.bluetooth.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hec.bluetooth.CallBack.BlueToothInterface;
import com.hec.bluetooth.R;
import com.hec.bluetooth.Receiver.BluetoothStateBroadcastReceive;
import com.hec.bluetooth.Util.BluetoothUtil;
import com.hec.bluetooth.Util.PermissionUtil;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private BluetoothUtil bluetoothUtil;
    private BluetoothStateBroadcastReceive broadcastReceive;
    private CardView cvBtSwitch;
    private TextView tvBt;
    private CardView cvBtSetting;
    private CardView cvBtQxSetting;
    private static final int COLOR_OPEN = Color.parseColor("#659FFF");
    private static final int COLOR_CLOSE = Color.parseColor("#246EE9");
    private static final String VALUE_OPEN = "bluetooth is turned on\nClick to close";
    private static final String VALUE_CLOSE = "Bluetooth is not turned on\nClick to open";
    private BlueToothInterface blueToothInterface = new BlueToothInterface() {

        @Override
        public void getBlueToothDevices(BluetoothDevice device) {

        }

        @Override
        public void getConnectedBlueToothDevices(BluetoothDevice device) {

        }

        @Override
        public void getDisConnectedBlueToothDevices(BluetoothDevice device) {

        }

        @Override
        public void searchFinish() {

        }

        @Override
        public void open() {
            cvBtSwitch.setCardBackgroundColor(COLOR_OPEN);
            tvBt.setText(VALUE_OPEN);
        }

        @Override
        public void disable() {
            cvBtSwitch.setCardBackgroundColor(COLOR_CLOSE);
            tvBt.setText(VALUE_CLOSE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bluetoothUtil = new BluetoothUtil(getContext());
        cvBtSwitch = view.findViewById(R.id.cv_bt_switch);
        cvBtSwitch.setOnClickListener(this);
        tvBt = view.findViewById(R.id.tv_bt);
        cvBtSetting = view.findViewById(R.id.cv_bt_setting);
        cvBtSetting.setOnClickListener(this);
        cvBtQxSetting = view.findViewById(R.id.cv_bt_qx_setting);
        cvBtQxSetting.setOnClickListener(this);
        initBtSwitch();
        registerBluetoothReceiver();
    }

    private void initBtSwitch() {
        if (bluetoothUtil.isBluetoothEnable()) {
            cvBtSwitch.setCardBackgroundColor(COLOR_OPEN);
            tvBt.setText(VALUE_OPEN);
        } else {
            cvBtSwitch.setCardBackgroundColor(COLOR_CLOSE);
            tvBt.setText(VALUE_CLOSE);
        }
    }

    private void registerBluetoothReceiver() {
        Log.i("hec", "\n" +
                "Bluetooth broadcast monitoring starts");
        if (broadcastReceive == null) {
            broadcastReceive = new BluetoothStateBroadcastReceive(blueToothInterface);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        getContext().registerReceiver(broadcastReceive, intentFilter);
    }

    private void unregisterBluetoothReceiver() {
        Log.i("hec", "Bluetooth broadcast monitoring is off");
        if (broadcastReceive != null) {
            getContext().unregisterReceiver(broadcastReceive);
            broadcastReceive = null;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_bt_switch:
                if (bluetoothUtil.isBluetoothEnable() && tvBt.getText().equals(VALUE_OPEN))
                    bluetoothUtil.disableBluetooth();
                else
                    bluetoothUtil.openBluetooth();
                break;
            case R.id.cv_bt_setting:
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                break;
            case R.id.cv_bt_qx_setting:
                new PermissionUtil(getContext()).open();
                break;
        }
    }

    @Override
    public void onDestroy() {
        Log.i("hec-onDestroy", "SettingClose");
        super.onDestroy();
        unregisterBluetoothReceiver();
        bluetoothUtil.close();
    }
}
