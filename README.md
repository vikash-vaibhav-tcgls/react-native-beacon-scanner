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

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
