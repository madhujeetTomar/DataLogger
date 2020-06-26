package com.embitel.datalogger.ui.configuration;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.embitel.datalogger.base.BaseActivity;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.bleutils.SharedPreferenceConstant;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.databinding.ActivityDeviceListConfigurationScreenBinding;
import com.embitel.datalogger.ui.location.MainActivity;
import com.embitel.datalogger.R;
import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.bleutils.Utils;
import com.embitel.datalogger.ui.adapter.DeviceLeAdapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeviceListConfigurationScreen extends
        BaseActivity<ActivityDeviceListConfigurationScreenBinding>
        implements DeviceLeAdapter.onDevicesItemClick, DeviceConfigurationNavigator {

    private DeviceLeAdapter deviceLeAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ActivityDeviceListConfigurationScreenBinding mDataBinding;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private DeviceListConfigurationViewModel deviceListConfigurationViewModel;
    private BleDevice mBleDevice, mConfiguredDevice;
    private List<BleDevice> bleDeviceList=new ArrayList<>();

    @Override
    public int getBindingVariable() {
        return BR.model;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_list_configuration_screen;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = getViewDataBinding();
        deviceListConfigurationViewModel = ViewModelProviders.of(this).get(DeviceListConfigurationViewModel.class);

        Utils.checkLocationPermission(this);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        deviceLeAdapter = new DeviceLeAdapter(this);
        mDataBinding.btnConnect.setOnClickListener(v -> {
            deviceListConfigurationViewModel.connect(mBleDevice);
        });
        mDataBinding.btnScan.setOnClickListener(v -> {
            if (mDataBinding.btnScan.getText().equals(getString(R.string.start_scan))) {
                deviceListConfigurationViewModel.startScan();
            } else if (mDataBinding.btnScan.getText().equals(getString(R.string.stop_scan))) {
                BleManager.getInstance().cancelScan();
            }
        });

        //observing response
        deviceListConfigurationViewModel.scBLeDevices().observe(this, this::scannedBLeDevices);
        deviceListConfigurationViewModel.btnStatus().observe(this, this::scanStatus);
        deviceListConfigurationViewModel.setIsLoading().observe(this, this::setLoading);
        deviceListConfigurationViewModel.getResponse().observe(this, this::getResponse);
        deviceListConfigurationViewModel.getConfiguredDevice().observe(this, this::getConfiguredDevice);
   deviceListConfigurationViewModel.sendDataSuccessfully().observe(this,this::timeUpdateSuccessfully);
        deviceListConfigurationViewModel.getErrorResult().observe(this,this::onError);
    }

    private void onError(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void timeUpdateSuccessfully(String s) {
        SharedPreferencesManager.setPreference(SharedPreferenceConstant.BLE_DEVICE, new Gson().toJson(mBleDevice));
        //send Time data
        final Intent intent = new Intent(DeviceListConfigurationScreen.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRAS_DEVICE_DATA, mBleDevice);
        startActivity(intent);
        finish();
    }

    private void getConfiguredDevice(BleDevice bleDevice) {
        mConfiguredDevice=bleDevice;
    }

    private void getResponse(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        if (s.equals("Connection established successfully")) {

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            int second = now.get(Calendar.SECOND);

            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("h",hour);
                jsonObject.put("m",minute);
                jsonObject.put("s",second);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            deviceListConfigurationViewModel.updateSettingsConfiguration(jsonObject.toString(), Constants.ACTION_SEND_TIME,
                 SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_7,mBleDevice
         );
        }
    }

    private void setLoading(Boolean aBoolean) {
        if (aBoolean) {
            showLoading();
        } else {
            hideLoading();
        }
    }

    private void scanStatus(String s)
    {
        mDataBinding.btnScan.setText(s);
//AutoConnect
        if(s.equals("Start Scan") && mConfiguredDevice!=null)
        {
           mBleDevice=mConfiguredDevice;
            deviceListConfigurationViewModel.connect(mConfiguredDevice);
        }
    }

    private void scannedBLeDevices(BleDevice bleDevice) {
        bleDeviceList.add(bleDevice);
        deviceLeAdapter.addDevice(bleDevice);
        deviceLeAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPEN_GPS) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        deviceLeAdapter = new DeviceLeAdapter(this);
        deviceLeAdapter.setOnDevicesItemClick(this);
        mDataBinding.recyScan.setLayoutManager(new LinearLayoutManager(this));
        mDataBinding.recyScan.setAdapter(deviceLeAdapter);
    }


    private long getTime()
    {
        SimpleDateFormat datesformet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTime = System.currentTimeMillis();
        String currentdate = datesformet.format(currentTime);
        long diff = 0;
        //Setting dates
        try {
            Date date = datesformet.parse(currentdate);

        
             diff = Math.abs(date.getTime());
            



        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return diff;
    }

    @Override
    public void onDevicesItemClick(BleDevice device) {
        mBleDevice = device;
        deviceListConfigurationViewModel.connect(mBleDevice);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
