# react-native-beacon-scanner

### Beacon Scanner

It scans for eddystone beacons extract urls, name form the bytes beacon emits.

## Installation

Not yet hosted on npm, you need to clone it and install it locally

### Run

#### Clone the project

```sh
git clone https://github.com/vikash-vaibhav-tcgls/react-native-beacon-scanner.git
```

#### Create build

```sh
# navigate to library directory
yarn install

yarn prepare
```

Now, delete node_modules folders in library directory

### Now, navigate to your project where you want to install the library

### Run

```sh
yarn add /username/your/directory/path-to-cloned-library
```

## Some setup

Add these permission to manifest file

```
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
  <uses-permission
    android:name="android.permission.BLUETOOTH_SCAN"
    tools:remove="android:usesPermissionFlags" />
  <uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

And done.

## Usage

Scanning beacons need location and bluetooth nearby devices permissions,
make sure app has permission and both bluetooth and gps location is on.

#### Scanning beacons

```javascript
import {
  startScanning,
  stopScanning,
  ScanMode,
  PhyMode,
  ServiceUUIDs
} from 'react-native-beacon-scanner';

 const options: ScanOptions = {
  scanMode: ScanMode.LOW_LATENCY,
  isLegacy: false,
  phy: PhyMode.LE_ALL_SUPPORTED,
  filters: [
    {
      serviceUuid: ServiceUUIDs.EDDY_STONE_UUID,
      deviceName: 'MyBeacon',
    },
  ],
};

startScanning(options);
```

### Get scanned beacons result

```javascript
import {stopScanning} from 'react-native-beacon-scanner';
import {DeviceEventEmitter} from 'react-native';

  useEffect(() => {
    const subscription = DeviceEventEmitter.addListener(
      'beaconEvents',
      ({ event, data }: { event: ScanEventType; data: string }) => {
        console.log('APP_BEACON_PARSER :js- Bluetooth Status:', event, data);
      }
    );

    return () => {
      stopScanning();
      subscription.remove(); // Clean up listener
    };
  }, []);
```

## 📡 Beacon Scanner API

### 📶 Scan Event Types

| Enum                                    | Description                      |
| --------------------------------------- | -------------------------------- |
| `ScanEventType.STARTED`                 | Scanning started                 |
| `ScanEventType.STOPPED`                 | Scanning stopped                 |
| `ScanEventType.NO_BLUETOOTH_ENABLED`    | Bluetooth is disabled            |
| `ScanEventType.NO_LOCATION_ENABLED`     | Location is disabled             |
| `ScanEventType.NO_BLUETOOTH_PERMISSION` | Bluetooth permission not granted |
| `ScanEventType.NO_LOCATION_PERMISSION`  | Location permission not granted  |
| `ScanEventType.FOUND_EDDYSTONE`         | Eddystone beacon found           |

---

### ⚙️ Scan Modes (`ScanMode`)

| Enum                     | Description          |
| ------------------------ | -------------------- |
| `ScanMode.LOW_POWER`     | Battery-saving mode  |
| `ScanMode.BALANCED`      | Balanced performance |
| `ScanMode.LOW_LATENCY`   | Fastest scan rate    |
| `ScanMode.OPPORTUNISTIC` | Opportunistic mode   |

---

### 📡 PHY Modes (`PhyMode`)

| Enum                       | Description                  |
| -------------------------- | ---------------------------- |
| `PhyMode.LE_1M`            | Standard 1M PHY              |
| `PhyMode.LE_2M`            | Higher-speed 2M PHY          |
| `PhyMode.LE_CODED`         | Long-range coded PHY         |
| `PhyMode.LE_ALL_SUPPORTED` | All supported PHYs (default) |

---

### 📘 Service UUIDs

| Enum                           | Description           |
| ------------------------------ | --------------------- |
| `ServiceUUIDs.EDDY_STONE_UUID` | Eddystone beacon UUID |

---

### 🔍 Beacon Filters

| Property      | Type            | Description                     |
| ------------- | --------------- | ------------------------------- |
| `serviceUuid` | `ServiceUUIDs?` | Filter by specific service UUID |
| `deviceName`  | `string?`       | Filter by beacon name           |

---

### ⚙️ Scan Options

| Property   | Type              | Default            | Description                          |
| ---------- | ----------------- | ------------------ | ------------------------------------ |
| `scanMode` | `ScanMode?`       | `LOW_LATENCY`      | Scan performance mode                |
| `isLegacy` | `boolean?`        | `false`            | Enable legacy mode for older devices |
| `phy`      | `PhyMode?`        | `LE_ALL_SUPPORTED` | Bluetooth PHY mode                   |
| `filters`  | `BeaconFilters[]? | []`                | List of filtering rules              |

---

### 🧠 API Functions

| Function             | Signature                        | Description                             |
| -------------------- | -------------------------------- | --------------------------------------- |
| `startScanning`      | `(options: ScanOptions) => void` | Start scanning with given options       |
| `stopScanning`       | `() => void`                     | Stop active scanning                    |
| `enableBluetooth`    | `() => Promise<boolean>`         | Prompt to enable Bluetooth              |
| `enableLocation`     | `() => Promise<boolean>`         | Prompt to enable location               |
| `isBluetoothEnabled` | `() => Promise<boolean>`         | Check if Bluetooth is currently enabled |
| `isLocationEnabled`  | `() => Promise<boolean>`         | Check if location is currently enabled  |

---

### 📍 Found Beacon Structure

| Property             | Type            | Description                        |
| -------------------- | --------------- | ---------------------------------- |
| `event`              | `ScanEventType` | Event type (`FOUND_EDDYSTONE`)     |
| `data.name`          | `string`        | Beacon name                        |
| `data.uuid`          | `string`        | Beacon UUID                        |
| `data.eddystoneUrls` | `string[]`      | List of Eddystone URLs broadcasted |

## Contributing

Project contains an example, checkout the example folder you can try feature by running example app.

### Run

Clone library

```sh
git clone https://github.com/vikash-vaibhav-tcgls/react-native-beacon-scanner.git
```

install dependencies

```sh
yarn install
```

Open terminal and navigate to /example folder

```sh
# run metro server
yarn start
```

This will open android emulator on your system after successful build

```sh
# build for android
yarn android
```

-- or you can open `/example/android` folder in android studio and run it

Make sure app has both bluetooth and gps location permissions and are turned on.

Checkout /example/src/App.tsx file to customize filters, setting as your need

## Troubleshoot
In case you have metro server running and you are getting metro server error
#### Run 
```sh
# open your terminal and run
adb reverse tcp:8081 tcp:8081
```


## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
