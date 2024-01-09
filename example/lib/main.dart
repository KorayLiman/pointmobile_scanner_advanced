import 'package:flutter/material.dart';
import 'package:pointmobile_scanner_advanced/pointmobile_scanner_advanced.dart';

void main() => runApp(const MyApp());

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    _runExampleScannerUtils();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Material App',
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Material App Bar'),
        ),
        body: const Center(
          child: Text('Hello World'),
        ),
      ),
    );
  }

  Future<void> _runExampleScannerUtils() async {
    if (!await PMScanner.isDevicePointMobile()) {
      return;
    }
    // INIT SCANNER FIRST
    await PMScanner.initScanner(resultType: ResultType.clipboardKeycodePaste);
    await PMScanner.setDecodeMode(decodeMode: DecodeMode.fixedFocus);
    await PMScanner.setBeepEnabled(beepEnabled: true);
    await PMScanner.setCenterWindowTolerance(tolerance: 30);
    final timeout = await PMScanner.getTriggerTimeout();
    final decodeDelay = await PMScanner.getDecodeDelay();
    final barcodeScanNumber = await PMScanner.getNumberOfBarcodesToScan();
    await PMScanner.resetAllSettingsToDefault();

    /*
    IF YOU SET RESULTTYPE TO CLIPBOARDKEYCODEPASTE
    DO NOT FORGET TO LISTEN CLIPBOARD

     ----await PMUtils.listenClipboard(); ----
     
     */

    PMScanner.onDecode = _onDecode;
  }

  void _onDecode(String symbology, String barcodeNumber) {
    print("$symbology --- $barcodeNumber");
  }
}
