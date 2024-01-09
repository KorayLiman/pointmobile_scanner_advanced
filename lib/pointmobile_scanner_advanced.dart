import 'package:flutter/services.dart';

// ignore_for_file: constant_identifier_names
const _channel = MethodChannel("pointmobile_scanner_advanced");

enum TriggerMode { auto, continuous, single, multi, multiOnce }

enum ResultType {
  userMessage,
  clipboardKeycodePaste,
  //keyboardEventMultiple_TO_BE_IMPLEMENTED,
  //intentBroadcast_TO_BE_IMPLEMENTED,
  //customIntent_TO_BE_IMPLEMENTED
}

enum Terminator { none, space, tab, lf, tabLf }

enum DecodeMode { standard, priorityTo2DBarcode, fixedFocus }

enum Inverse1DMode { regularTypeOnly, inverseTypeOnly, inverseAutoDetect }

enum GroupSeparator { SOH, STX, EXT, EOT, ENQ, ACK, BEL, BS, HT, LF, VT, FF, CR, SO, SI, DLE, DC1, DC2, DC3, DC4, NAK, SYN, ETB, CAN, EM, SUB, ESC, FS, GS, RS, US }

enum Symbology { NIL, AIRLINE_2OF5_13_DIGIT, AIRLINE_2OF5_15_DIGIT, AZTEC, AUSTRALIAN_POSTAL, BOOKLAND_EAN, BPO, CANPOST, CHINAPOST, CHINESE_2OF5, CODABAR, CODABLOCK, CODE11, CODE128, CODE16K, CODE32, CODE39, CODE49, CODE93, COMPOSITE, COUPON_CODE, DATAMATRIX, DISCRETE_2OF5, DUTCH_POSTAL, EAN128, EAN13, EAN8, GS1_DATABAR_14, GS1_DATABAR_EXPANDED, GS1_DATABAR_LIMITED, HONGKONG_2OF5, IATA_2OF5, IDTAG, INTERLEAVED_2OF5, ISBT128, JAPANESE_POSTAL, KOREAN_POSTAL, MATRIX_2OF5, MAXICODE, MESA, MICRO_PDF417, MICRO_QR, MSI, NEC_2OF5, OCR, PDF417, PLESSEY, POSICODE, POST_US4, QR, STRAIGHT_2OF5, STANDARD_2OF5, TELEPEN, TLCODE39, TRIOPTIC, UK_POSTAL, UPCA, UPCE, UPCE1, US_PLANET, US_POSTNET, USPS_4CB, RSS, LABEL, HANXIN, GRIDMATRIX, INFO_MAIL, INTELLIGENT_MAIL, SWEDENPOST, LAST }

abstract class PMScanner {
  static const String _onDecode = "onDecode";
  static const String _onClipboardPaste = "onClipboardPaste";
  static Function(String symbology, String barcodeNumber)? onDecode;

  static Future<void> _handler(MethodCall call) async {
    if (call.method == _onDecode) {
      if (onDecode != null) {
        onDecode!(call.arguments[0], call.arguments[1]);
      }
    } else if (call.method == _onClipboardPaste) {
      if (onDecode != null) {
        onDecode!("no_symbology_data", call.arguments);
      }
    }
  }

  /// CHECKS IF DEVICE IS POINT MOBILE
  static Future<bool> isDevicePointMobile() async {
    return await _channel.invokeMethod("isDevicePointMobile");
  }

  /// Inıtializes Scanner If It's Not
  static Future<void> initScanner({ResultType resultType = ResultType.userMessage}) async {
    final bool? result = await _channel.invokeMethod("initializeScanner", resultType.index);
    if (result == true) {
      _channel.setMethodCallHandler(_handler);
    }
  }

  // TODO: TO BE IMPLEMENTED
  /*

   -> Seperator of each barcode (Throws Unsupported value: '' of type 'class java.lang.Character')
   -> Enable TouchScan (Couldn't see existing sdk function for this)
   -> Scanner state listener
   -> Set/Get GoodReadFile and BadReadFile

   */

  // SETTER FUNCTIONS

  // static Future<void> setAllSymEnable({required bool isEnabled}) async {
  //   await _channel.invokeMethod("setAllSymEnable", isEnabled);
  // }
  /// SET LIST OF SYMBOLOGIES ENABLED OR NOT
  static Future<void> setSymsEnabled({required List<Symbology> symbologies, required bool isEnabled}) async {
    await _channel.invokeMethod("setSymsEnabled", {"symList": symbologies.map((e) => e.index).toList(), "isEnabled": isEnabled});
  }

