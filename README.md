# Pointmobile Scanner Advanced [![GitHub stars](https://img.shields.io/github/stars/KorayLiman/pointmobile_scanner_advanced.svg)](https://github.com/KorayLiman/pointmobile_scanner_advanced)

Pointmobile Scanner Advanced is a feature-rich plugin to manage Scanner settings and configurations for Pointmobile devices

## Before Use

Import package

```dart
import 'package:pointmobile_scanner_advanced/pointmobile_scanner_advanced.dart';
```

## Usage
Initialize scanner first
* Parameterless function sets ResultType to "userMessage"
```dart
  await PMScanner.initScanner();
```
* If you want to use another ResultType you can set it by passing ResultType parameter and also change it later
```dart
  await PMScanner.initScanner(resultType: ResultType.clipboardKeycodePaste);
```
! If you want to use ClipboardKeycodePast this package also offers to catch copied barcode number to the Clipboard. See below for details.

## Catching Scanned Barcodes
*  Set onDecode function to your custom function (onDecode function defaults to null)
```dart
  PMScanner.onDecode = _onDecode;
```
* If you set ResultType to ResultType.clipboardKeycodePaste and wanna be able to get scanned barcode
```dart
// TO LISTEN
await PMUtils.listenClipboard();
// TO STOP LISTENING
await PMUtils.stopListeningClipboard();
// TO CLEAR CLIPBOARD
await PMUtils.clearClipboard();
// TO CLEAR CLIPBOARD WITHOUT LISTENING
await PMUtils.clearClipboardWithoutListening();
```
* Your custom function
```dart
  void _onDecode(Symbology symbology, String barcodeNumber) {
  print("$symbology --- $barcodeNumber");
}
```
## Configuring Scanner Settings
Pointmobile Scanner Advanced package offers many amount of functions to configure scanner settings. Here are some of them:
```dart
    await PMScanner.setDecodeMode(decodeMode: DecodeMode.fixedFocus);
    await PMScanner.setBeepEnabled(beepEnabled: true);
    await PMScanner.resetAllSettingsToDefault();
    await PMScanner.setCenterWindowTolerance(tolerance: 30);
    final decodeDelay = await PMScanner.getDecodeDelay();
    final barcodeScanNumber = await PMScanner.getNumberOfBarcodesToScan();
    final timeout = await PMScanner.getTriggerTimeout();
```
* For more please see the PMScanner class.

## Authors

- [Koray Liman](https://github.com/KorayLiman)

## Roadmap

- Scanner state listener

- Set/Get Good and Bad Read File

