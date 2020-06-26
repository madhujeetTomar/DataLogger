package com.embitel.datalogger.ui.location;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.embitel.datalogger.R;
import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.callback.BleGattCallback;
import com.embitel.datalogger.blemodule.callback.BleWriteCallback;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.blemodule.exception.BleException;
import com.embitel.datalogger.bleutils.BLeConstants;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.bleutils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    public static final String EXTRAS_DEVICE_DATA ="device" ;
    private static final String TAG = "MainActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private BleDevice bleDevice;
    private BluetoothAdapter mBluetoothAdapter;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    LocalBroadcastManager localBroadcastManager;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mService.requestLocationUpdates();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.checkLocationPermission(this);
        Log.d(TAG, "onCreate: ");

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
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }

    private void initData() {
        if(!SharedPreferencesManager.getBLeDevice().isEmpty())
        {
         bleDevice= new Gson().fromJson(SharedPreferencesManager.getBLeDevice(), BleDevice.class);
         if(!BleManager.getInstance().isConnected(bleDevice))
         {
             connect(bleDevice);
         }
         else
         {
             sendBluetoothStatus(BLeConstants.CONNECTED);
             bindService(new Intent(MainActivity.this, LocationUpdatesService.class), mServiceConnection,
                     Context.BIND_AUTO_CREATE); }
        }
        else
        {

        }
    }
    public BleDevice getBleDevice() {

        return bleDevice;
    }
    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this fragment is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    void sendBluetoothStatus(String status)
    {
        Intent localIntent = new Intent(Constants.ACTION_SENT_CONNECTION_STATUS);
// Send local broadcast
        localIntent.putExtra(Constants.SEND_STATUS,status);
        localBroadcastManager.sendBroadcast(localIntent);

    }

    void connect(BleDevice device) {
        BleManager.getInstance().connect(device, new BleGattCallback() {
            @Override
            public void onStartConnect() {
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
              //  mMessage.postValue("Connection Failed!!!");
                Toast.makeText(MainActivity.this,exception.getDescription(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                sendBluetoothStatus(BLeConstants.CONNECTED);
                bindService(new Intent(MainActivity.this, LocationUpdatesService.class), mServiceConnection,
                        Context.BIND_AUTO_CREATE);


            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                //isLoading.postValue(false);
                //mMessage.postValue("Connection Disconnect!!!");
                Toast.makeText(MainActivity.this,"Connection Disconnect!!!",Toast.LENGTH_SHORT).show();

            }
        });
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

}
