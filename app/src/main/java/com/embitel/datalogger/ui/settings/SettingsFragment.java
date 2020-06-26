package com.embitel.datalogger.ui.settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.bleutils.SharedPreferenceConstant;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.ui.location.MainActivity;
import com.embitel.datalogger.R;
import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.databinding.FragmentDashboardBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SettingsFragment";
    private FragmentDashboardBinding fragmentDashboardBinding;
    private SettingViewModel dashboardViewModel;
    private BleDevice bleDevice;
    private static final String CHANNEL_ID = "channel_01";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(!SharedPreferencesManager.getBLeDevice().isEmpty())
        {
            bleDevice = new Gson().fromJson(SharedPreferencesManager.getBLeDevice(), BleDevice.class);
        }

        dashboardViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        fragmentDashboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard,
                container, false);
        fragmentDashboardBinding.setModel(dashboardViewModel);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        fragmentDashboardBinding.switch1.setChecked(SharedPreferencesManager.getGPSValue());
        fragmentDashboardBinding.switch2.setChecked(SharedPreferencesManager.getDNDValue());
        dashboardViewModel.getBleDevice(bleDevice);
        dashboardViewModel.sendDataSuccessfully().observe(this, this::onSuccessfullyDataUpdate);
        dashboardViewModel.getErrorResult().observe(this, this::getError);
        dashboardViewModel.getExceedSpeed().observe(getViewLifecycleOwner(), this::exceedLimit);
        dashboardViewModel.getMinSpeed().observe(getViewLifecycleOwner(), this::minSpeed);
        // Spinner click listener
        fragmentDashboardBinding.spinnerApiCallRetry.setOnItemSelectedListener(this);
        fragmentDashboardBinding.spinnerBleCallRetry.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<Integer> retries = new ArrayList<>();
        retries.add(1);
        retries.add(2);
        retries.add(3);
        retries.add(4);
        retries.add(5);
        // Creating adapter for spinner
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_selected, retries);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        fragmentDashboardBinding.spinnerApiCallRetry.setAdapter(adapter);
        fragmentDashboardBinding.spinnerBleCallRetry.setAdapter(adapter);
        return fragmentDashboardBinding.getRoot();
    }

    private void minSpeed(String s) {
        fragmentDashboardBinding.etSpeed.setText(s);
    }

    private void exceedLimit(String s) {
        //Send Notification
        getNotification("Your speed:" +s+" exceeds the speed limit");
    }

    private void getError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void onSuccessfullyDataUpdate(String data) {
        Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        int item = Integer.parseInt(parent.getItemAtPosition(position).toString());

        switch (parent.getId()) {
            case R.id.spinner_api_call_retry:
                SharedPreferencesManager.setPreference(SharedPreferenceConstant.RETRY_API_CALL, item);
                break;
            case R.id.spinner_ble_call_retry:
                SharedPreferencesManager.setPreference(SharedPreferenceConstant.RETRY_BLE_CONNECTION, item);
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void getNotification(String text) {

      /*  // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity())
                .setContentText(text)
                .setContentTitle("HUD Connect")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        dashboardViewModel.readData(SampleGattAttributes.CURRENT_TIME);
    }
}
