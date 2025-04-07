package com.beaconscanner;

import static com.beaconscanner.Utils.LOG_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeaconScannerImplementation implements ActivityEventListener {
  private ReactApplicationContext reactContext;
  private final BluetoothAdapter bluetoothAdapter;
  private BluetoothLeScanner bluetoothLeScanner;
  private Promise _promise = null;
  private static Set<String> eddystoneScannedBeacon = new HashSet<>();

  BeaconScannerImplementation(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
    this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
  }

  boolean isBluetoothEnabled() {
    return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
  }


  @SuppressLint("MissingPermission")
  void startScanForBeacons(ReadableMap scanOptions) {
    if (!Utils.hasBluetoothPermission(this.reactContext.getCurrentActivity())) {
      Log.e(LOG_TAG, "No bluetooth permission");
      this.sendEvent(Utils.SCAN_EVENTS.NO_BLUETOOTH_PERMISSION.toString(), "Bluetooth permission required");
      return;
    }

    if (!isBluetoothEnabled()) {
      Log.e(LOG_TAG, "Bluetooth not enabled");
      this.sendEvent(Utils.SCAN_EVENTS.NO_BLUETOOTH_ENABLED.toString(), "Bluetooth not enabled");
      return;
    }

    if (!Utils.hasLocationPermission(this.reactContext.getCurrentActivity())) {
      Log.e(LOG_TAG, "Location permission required");
      this.sendEvent(Utils.SCAN_EVENTS.NO_LOCATION_PERMISSION.toString(), "Location permission required");
      return;
    }

    if (!Utils.isLocationEnabled(this.reactContext.getCurrentActivity())) {
      Log.e(LOG_TAG, "Location not enabled");
      this.sendEvent(Utils.SCAN_EVENTS.NO_LOCATION_ENABLED.toString(), "Location not enabled");
      return;
    }

    bluetoothLeScanner.stopScan(bluetoothScanCallback);
    bluetoothLeScanner.startScan(Utils.createScanFilters(scanOptions), Utils.createScanSettings(scanOptions), bluetoothScanCallback);
    this.sendEvent(Utils.SCAN_EVENTS.STARTED.toString(), "Scanning started");
  }


  @SuppressLint("MissingPermission")
  void stopScanForBeacon() {
    if (bluetoothLeScanner == null) {
      Log.e(LOG_TAG, "Scanning not initiated yet");
      // not initiated yet
      return;
    }
    bluetoothLeScanner.stopScan(bluetoothScanCallback);
    eddystoneScannedBeacon.clear();
    sendEvent(Utils.SCAN_EVENTS.STOPPED.toString(), "Scanning stop");

  }


  private final ScanCallback bluetoothScanCallback = new ScanCallback() {
    @Override
    public void onBatchScanResults(List<ScanResult> results) {
      super.onBatchScanResults(results);
      Log.d(LOG_TAG, "batch scan result");
    }

    @Override
    public void onScanFailed(int errorCode) {
      super.onScanFailed(errorCode);
      Log.d(LOG_TAG, "scanning failed" + errorCode);
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && result.getDataStatus() == ScanResult.DATA_TRUNCATED) {
        Log.d(LOG_TAG, "Getting truncated data");
        return;
      }
      ScanRecord scanRecord = result.getScanRecord();
      if (scanRecord != null) {
        Map<ParcelUuid, byte[]> serviceData = scanRecord.getServiceData();
        if (serviceData == null || serviceData.isEmpty()) {
          Log.d(LOG_TAG, "No service data found.");
          return;
        }
        if(eddystoneScannedBeacon.contains(scanRecord.getDeviceName())){
          return;
        }

        WritableMap dataToSendTojs = Arguments.createMap();
        dataToSendTojs.putString("name", scanRecord.getDeviceName());

        for (ParcelUuid uuid : serviceData.keySet()) {
          byte[] beaconData = serviceData.get(uuid);
          if (beaconData == null) {
            Log.d(LOG_TAG, "Bytes not found");
            return;
          }
          dataToSendTojs.putString("uuid", uuid.toString());
          int beaconFrameType = beaconData[0] & 0xFF;
          switch (beaconFrameType) {
            case 0x10:
              List<String> urls = Utils.extractEddystoneUrlsFromBluetoothPacket(scanRecord.getBytes());
              if (!urls.isEmpty()) {
                eddystoneScannedBeacon.add(scanRecord.getDeviceName());
                dataToSendTojs.putArray("eddystoneUrls", Utils.convertListToReadableArray(urls));
                sendEvent( Utils.SCAN_EVENTS.FOUND_EDDYSTONE.toString(),dataToSendTojs);
              } else {
                Log.d(LOG_TAG, "No valid url found from bytes");
              }
              break;
            default:
              Log.d(LOG_TAG, "Unknown beacon frame found");
          }
        }
      }
    }
  };


  private void sendEvent(String eventName, String message) {
    if (this.reactContext == null) {
      Log.w(LOG_TAG, "Could not found react-context to send events");
      return;
    }
    WritableMap data = Arguments.createMap();
    data.putString("data", message);
    data.putString("event", eventName);
    if (this.reactContext.hasActiveCatalystInstance()) {
      reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(Utils.BEACON_SUBSCRIBE_EVENT_NAME, data);
    }
  }


  private void sendEvent(String eventName,
                         WritableMap dataToSend) {
    if (this.reactContext == null) {
      Log.w(LOG_TAG, "Could not found react-context to send events");
      return;
    }
    WritableMap data = Arguments.createMap();
    data.putMap("data", dataToSend);
    data.putString("event", eventName);
    if (this.reactContext.hasActiveCatalystInstance()) {
      reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(Utils.BEACON_SUBSCRIBE_EVENT_NAME, data);
    }
  }

  void resolvePromise(Object data) {
    if (_promise != null)
      _promise.resolve(data);
    this._promise = null;
  }

  void rejectPromise(Promise promise, String message) {
    if (_promise != null)
      _promise.reject("error", message);
    this._promise = null;
  }

  void assignPromise(Promise promise) {
    this._promise = promise;
  }


  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent intent) {
    Log.e(LOG_TAG, String.valueOf(requestCode));
    switch (requestCode) {
      case Utils.ENABLE_BLUETOOTH_CODE:
        this.resolvePromise(isBluetoothEnabled());
        break;

      case Utils.ENABLE_LOCATION_CODE:
        this.resolvePromise(Utils.isLocationEnabled(this.reactContext.getCurrentActivity()));
        break;

      default:
        Log.e(LOG_TAG, "Unknown requestCode: " + requestCode);
        break;
    }
  }

  @Override
  public void onNewIntent(Intent intent) {

  }
}
