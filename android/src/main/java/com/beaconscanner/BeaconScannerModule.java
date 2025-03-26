package com.beaconscanner;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

class BeaconScannerModule extends ReactContextBaseJavaModule{
  final String NAME = "BeaconScanner";
  BeaconScannerModule(ReactApplicationContext reactContext){
    super(reactContext);
  }

@NonNull
@Override
public String getName() {
    return NAME;
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  void multiply(Double a, Double b , Promise promise) {
    promise.resolve(a * b +90);
  }

}
