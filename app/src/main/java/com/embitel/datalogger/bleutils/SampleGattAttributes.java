package com.embitel.datalogger.bleutils;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    public static final String CURRENT_TIME_READ = "0000fff3-0000-1000-8000-00805f9b34fb";
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    /* Current Time Service UUID */
    public static String TIME_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    /* Mandatory Current Time Information Characteristic */
    public static String CURRENT_TIME    = "0000fff4-0000-1000-8000-00805f9b34fb";
    /* Optional Local Time Information Characteristic */
    public static UUID LOCAL_TIME_INFO = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb");
    /* Mandatory Client Characteristic Config Descriptor */
    public static UUID CLIENT_CONFIG = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");

    public static String READ_SERVICE = "0000fffa-0000-1000-8000-00805f9b34fb";
    /* Mandatory Current Time Information Characteristic */
    public static String READ_TIME    = "22fe5d5c-77f3-11ea-bc55-0242ac137210";


    public static String BLE_SERVICE_1="22fe5d5c-77f3-11ea-bc55-0242ac137100";
    public static String BLE_SERVICE_2="22fe5d5c-77f3-11ea-bc55-0242ac137200";



    public final static String CLIENT_CHARACTERISTIC_CONFIG_1 = "22fe5d5c-77f3-11ea-bc55-0242ac137110";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_2 = "22fe5d5c-77f3-11ea-bc55-0242ac137120";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_3 = "22fe5d5c-77f3-11ea-bc55-0242ac137130";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_4 = "22fe5d5c-77f3-11ea-bc55-0242ac137140";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_5 = "22fe5d5c-77f3-11ea-bc55-0242ac137150";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_6 = "22fe5d5c-77f3-11ea-bc55-0242ac137160";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_7 = "22fe5d5c-77f3-11ea-bc55-0242ac137170";
    public final static String CLIENT_CHARACTERISTIC_CONFIG_8 = "22fe5d5c-77f3-11ea-bc55-0242ac137180";




    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
