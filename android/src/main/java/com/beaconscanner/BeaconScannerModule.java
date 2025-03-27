package com.beaconscanner;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

class BeaconScannerModule extends ReactContextBaseJavaModule{
  final String NAME = "BeaconScanner";
  final private BeaconScannerImplementation beaconScannerImplementation;
  final private ReactApplicationContext reactContext;
  BeaconScannerModule(ReactApplicationContext reactContext){
    super(reactContext);
    this.beaconScannerImplementation = new BeaconScannerImplementation(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(beaconScannerImplementation);
  }

@NonNull
@Override
public String getName() {
    return NAME;
  }

  @ReactMethod
  void startScanning(ReadableMap scanOptions) {
      this.beaconScannerImplementation.startScanForBeacons(scanOptions);

  }

  @ReactMethod
  void stopScanning(){
    this.beaconScannerImplementation.stopScanForBeacon();
  }

  @ReactMethod
  void enableLocation(Promise promise){
    this.beaconScannerImplementation.assignPromise(promise);
    Utils.enableLocation(this.reactContext.getCurrentActivity());
  }

  @ReactMethod
  void enableBluetooth(Promise promise){
    this.beaconScannerImplementation.assignPromise(promise);
    Utils.enableBluetooth(this.reactContext.getCurrentActivity());
  }

  @ReactMethod
  void isBluetoothEnabled(Promise promise){
    promise.resolve(this.beaconScannerImplementation.isBluetoothEnabled());
  }

  @ReactMethod
  void isLocationEnabled(Promise promise){
    promise.resolve(Utils.isLocationEnabled(this.reactContext.getCurrentActivity()));
  }

}
