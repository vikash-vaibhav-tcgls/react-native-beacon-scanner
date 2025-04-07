import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-beacon-scanner' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const BeaconScanner = NativeModules.BeaconScanner
  ? NativeModules.BeaconScanner
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export enum ScanEventType {
  STARTED = 'started',
  STOPPED = 'stopped',
  NO_BLUETOOTH_ENABLED = 'no_bluetooth_enabled',
  NO_LOCATION_ENABLED = 'no_location_enabled',
  NO_BLUETOOTH_PERMISSION = 'no_bluetooth_permission',
  NO_LOCATION_PERMISSION = 'no_location_permission',
  FOUND_EDDYSTONE = 'found_eddystone',
}

export type FoundBeacon = {
  event: ScanEventType.FOUND_EDDYSTONE;
  data: {
    name: string;
    uuid: string;
    eddystoneUrls: Array<string>;
  };
};

export enum ScanMode {
  LOW_POWER = 0,
  BALANCED = 1,
  LOW_LATENCY = 2,
  OPPORTUNISTIC = -1,
}

export enum PhyMode {
  LE_1M = 1,
  LE_2M = 2,
  LE_CODED = 3,
  LE_ALL_SUPPORTED = 255,
}

export enum ServiceUUIDs {
  EDDY_STONE_UUID = '0000FEAA-0000-1000-8000-00805F9B34FB',
}

export type BeaconFilters = {
  serviceUuid?: ServiceUUIDs;
  deviceName?: string;
};
export interface ScanOptions {
  scanMode?: ScanMode; // Default: LOW_POWER
  isLegacy?: boolean; // Default: false
  phy?: PhyMode; // Default: LE_ALL_SUPPORTED
  filters: Array<BeaconFilters>;
}

export function startScanning(options: ScanOptions) {
  return BeaconScanner.startScanning(options);
}

export function stopScanning() {
  return BeaconScanner.stopScanning();
}

export function enableLocation(): Promise<boolean> {
  return BeaconScanner.enableLocation();
}

export function enableBluetooth(): Promise<boolean> {
  return BeaconScanner.enableBluetooth();
}

export function isBluetoothEnabled(): Promise<boolean> {
  return BeaconScanner.isBluetoothEnabled();
}
export function isLocationEnabled(): Promise<boolean> {
  return BeaconScanner.isLocationEnabled();
}