  /// SET CHARSET
  static Future<void> setCharset({required String charset}) async {
    await _channel.invokeMethod("setCharset", charset);
  }

  /// SET PREFIX
  static Future<void> setPrefix({required String prefix}) async {
    await _channel.invokeMethod("setPrefix", prefix);
  }

  /// SET PREFIX ENABLED
  static Future<void> setPrefixEnabled({required bool isEnabled}) async {
    await _channel.invokeMethod("setPrefixEnabled", isEnabled);
  }

  /// SET SUFFIX
  static Future<void> setSuffix({required String suffix}) async {
    await _channel.invokeMethod("setSuffix", suffix);
  }

  /// SET SUFFIX ENABLED
  static Future<void> setSuffixEnabled({required bool isEnabled}) async {
    await _channel.invokeMethod("setSuffixEnabled", isEnabled);
  }

  // static Future<void> setGoodReadFile({required String filePath}) async {
  //   await _channel.invokeMethod("setGoodReadFile", filePath);
  // }
  //
  // static Future<void> setBadReadFile({required String filePath}) async {
  //   await _channel.invokeMethod("setBadReadFile", filePath);
  // }
  /// SET GROUP SEPERATOR
  static Future<void> setGroupSeparator({required GroupSeparator groupSeparator}) async {
    await _channel.invokeMethod("setGroupSeparator", groupSeparator.index + 1);
  }

  /// SET SINGLE SYMBOLOGY ENABLED OR NOT
  static Future<void> setSymEnable({required Symbology symbology, required bool isEnabled}) async {
    await _channel.invokeMethod("setSymEnable", {"symId": symbology.index, "isEnabled": isEnabled});
  }

  /// SET DECODE MODE
  static Future<void> setDecodeMode({required DecodeMode decodeMode}) async {
    await _channel.invokeMethod("setDecodeMode", decodeMode.index);
  }

  /// SET TRANSMIT BARCODE ID
  static Future<void> setTransmitBarcodeId({required bool isEnabled}) async {
    await _channel.invokeMethod("setTransmitBarcodeId", isEnabled);
  }

  /// SET TRANSMIT AIM ID
  static Future<void> setTransmitAimId({required bool isEnabled}) async {
    await _channel.invokeMethod("setTransmitAimId", isEnabled);
  }

  /// SET VIBRATOR ENABLE
  static Future<void> setVibratorEnable({required bool isEnabled}) async {
    await _channel.invokeMethod("setVibratorEnable", isEnabled);
  }

  /// SET VIBRATOR SUCCESS INTERVAL
  static Future<void> setVibratorSuccessInterval({required double interval}) async {
    await _channel.invokeMethod("setVibratorSuccessInterval", interval);
  }

  /// SET VIBRATOR FAIL INTERVAL
  static Future<void> setVibratorFailInterval({required double interval}) async {
    await _channel.invokeMethod("setVibratorFailInterval", interval);
  }

  /// SET DECODE DELAY
  static Future<void> setDecodeDelay({required double delay}) async {
    await _channel.invokeMethod("setDecodeDelay", delay);
  }

  /// SET INVERSE 1D MODE
  static Future<void> setInverse1DMode({required Inverse1DMode inverse1dMode}) async {
    await _channel.invokeMethod("setInverse1DMode", inverse1dMode.index);
  }

  /// SET POWERSAVER MODE ENABLED OR NOT
  static Future<void> setPowerSaverModeEnabled({required bool powerSaverModeEnabled}) async {
    await _channel.invokeMethod("setPowerSaverModeEnabled", powerSaverModeEnabled);
  }

  /// SET POWERSAVER TIMEOUT
  static Future<void> setPowerSaverTimeout({required int powerSaverModeTimeoutSeconds}) async {
    await _channel.invokeMethod("setPowerSaverTimeout", powerSaverModeTimeoutSeconds);
  }

  /// SET DECODE ENABLED
  static Future<void> setDecodeEnabled({required bool decodeEnabled}) async {
    await _channel.invokeMethod("setDecodeEnabled", decodeEnabled);
  }

  /// SET BEEP ENABKED
  static Future<void> setBeepEnabled({required bool beepEnabled}) async {
    await _channel.invokeMethod("setBeepEnabled", beepEnabled);
  }

