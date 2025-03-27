package com.beaconscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.location.LocationManager;
import java.util.UUID;
import android.os.ParcelUuid;



import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;

public class Utils {
  static final String LOG_TAG = "APP_BEACON_PARSER";
  static final String BEACON_SUBSCRIBE_EVENT_NAME = "beaconEvents";
  final static String EDDY_STONE_UUID = "0000FEAA-0000-1000-8000-00805F9B34FB";
  private static final String[] URL_PREFIXES = {
    "http://www.", "https://www.", "http://", "https://"
  };
  private static final String[] URL_SUFFIXES = {
    ".com/", ".org/", ".edu/", ".net/", ".info/", ".biz/", ".gov/",
    ".com", ".org", ".edu", ".net", ".info", ".biz", ".gov"
  };
  static final int ENABLE_BLUETOOTH_CODE = 100;
  static final int ENABLE_LOCATION_CODE = 101;

  public enum SCAN_EVENTS {
    STARTED("started"),
    STOPPED("stopped"),
    NO_BLUETOOTH_ENABLED("no_bluetooth_enabled"),
    NO_LOCATION_ENABLED("no_location_enabled"),
    NO_BLUETOOTH_PERMISSION("no_bluetooth_permission"),
    NO_LOCATION_PERMISSION("no_location_permission"),
    FOUND_EDDYSTONE("found_eddystone");

    private final String value;

    SCAN_EVENTS(String value) {
      this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
      return value;
    }
  }




  static String parseEddyStoneBytes(byte[] eddystoneBytes) {
    StringBuilder hexData = new StringBuilder();
    for (byte b : eddystoneBytes) {
      hexData.append(String.format("%02X ", b));
    }
    Log.d(LOG_TAG, "Service UUID: " + " Data: " + hexData.toString().trim());
    int prefixIndex = eddystoneBytes[2] & 0xFF;
    if (prefixIndex >= URL_PREFIXES.length) {
      Log.w(LOG_TAG, "Could not found prefix");
      return null;
    }
    StringBuilder url = new StringBuilder(URL_PREFIXES[prefixIndex]);

    for (int i = 3; i < eddystoneBytes.length - 1; i++) {
      url.append((char) eddystoneBytes[i]);
    }

    int suffixIndex = eddystoneBytes[eddystoneBytes.length - 1] & 0xFF;
    if (suffixIndex >= URL_SUFFIXES.length) {
      Log.w(LOG_TAG, "Could not found top level domain");
      return null;
    }
    url.append(URL_SUFFIXES[suffixIndex]);
    return url.toString();
  }


  static void enableLocation(Activity context) {
    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    context.startActivityForResult(intent, ENABLE_LOCATION_CODE, null);
  }

  @SuppressLint("MissingPermission")
  static void enableBluetooth(Activity context) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    context.startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_CODE, null);
  }

  static boolean hasLocationPermission(Activity context) {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
      ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  static boolean hasBluetoothPermission(Activity context) {

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    } else {
      return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
    }
  }

    static boolean isLocationEnabled(Activity context){
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    if(locationManager==null){
      Log.e(LOG_TAG, "Need location permission");
      return false;
    }
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  static List<ScanFilter> createScanFilters(ReadableMap scanOptions) {
    List<ScanFilter> filters = new ArrayList<>();

    if (scanOptions.hasKey("filters")) {
      ReadableArray jsFilters = scanOptions.getArray("filters");
      for (int i = 0; i < jsFilters.size(); i++) {
        ReadableMap filterObj = jsFilters.getMap(i);
        ScanFilter.Builder filterBuilder = new ScanFilter.Builder();

        if (filterObj.hasKey("deviceName")) {
          String deviceName = filterObj.getString("deviceName");
          filterBuilder.setDeviceName(deviceName);
        }

        if (filterObj.hasKey("serviceUuid")) {
          String uuidString = filterObj.getString("serviceUuid");
          filterBuilder.setServiceUuid(new ParcelUuid(UUID.fromString(uuidString)));
        }
        filters.add(filterBuilder.build());
      }
    }
    return filters;
  }

  static ScanSettings createScanSettings(ReadableMap scanOptions) {
    ScanSettings.Builder settingBuilder = new ScanSettings.Builder();

    if (scanOptions.hasKey("scanMode")) {
      int scanMode = scanOptions.getInt("scanMode");
      settingBuilder.setScanMode(scanMode);
    } else {
      settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (scanOptions.hasKey("isLegacy")) {
        settingBuilder.setLegacy(scanOptions.getBoolean("isLegacy"));
      } else {
        settingBuilder.setLegacy(false);
      }

      if (scanOptions.hasKey("phy")) {
        int phyMode = scanOptions.getInt("phy");
        settingBuilder.setPhy(phyMode);
      } else {
        settingBuilder.setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED);
      }
    }
    return settingBuilder.build();
  }



}
