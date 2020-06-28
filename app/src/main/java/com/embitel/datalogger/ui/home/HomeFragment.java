package com.embitel.datalogger.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.embitel.datalogger.App;
import com.embitel.datalogger.R;
import com.embitel.datalogger.base.AppContainer;
import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.data.BleDevice;

import com.embitel.datalogger.bleutils.BLeConstants;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.bleutils.SharedPreferenceConstant;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.bleutils.Utils;
import com.embitel.datalogger.databinding.FragmentHomeBinding;
import com.embitel.datalogger.model.WeatherResponse;
import com.embitel.datalogger.ui.configuration.DeviceListConfigurationScreen;
import com.embitel.datalogger.ui.location.LocationUpdatesService;
import com.embitel.datalogger.ui.location.MainActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private TextView txtStatus, txtTemp, txtCloud, textValBattery;
    private ImageView imgStatus;
    private BleDevice mBleDevice;
    private MyReceiver myReceiver;
    private StatusListener statusListener;
    AppContainer appContainer = App.getInstance().appContainer;
    private FragmentHomeBinding fragmentHomeBinding;
    private String temperature="";
    private String weather="";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
                container, false);
        myReceiver = new MyReceiver();
        statusListener = new StatusListener();
        appContainer = App.getInstance().appContainer;

        homeViewModel = appContainer.homeViewModelFactory.create();
        homeViewModel.getWeatherData().observe(this, this::getWeatherData);
        homeViewModel.getData().observe(this, this::updateWeather);
        homeViewModel.getError().observe(this, this::errorResult);
        fragmentHomeBinding.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DeviceListConfigurationScreen.class));
                getActivity().finish();
            }
        });
        batteryLevel();
        return fragmentHomeBinding.getRoot();
    }

    private void errorResult(String s) {
        if (!BleManager.getInstance().isConnected(mBleDevice)) {

            fragmentHomeBinding.txtStatus.setText(BLeConstants.DISCONNECT);
        }
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

    }

    private void updateWeather(String s) {
        Log.d(TAG, "updateWeather: " + s);
        //Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
    }

    private void getWeatherData(WeatherResponse weatherResponse) {

        temperature=String.format("%s%sC", String.valueOf(weatherResponse.getMain().getTemp()), (char) 0x00B0);
        weather=String.format("%s: %s", weatherResponse.getWeather().get(0).getMain(), weatherResponse.getWeather().get(0).getDescription());
        fragmentHomeBinding.txTemp.setText(temperature);
        fragmentHomeBinding.txtCloud.setText(weather);
        SharedPreferencesManager.setPreference(SharedPreferenceConstant.TEMP,temperature);
        SharedPreferencesManager.setPreference(SharedPreferenceConstant.WEATHER,weather);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("w", weatherResponse.getWeather().get(0).getDescription());
            jsonObject.put("T", String.valueOf(weatherResponse.getMain().getTemp()));

            homeViewModel.updateSettingsConfiguration(jsonObject.toString(), Constants.ACTION_SEND_WEATHER_UPDATE,
                    SampleGattAttributes.CURRENT_TIME, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mBleDevice = ((MainActivity) getActivity()).getBleDevice();
        homeViewModel.getBleDevice(mBleDevice);
        Log.d(TAG, "onResume" + mBleDevice);
        if (BleManager.getInstance().isConnected(mBleDevice)) {
            fragmentHomeBinding.txtStatus.setText(BLeConstants.CONNECTED);
        }

        fragmentHomeBinding.txTemp.setText(SharedPreferencesManager.getTemperature());
        fragmentHomeBinding.txtCloud.setText(SharedPreferencesManager.getWeather());
        fragmentHomeBinding.textView6.setText(String.format("%sKMPH", SharedPreferencesManager.getSpeed()));
        fragmentHomeBinding.swGps.setChecked(SharedPreferencesManager.getGPSValue());
        fragmentHomeBinding.swDnd.setChecked(SharedPreferencesManager.getDNDValue());


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(statusListener,
                new IntentFilter(Constants.ACTION_SENT_CONNECTION_STATUS));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(statusListener);
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                try {
                    homeViewModel.fetchData(Utils.getCityName(location.getLatitude(), location.getLongitude(), getActivity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Computes the battery level by registering a receiver to the intent triggered
     * by a battery status/level change.
     */
    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                fragmentHomeBinding.txtValBtry.setText(level + "%");
/*
                homeViewModel.updateSettingsConfiguration(String.valueOf(level),
                        Constants.ACTION_SEND_BATTERY_LEVEL, SampleGattAttributes.CURRENT_TIME,false
                        );
*/
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(batteryLevelReceiver, batteryLevelFilter);

    }

    private class StatusListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(Constants.SEND_STATUS);
            fragmentHomeBinding.txtStatus.setText(data);
        }

    }
}