  /// SET TRIGGER ON OR OFF
  static Future<void> triggerOnOff({required bool isOn}) async {
    await _channel.invokeMethod("triggerOnOff", isOn);
  }

  /// SET TRIGGER TIMEOUT
  static Future<void> setTriggerTimeout({required int timeoutSeconds}) async {
    await _channel.invokeMethod("setTriggerTimeout", timeoutSeconds);
  }

  /// SET TRIGGERMODE
  static Future<void> setTriggerMode({required TriggerMode triggerMode}) async {
    await _channel.invokeMethod("setTriggerMode", triggerMode.index);
  }

  /// SET CENTER WINDOW ENABLED
  static Future<void> setCenterWindowEnabled({required bool centerWindowEnabled}) async {
    await _channel.invokeMethod("setCenterWindowEnabled", centerWindowEnabled);
  }

  /// SET CENTER WINDOW TOLERANCE
  static Future<void> setCenterWindowTolerance({required int tolerance}) async {
    await _channel.invokeMethod("setCenterWindowTolerance", tolerance);
  }

  /// SET AIM ON
  static Future<void> setAimOn({required bool aimOn}) async {
    await _channel.invokeMethod("setAimOn", aimOn);
  }

  /// SET ILLUMINATION ON
  static Future<void> setIllumOn({required bool illumOn}) async {
    await _channel.invokeMethod("setIllumOn", illumOn);
  }

  /// SET LASER FOCUS ENABLED
  static Future<void> setLaserFocusEnabled({required bool laserFocusEnabled}) async {
    await _channel.invokeMethod("setLaserFocusEnabled", laserFocusEnabled);
  }

  /// SET LED ENABLED
  static Future<void> setLedEnabled({required bool ledEnabled}) async {
    await _channel.invokeMethod("setLedEnabled", ledEnabled);
  }

  /// SET AIMER ENABLED
  static Future<void> setAimerEnabled({required bool aimerEnabled}) async {
    await _channel.invokeMethod("setAimerEnabled", aimerEnabled);
  }

  /// RESET ALL SETTINGS TO DEFAULT
  static Future<void> resetAllSettingsToDefault() async {
    await _channel.invokeMethod("setDefaultAllSettings");
  }

  /// SET RESULT TYPE
  static Future<void> setResultType({required ResultType resultType}) async {
    await _channel.invokeMethod("setResultType", resultType.index);
  }

  /// SET AUTOSCAN INTERVAL
  static Future<void> setAutoScanInterval({required double seconds}) async {
    await _channel.invokeMethod("setAutoScanInterval", seconds);
  }

  /// SET NUMBER OF BARCODES TO SCAN
  static Future<void> setNumberOfBarcodesToScan({required int number}) async {
    await _channel.invokeMethod("setNumberOfBarcodesToScan", number);
  }

  /// SET TERMINATOR
  static Future<void> setTerminator({required Terminator terminator}) async {
    late int terminatorIndex;
    switch (terminator) {
      case Terminator.none:
        terminatorIndex = 0;
        break;
      case Terminator.space:
        terminatorIndex = 2;
        break;
      case Terminator.tab:
        terminatorIndex = 3;
        break;
      case Terminator.lf:
        terminatorIndex = 5;
        break;
      case Terminator.tabLf:
        terminatorIndex = 6;
        break;
      default:
        terminatorIndex = 0;
        break;
    }
    await _channel.invokeMethod("setTerminator", terminatorIndex);
  }

  // GETTER FUNCTIONS

  static Future<String> getPrefix() async {
    return await _channel.invokeMethod("getPrefix");
  }

  static Future<bool> getPrefixEnabled() async {
    return await _channel.invokeMethod("getPrefixEnabled") == 1;
  }

  static Future<String> getSuffix() async {
    return await _channel.invokeMethod("getSuffix");
  }

  static Future<bool> getSuffixEnabled() async {
    return await _channel.invokeMethod("getSuffixEnabled") == 1;
  }

  static Future<String> getCharset() async {
    return await _channel.invokeMethod("getCharset");
  }

  static Future<GroupSeparator> getGroupSeparator() async {
    int index = await _channel.invokeMethod("getGroupSeparator");
    return GroupSeparator.values[index];
  }

  static Future<bool> getSymEnabled({required Symbology symbology}) async {
    return await _channel.invokeMethod("getSymEnable", {"symId": symbology.index});
  }

  static Future<int> getTriggerTimeout() async {
    return (await _channel.invokeMethod("getTriggerTimeout")) / 1000;
  }

