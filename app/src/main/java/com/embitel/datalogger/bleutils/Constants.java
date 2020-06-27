package com.embitel.datalogger.bleutils;

import com.embitel.datalogger.blemodule.data.BleDevice;

public class Constants {
    //Weather Api APP ID
    public static final String APP_ID="7f6c6d11ba08c24b83a7920bd4cc612c";
    //Response  msg
    public static final String GPS_DATA_UPDATE_SUCCESSFULLY = "GPS value updated successfully";
    public static final String TIME_UPDATE_SUCCESSFULLY = "Time sync successfully";
    public static final String DND_DATA_UPDATE_SUCCESSFULLY = "DND value updated successfully";
    public static final String PHONE_NUMBER_DATA_SUCCESSFULLY = "Phone number updated successfully";
    public static final String NAME_DATA_UPDATE_SUCCESSFULLY = "Name updated successfully";
    public static final String SPEED_DATA_UPDATE_SUCCESSFULLY = "Speed value updated successfully";
    //Actions
    public static final String ACTION_SEND_PHONE_NUMBER = "action_send_number";
    public static final String ACTION_SEND_DND_DATA = "action_send_dnd_data";
    public static final String ACTION_SEND_GPS_DATA = "action_send_gps_data";
    public static final String ACTION_SEND_SPEED_VALUE ="action_send_speed_value";
    public static final String ACTION_SEND_NAME ="action_send_name_value";
    public static final String ACTION_SEND_TIME ="action_send_time" ;

    public static final String BLUETOOTH_IS_NOT_ENABLED = "Bluetooth is not enabled";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;

    public static final String ACTION_SENT_CONNECTION_STATUS ="action_send_status";
    public static final String SEND_STATUS ="ble_connection_status";
    public static final String ACTION_SEND_WEATHER_UPDATE = "action_send_weather_update";
    public static final String ACTION_SEND_BATTERY_LEVEL = "action_send_battery_level";
    public static BleDevice bleDevice;
}
