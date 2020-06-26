package com.embitel.datalogger.extra;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import com.embitel.datalogger.bleutils.BLeConstants;
import com.embitel.datalogger.bleutils.SampleGattAttributes;

import java.nio.ByteBuffer;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BluetoothGattClient {

    private OnCounterReadListener mListener;

    public interface OnCounterReadListener {
        void onGettingStatus(String value, UUID config);

        void onConnected(boolean success);
    }

    private static final String TAG = "BluetoothGattClient";
    private Context mContext;



    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mDeviceAddress;
    private UUID mConfig;
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT client. Attempting to start service discovery");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT client");
               // mListener.onConnected(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                boolean connected = false;

                BluetoothGattService service = gatt.getService(UUID.fromString(SampleGattAttributes.TIME_SERVICE));
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(SampleGattAttributes.CURRENT_TIME));
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true);

                        BluetoothGattDescriptor descriptor =characteristic.getDescriptor(
                               SampleGattAttributes.CLIENT_CONFIG);;
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            connected = gatt.writeDescriptor(descriptor);
                        }
                    }
                }
                mListener.onConnected(connected);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            readCounterCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            readCounterCharacteristic(characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (SampleGattAttributes.CLIENT_CONFIG.equals(descriptor.getUuid())) {
                BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString(SampleGattAttributes.TIME_SERVICE)).getCharacteristic(UUID.fromString(SampleGattAttributes.CURRENT_TIME));
                gatt.readCharacteristic(characteristic);
            }
        }

        private void readCounterCharacteristic(BluetoothGattCharacteristic characteristic) {
            if (SampleGattAttributes.CURRENT_TIME.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                ByteBuffer bb = ByteBuffer.wrap(data);
/*
                int first = bb.getShort(); //pull off a 16 bit short (1, 5)
                int second = bb.get(); //pull off the next byte (5)
                int third = bb.getInt();
                String value = first + " " + second + " " + third;
*/
                mListener.onGettingStatus("hello", mConfig);
            }
        }
    };

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startClient();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopClient();
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    };

    public void onCreate(Context context, String deviceAddress, UUID config, OnCounterReadListener listener) throws RuntimeException {
        mContext = context;
        mListener = listener;
        mDeviceAddress = deviceAddress;
        mConfig=config;

        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (!checkBluetoothSupport(mBluetoothAdapter)) {
            throw new RuntimeException("GATT client requires Bluetooth support");
        }

        // Register for system Bluetooth events
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceiver, filter);
        if (!mBluetoothAdapter.isEnabled()) {
            Log.w(TAG, "Bluetooth is currently disabled... enabling");
            mBluetoothAdapter.enable();
        } else {
            Log.i(TAG, "Bluetooth enabled... starting client");
            startClient();
        }
    }

    public void onDestroy() {
        mListener = null;
        mContext.unregisterReceiver(mBluetoothReceiver);

        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopClient();
        }

        mContext.unregisterReceiver(mBluetoothReceiver);
    }
    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }

    private void startClient() {
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mGattCallback);

        if (mBluetoothGatt == null) {
            Log.w(TAG, "Unable to create GATT client");
            return;
        }
    }

    private void stopClient() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null;
        }
    }
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled,
                                              String config) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (BLeConstants.SERVICE_1.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(config));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public boolean writeInteractor(String value, UUID uuid) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = mBluetoothGatt
                .getService(UUID.fromString(SampleGattAttributes.TIME_SERVICE))
                .getCharacteristic(uuid);
        bluetoothGattCharacteristic.setValue(value);
        return mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }



}