  static Future<int> getCenterWindowTolerance() async {
    return await _channel.invokeMethod("getCenterWindowTolerance");
  }

  static Future<int> getPowerSaverTimeout() async {
    return await _channel.invokeMethod("getPowerSaverTimeout");
  }

  static Future<int> getTerminator() async {
    return await _channel.invokeMethod("getTerminator");
  }

  @Deprecated("- This method is deprecated in Scanner SDK -")
  static Future<bool> getAimOn() async {
    return await _channel.invokeMethod("getAimOn");
  }

  @Deprecated("- This method is deprecated in Scanner SDK -")
  static Future<bool> getIllumOn() async {
    return await _channel.invokeMethod("getIllumOn");
  }

  // static Future<String> getGoodReadFile() async {
  //   return await _channel.invokeMethod("getGoodReadFile");
  // }
  //
  // static Future<String> getBadReadFile() async {
  //   return await _channel.invokeMethod("getBadReadFile");
  // }

  static Future<bool> getTransmitBarcodeId() async {
    return await _channel.invokeMethod("getTransmitBarcodeId");
  }

  static Future<bool> getTransmitAimId() async {
    return await _channel.invokeMethod("getTransmitAimId");
  }

  static Future<int> getNumberOfBarcodesToScan() async {
    return await _channel.invokeMethod("getNumberOfBarcodesToScan");
  }

  static Future<double> getAutoScanInterval() async {
    return (await _channel.invokeMethod("getAutoScanInterval")) / 1000;
  }

  static Future<double> getDecodeDelay() async {
    return (await _channel.invokeMethod("getDecodeDelay")) / 1000;
  }

  static Future<TriggerMode> getTriggerMode() async {
    int index = await _channel.invokeMethod("getTriggerMode");
    return TriggerMode.values[index];
  }

  static Future<bool> getBeepEnabled() async {
    return await _channel.invokeMethod("getBeepEnabled");
  }

  static Future<bool> getDecodeEnabled() async {
    return await _channel.invokeMethod("getDecodeEnabled");
  }

  static Future<bool> getCenterWindowEnabled() async {
    return await _channel.invokeMethod("getCenterWindowEnabled");
  }

  static Future<DecodeMode> getDecodeMode() async {
    int index = await _channel.invokeMethod("getDecodeMode");
    return DecodeMode.values[index];
  }

  static Future<Inverse1DMode> getInverse1DMode() async {
    int index = await _channel.invokeMethod("getInverse1DMode");
    return Inverse1DMode.values[index];
  }

  static Future<bool> getPowerSaverModeEnabled() async {
    return await _channel.invokeMethod("getPowerSaverModeEnabled");
  }

  static Future<ResultType> getResultType() async {
    int index = await _channel.invokeMethod("getResultType");
    return ResultType.values[index];
  }

  static Future<bool> getLaserFocusEnable() async {
    return await _channel.invokeMethod("getLaserFocusEnable");
  }

  static Future<bool> getLedEnable() async {
    return await _channel.invokeMethod("getLedEnable");
  }

  static Future<bool> getAimerEnable() async {
    return await _channel.invokeMethod("getAimerEnable");
  }

  static Future<bool> getVibratorEnable() async {
    return await _channel.invokeMethod("getVibratorEnable");
  }

  static Future<double> getVibratorSuccessInterval() async {
    return (await _channel.invokeMethod("getVibratorSuccessInterval")) / 1000;
  }

  static Future<double> getVibratorFailInterval() async {
    return (await _channel.invokeMethod("getVibratorFailInterval")) / 1000;
  }
}

abstract class PMUtils {
  /// START LISTENING CLIPBOARD CHANGES
  static Future<void> listenClipboard() async {
    await _channel.invokeMethod("listenClipboard");
  }

  /// STOP LISTENING CLIPBOARD CHANGES
  static Future<void> stopListeningClipboard() async {
    await _channel.invokeMethod("stopListeningClipboard");
  }

  /// CLEAR CLIPBOARD. IF YOU ARE LISTENING CLIPBOARD YOU'LL ALSO GET CLEAR CHANGES
  static Future<void> clearClipboard() async {
    await _channel.invokeMethod("clearClipboard");
  }

  /// CLEAR CLIPBOARD. IF YOU ARE LISTENING CLIPBOARD YOU'LL NOT GET CLEAR CHANGES
  static Future<void> clearClipboardWithoutListening() async {
    await _channel.invokeMethod("clearClipboardWithoutListening");
  }
}
