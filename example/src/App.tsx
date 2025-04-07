import {
  startScanning,
  stopScanning,
  isBluetoothEnabled,
  isLocationEnabled,
  ScanEventType,
  ServiceUUIDs,
} from 'react-native-beacon-scanner';
import {
  Text,
  View,
  StyleSheet,
  PermissionsAndroid,
  DeviceEventEmitter,
  Button,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { useState, useEffect } from 'react';

export default function App() {
  const [scanning, setScanning] = useState(false);
  const [accumulatedEvents, setAccumulatedEvents] = useState<any[]>([]);
  useEffect(() => {
    permissions();
  }, []);

  useEffect(() => {
    const subscription = DeviceEventEmitter.addListener(
      'beaconEvents',
      ({ event, data }: { event: ScanEventType; data: string }) => {
        console.log('APP_BEACON_PARSER :js- Bluetooth Status:', event, data);
        if (event === ScanEventType.STOPPED) {
          setScanning(false);
        }
        setAccumulatedEvents((acc) => [{ event, data }, ...acc]);
      }
    );

    return () => {
      stopScanning();
      subscription.remove(); // Clean up listener
    };
  }, []);

  const startScan = async () => {
    const a = await isBluetoothEnabled();
    const b = await isLocationEnabled();

    if (a && b) {
      setScanning(true);
      startScanning({
        isLegacy: false,
        filters: [
          {
            serviceUuid: ServiceUUIDs.EDDY_STONE_UUID,
            deviceName: 'Ethics_8871',
          },
        ],
      });
    }
  };

  const permissions = async () => {
    PermissionsAndroid.requestMultiple([
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
      PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
    ]);
  };

  return (
    <View style={styles.container}>
      {scanning ? (
        <View>
          <ActivityIndicator />
          <Button title="Stop Scanning" onPress={stopScanning} />
        </View>
      ) : (
        <Button title="Start scanning" onPress={startScan} />
      )}

      <ScrollView contentContainerStyle={styles.scrollViewContent}>
        {accumulatedEvents.map(
          (event: { event: ScanEventType; data: string }, index) => {
            if (event.event === 'found_eddystone')
              return (
                <Text key={`sdas${index}`} style={styles.greenText}>
                  found_eddystone: {JSON.stringify(event.data)}
                </Text>
              );

            return (
              <Text key={`sdas${index}`} style={styles.blackText}>
                {event.event}: {event.data}
              </Text>
            );
          }
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 30,
    backgroundColor: 'white',
  },
  greenText: { color: 'green' },
  blackText: { color: 'black' },
  scrollViewContent: { flexGrow: 1 },
});
