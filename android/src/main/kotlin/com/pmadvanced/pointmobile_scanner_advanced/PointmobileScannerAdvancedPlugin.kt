package com.pmadvanced.pointmobile_scanner_advanced

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import device.common.DecodeResult
import device.common.ScanConst
import device.sdk.DeviceServer
import device.sdk.ScanManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** PointmobileScannerAdvancedPlugin */
class PointmobileScannerAdvancedPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var mContext: Context

    class ScanResultReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "On Receive")
            if (mScanner != null) {
                try {
                    mScanner!!.aDecodeGetResult(mDecodeResult.recycle())
                    onDecode(mDecodeResult)
                } catch (e: Throwable) {
                    Log.d(TAG, "$e")
                }
            }
        }
    }

    private var mActivity: Activity? = null

    companion object Companions {
        private var mScanner: ScanManager? = null
        lateinit var mChannel: MethodChannel
        lateinit var mDecodeResult: DecodeResult
        private var permissionResult: Result? = null
        private var mBinding: ActivityPluginBinding? = null
        private var mScanResultReceiver: ScanResultReceiver? = null
        const val TAG = "PMAdvanced"
        const val onDecode = "onDecode";
        // var customIntentCategory = "android.intent.category.DEFAULT"
        const val onClipboardPaste = "onClipboardPaste";
        private var listener: ClipboardManager.OnPrimaryClipChangedListener? = null
        fun onDecode(decodeResult: DecodeResult) {
            val alDecodeResult = ArrayList<String>()
            alDecodeResult.add(decodeResult.symName)
            alDecodeResult.add(decodeResult.toString())
            Log.d(TAG, alDecodeResult.toString())
            mChannel.invokeMethod(onDecode, alDecodeResult)

        }
    }

    private fun requestPermissionListenerRead(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray,
        isSuccessFile: Boolean,

        ): Boolean {

        if (permissionResult != null) {
            if (results[0] == PackageManager.PERMISSION_GRANTED) {
                if (isSuccessFile) {
                    permissionResult!!.success(mScanner?.aDecodeGetBeepSuccessFile())
                } else {
                    permissionResult!!.success(mScanner?.aDecodeGetBeepFailFile())
                }
            } else {
                permissionResult!!.error("NOT GRANTED", "Write Storage access is not granted", null)
            }
        }

        permissionResult = null
        return true
    }

    private fun requestPermissionListener(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray,
        path: String,
        isSuccessFile: Boolean,

        ): Boolean {
        if (permissionResult != null) {
            if (results[0] == PackageManager.PERMISSION_GRANTED) {
                if (isSuccessFile) {
                    mScanner?.aDecodeSetBeepSuccessFile(path)
                    permissionResult!!.success(null)

                } else {
                    mScanner?.aDecodeSetBeepFailFile(path)
                    permissionResult!!.success(null)
                }
            } else {
                permissionResult!!.error("NOT GRANTED", "Write Storage access is not granted", null)
            }
        }
        permissionResult = null
        return true
    }


    override fun onAttachedToActivity(binding: ActivityPluginBinding) {

        mBinding = binding
        mActivity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        mBinding = null
        mActivity = null
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(TAG, "onAttachedToEngine")
        mContext = flutterPluginBinding.applicationContext
        mChannel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "pointmobile_scanner_advanced")
        mChannel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (!isDevicePointMobile()) {
            result.error(
                "Device Type Error",
                "-----THIS PLUGIN MEANT TO BE USED IN POINTMOBILE DEVICES----",
                "This plugin can only be used in PointMobile devices"
            )
        }
        if (call.method != "initializeScanner")
            if (mScanner == null) {
                result.error(
                    "Initialization Error",
                    "-----PLEASE INITIALIZE SCANNER FIRST----",
                    "Scanner has to be initialized first"
                )
            }

        when (call.method) {
            "setDecodeEnabled" -> setDecodeEnabled(
                call.arguments<Boolean>() ?: false, result
            )

            "setBeepEnabled" -> setBeepEnabled(call.arguments<Boolean>() ?: false, result)
            "triggerOnOff" -> triggerOnOff(call.arguments<Boolean>() ?: false, result)

            "setTriggerTimeout" -> setTriggerTimeout(call.arguments<Int>() ?: 1, result)

            "setTriggerMode" -> setTriggerMode(call.arguments<Int>() ?: -1, result)

            "setCenterWindowEnabled" -> setCenterWindowEnabled(
                call.arguments<Boolean>() ?: false, result
            )

            "setCenterWindowTolerance" -> setCenterWindowTolerance(
                call.arguments<Int>() ?: 10, result
            )

            "setAimOn" -> setAimOn(call.arguments<Boolean>() ?: false, result)

            "setIllumOn" -> setIllumOn(call.arguments<Boolean>() ?: false, result)

            "setLaserFocusEnabled" -> setLaserFocusEnable(
                call.arguments<Boolean>() ?: false, result
            )


            "setLedEnabled" -> setLedEnable(call.arguments<Boolean>() ?: false, result)


            "setDefaultAllSettings" -> setDefaultAllSettings(result)


            "setAimerEnabled" -> setAimerEnable(
                call.arguments<Boolean>() ?: false, result
            )


            "initializeScanner" -> initializeScanner(call.arguments<Int>() ?: 0, result)
            "setResultType" -> setResultType(call.arguments<Int>() ?: 0, result)
            "listenClipboard" -> listenClipboard(result)
            "stopListeningClipboard" -> stopListeningClipboard(result)
            "clearClipboard" -> clearClipboard(result)
            "clearClipboardWithoutListening" -> clearClipboardWithoutListening(result)
            "setAutoScanInterval" -> setAutoScanInterval(
                call.arguments<Double>() ?: 1.0,

                result
            )

            "setNumberOfBarcodesToScan" -> setNumberOfBarcodesToScan(
                call.arguments<Int>() ?: 2,

                result
            )

            "setTerminator" -> setTerminator(call.arguments<Int>() ?: 0, result)
            "getTerminator" -> getTerminator(result)
            "setDecodeMode" -> setDecodeMode(call.arguments<Int>() ?: 0, result)
            "setInverse1DMode" -> setInverse1DMode(call.arguments<Int>() ?: 0, result)
            "setPowerSaverModeEnabled" -> setPowerSaverModeEnabled(
                call.arguments<Boolean>() ?: false, result
            )

            "setPowerSaverTimeout" -> setPowerSaverTimeout(
                call.arguments<Int>() ?: 0,

                result
            )

            "setDecodeDelay" -> setDecodeDelay(call.arguments<Double>() ?: 0.0, result)
            "setGoodReadFile" -> setGoodReadFile(call.arguments<String>() ?: "", result)
            "setBadReadFile" -> setBadReadFile(call.arguments<String>() ?: "", result)
            "setVibratorEnable" -> setVibratorEnable(
                call.arguments<Boolean>() ?: false,

                result
            )

            "setVibratorSuccessInterval" -> setVibratorSuccessInterval(
                call.arguments<Double>() ?: 0.0, result
            )

            "setVibratorFailInterval" -> setVibratorFailInterval(
                call.arguments<Double>() ?: 0.0, result
            )

            "setTransmitAimId" -> setTransmitAimId(
                call.arguments<Boolean>() ?: false,

                result
            )

            "setTransmitBarcodeId" -> setTransmitBarcodeId(
                call.arguments<Boolean>() ?: false,

                result
            )

            "getGoodReadFile" -> getGoodReadFile(result)
            "getBadReadFile" -> getBadReadFile(result)
            "getTransmitBarcodeId" -> getTransmitBarcodeId(result)
            "getTransmitAimId" -> getTransmitAimId(result)
            "getNumberOfBarcodesToScan" -> getNumberOfBarcodesToScan(result)
            "getAutoScanInterval" -> getAutoScanInterval(result)
            "getDecodeDelay" -> getDecodeDelay(result)
            "getTriggerMode" -> getTriggerMode(result)
            "getTriggerTimeout" -> getTriggerTimeout(result)
            "getBeepEnabled" -> getBeepEnabled(result)
            "getDecodeEnabled" -> getDecodeEnabled(result)
            "getCenterWindowEnabled" -> getCenterWindowEnabled(result)
            "getCenterWindowTolerance" -> getCenterWindowTolerance(result)
            "getDecodeMode" -> getDecodeMode(result)
            "getInverse1DMode" -> getInverse1DMode(result)
            "getPowerSaverModeEnabled" -> getPowerSaverModeEnabled(result)
            "getPowerSaverTimeout" -> getPowerSaverTimeout(result)
            "getResultType" -> getResultType(result)
            "getAimOn" -> getAimOn(result)
            "getIllumOn" -> getIllumOn(result)
            "getLaserFocusEnable" -> getLaserFocusEnable(result)
            "getLedEnable" -> getLedEnable(result)
            "getAimerEnable" -> getAimerEnable(result)
            "getVibratorEnable" -> getVibratorEnable(result)
            "getVibratorSuccessInterval" -> getVibratorSuccessInterval(result)
            "getVibratorFailInterval" -> getVibratorFailInterval(result)
            "getSymEnable" -> getSymEnable(
                call.argument<Int>("symId") ?: -1,

                result
            )

            "setSymEnable" -> setSymEnable(
                call.argument<Int>("symId") ?: -1,
                call.argument<Boolean>("isEnabled") ?: false,

                result
            )

            "setSymsEnabled" -> setSymsEnabled(
                call.argument<List<Int>>("symList") ?: emptyList(),
                call.argument<Boolean>("isEnabled") ?: false,
                result
            )

            "setAllSymEnable" -> setAllSymEnable(call.arguments<Boolean>() ?: false, result)
            "setGroupSeparator" -> setGroupSeparator(call.arguments<Int>() ?: 1, result)
            "getGroupSeparator" -> getGroupSeparator(result)
//            "setSeparatorOfEachBarcode"->setSeparatorOfEachBarcode(call.arguments<Int>() ?:1,call,result)
            "getSeparatorOfEachBarcode" -> getSeparatorOfEachBarcode(call, result)
            "setPrefixEnabled" -> setPrefixEnabled(call.arguments<Boolean>() ?: false, result)
            "getPrefixEnabled" -> getPrefixEnabled(result)
            "setPrefix" -> setPrefix(call.arguments<String>() ?: "", result)
            "getPrefix" -> getPrefix(result)
            "setSuffixEnabled" -> setSuffixEnabled(call.arguments<Boolean>() ?: false, result)
            "getSuffixEnabled" -> getSuffixEnabled(result)
            "setSuffix" -> setSuffix(call.arguments<String>() ?: "", result)
            "getSuffix" -> getSuffix(result)
            "setCharset" -> setCharset(call.arguments<String>() ?: "", result)
            "getCharset" -> getCharset(result)
            else -> result.notImplemented()
        }


    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        mChannel.setMethodCallHandler(null)
    }

    // GETTER FUNCTIONS


    private fun getTerminator(result: Result) {
        result.success(mScanner?.aDecodeGetTerminator())

    }

    private fun getGoodReadFile(result: Result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissionResult = result
                mBinding?.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
                    requestPermissionListenerRead(
                        requestCode,
                        permissions,
                        grantResults,
                        true,

                        )
                }

                mActivity?.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    ), 1
                )
            } else {
                result.success(mScanner?.aDecodeGetBeepSuccessFile())
            }
        } else {
            result.error("Version error", "OS Version must be greater than API 23", null)
        }

    }

    private fun getBadReadFile(result: Result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissionResult = result
                mBinding?.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
                    requestPermissionListenerRead(
                        requestCode,
                        permissions,
                        grantResults,
                        false,

                        )
                }
                mActivity?.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    ), 1
                )
            } else {
                result.success(mScanner?.aDecodeGetBeepFailFile())
            }
        } else {
            result.error("Version error", "OS Version must be greater than API 23", null)
        }

    }

    private fun getSeparatorOfEachBarcode(call: MethodCall, result: Result) {
        result.success(mScanner?.aDecodeGetMultiScanSeparator())
    }

    private fun getTransmitBarcodeId(result: Result) {
        result.success(mScanner?.aDecodeGetResultSymIdEnable())
    }

    private fun getTransmitAimId(result: Result) {
        result.success(mScanner?.aDecodeGetResultAimIdEnable())
    }

    private fun getNumberOfBarcodesToScan(result: Result) {
        result.success(mScanner?.aDecodeGetMultiScanNumber())
    }

    private fun getAutoScanInterval(result: Result) {
        result.success(mScanner?.aDecodeGetTriggerInterval())
    }

    private fun getDecodeDelay(result: Result) {
        result.success(mScanner?.aDecodeGetDecodeDelay())
    }


    private fun getTriggerMode(result: Result) {
        result.success(mScanner?.aDecodeGetTriggerMode())
    }


    private fun getTriggerTimeout(result: Result) {
        result.success(mScanner?.aDecodeGetTriggerTimeout())
    }

    private fun getBeepEnabled(result: Result) {
        result.success(mScanner?.aDecodeGetBeepEnable() == 1)
    }

    private fun getDecodeEnabled(result: Result) {
        result.success(mScanner?.aDecodeGetDecodeEnable() == 1)
    }

    private fun getCenterWindowEnabled(result: Result) {
        result.success(mScanner?.aDecodeGetCenterWindowEnable() == 1)
    }

    private fun getCenterWindowTolerance(result: Result) {
        result.success(mScanner?.aDecodeGetCenterWindowTolerance())
    }


    private fun getDecodeMode(result: Result) {
        result.success(mScanner?.aDecodeGetDecodeMode())
    }

    private fun getInverse1DMode(result: Result) {
        result.success(mScanner?.aDecodeGetInverse1DMode())
    }

    private fun getPowerSaverModeEnabled(result: Result) {
        result.success(mScanner?.aDecodeGetPowerSaveMode() == 1)
    }

    private fun getPowerSaverTimeout(result: Result) {
        result.success(mScanner?.aDecodeGetPowerSaveTimeOut())
    }


    private fun getResultType(result: Result) {
        result.success(mScanner?.aDecodeGetResultType())
    }


    private fun getAimOn(result: Result) {
        result.success(mScanner?.aDecodeGetAimOn() == 1)
    }

    private fun getSymEnable(symId: Int, result: Result) {
        result.success(mScanner?.aDecodeSymGetEnable(symId) == 1)
    }

    private fun getIllumOn(result: Result) {
        result.success(mScanner?.aDecodeGetIllumOn() == 1)
    }

    private fun getLaserFocusEnable(result: Result) {
        result.success(mScanner?.aDecodeGetLaserFocusEnable() == 1)
    }

    private fun getLedEnable(result: Result) {
        result.success(mScanner?.aDecodeGetLedEnable() == 1)
    }


    private fun getAimerEnable(result: Result) {
        result.success(mScanner?.aDecodeGetAimerEnable() == 1)
    }

    private fun getVibratorEnable(result: Result) {
        result.success(mScanner?.aDecodeGetVibratorEnable() == 1)
    }

    private fun getVibratorSuccessInterval(result: Result) {
        result.success(mScanner?.aDecodeGetVibratorSuccessInterval())
    }

    private fun getVibratorFailInterval(result: Result) {
        result.success(mScanner?.aDecodeGetVibratorFailInterval())
    }

    private fun getPrefixEnabled(result: Result) {
        result.success(mScanner?.aDecodeGetPrefixEnable())
    }

    private fun getPrefix(result: Result) {
        result.success(mScanner?.aDecodeGetPrefix())
    }

    private fun getSuffixEnabled(result: Result) {
        result.success(mScanner?.aDecodeGetPostfixEnable())
    }

    private fun getSuffix(result: Result) {
        result.success(mScanner?.aDecodeGetPostfix())
    }

    private fun getCharset(result: Result) {
        result.success(mScanner?.aDecodeGetCharset())
    }

    private fun getGroupSeparator(result: Result) {
        result.success(mScanner?.aDecodeGetGroupSeparator())
    }

    // SETTER FUNCTIONS
    private fun setCharset(charset: String, result: Result) {
        mScanner?.aDecodeSetCharset(charset)
        result.success(null)
    }

    private fun setTerminator(terminatorIndex: Int, result: Result) {
        mScanner?.aDecodeSetTerminator(terminatorIndex)
        result.success(null)
    }


    private fun setGoodReadFile(filePath: String, result: Result) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissionResult = result
                mBinding?.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
                    requestPermissionListener(
                        requestCode,
                        permissions,
                        grantResults,
                        filePath,
                        true,
                    )
                }
                mActivity?.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ), 1
                )
            } else {
                mScanner?.aDecodeSetBeepSuccessFile(filePath)
                result.success(null)
            }
        } else {
            result.error("Version error", "OS Version must be greater than API 23", null)
        }

    }

    private fun setBadReadFile(filePath: String, result: Result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissionResult = result
                mBinding?.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
                    requestPermissionListener(
                        requestCode,
                        permissions,
                        grantResults,
                        filePath,
                        false,

                        )
                }
                mActivity?.requestPermissions(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ), 1
                )
            } else {
                mScanner?.aDecodeSetBeepFailFile(filePath)
                result.success(null)
            }
        } else {
            result.error("Version error", "OS Version must be greater than API 23", null)
        }

    }

    private fun setTransmitBarcodeId(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetResultSymIdEnable(enabled)
        result.success(null)
    }

    private fun setTransmitAimId(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetResultAimIdEnable(enabled)
        result.success(null)
    }

    private fun setNumberOfBarcodesToScan(number: Int, result: Result) {
        mScanner?.aDecodeSetMultiScanNumber(number)
        result.success(null)
    }

    private fun setAutoScanInterval(seconds: Double, result: Result) {
        val timeoutMs = seconds.times(1000)
        mScanner?.aDecodeSetTriggerInterval(timeoutMs.toInt())
        result.success(null)
    }

    private fun setDecodeDelay(seconds: Double, result: Result) {
        val delayMs = seconds.times(1000)
        mScanner?.aDecodeSetDecodeDelay(delayMs.toInt())
        result.success(null)
    }


    private fun setTriggerMode(triggerModeIndex: Int, result: Result) {
        when (triggerModeIndex) {
            0 -> mScanner?.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO)
            1 -> mScanner?.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_CONTINUOUS)
            2 -> mScanner?.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_MULTI)
            3 -> mScanner?.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_MULTI_ONCE)
            4 -> mScanner?.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT)
            else -> result.error("Undefined Trigger Mode", null, null)
        }
        result.success(null)
    }

    private fun setSymsEnabled(symList: List<Int>, isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        for (value in symList) {
            mScanner?.aDecodeSymSetEnable(value, enabled)
        }
        result.success(null)
    }

    private fun setAllSymEnable(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        val l = List(70) { 0 }
        var i = 0
        while (i < l.size) {
            mScanner?.aDecodeSymSetEnable(i, enabled)
            i++
        }
        result.success(null)
    }

    private fun setGroupSeparator(separatorId: Int, result: Result) {
        mScanner?.aDecodeSetGroupSeparator(separatorId)
        result.success(null)

    }

    //        private fun setSeparatorOfEachBarcode(separatorId: Int, result: Result) {
//        mScanner?.aDecodeSetMultiScanSeparator(separator)
//        result.success(null)
//    }
    private fun setPrefix(prefix: String, result: Result) {
        mScanner?.aDecodeSetPrefix(prefix)
        result.success(null)
    }

    private fun setSuffix(suffix: String, result: Result) {
        mScanner?.aDecodeSetPostfix(suffix)
        result.success(null)
    }

    private fun setSymEnable(symId: Int, isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSymSetEnable(symId, enabled)
        result.success(null)
    }

    private fun setTriggerTimeout(seconds: Int, result: Result) {
        val timeoutMs = seconds.times(1000)
        mScanner?.aDecodeSetTriggerTimeout(timeoutMs)
        result.success(null)
    }

    private fun triggerOnOff(isOn: Boolean, result: Result) {
        if (isOn) {
            mScanner?.aDecodeSetTriggerOn(1)
        } else {
            mScanner?.aDecodeSetTriggerOn(0)
        }
        result.success(null)
    }

    private fun setPrefixEnabled(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetPrefixEnable(enabled)
        result.success(null)
    }

    private fun setSuffixEnabled(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetPostfixEnable(enabled)
        result.success(null)
    }

    private fun setBeepEnabled(isEnabled: Boolean, result: Result) {

        if (isEnabled) {
            mScanner?.aDecodeSetBeepEnable(1)
        } else {
            mScanner?.aDecodeSetBeepEnable(0)
        }
        result.success(null)
    }

    private fun setDecodeEnabled(isEnabled: Boolean, result: Result) {
        if (isEnabled) {
            mScanner?.aDecodeSetDecodeEnable(1)
        } else {
            mScanner?.aDecodeSetDecodeEnable(0)
        }
        result.success(null)
    }

    private fun setCenterWindowEnabled(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetCenterWindowEnable(enabled)
        result.success(null)
    }

    private fun setCenterWindowTolerance(tolerance: Int, result: Result) {

        mScanner?.aDecodeSetCenterWindowTolerance(tolerance)

        result.success(null)
    }


    private fun setDecodeMode(mode: Int, result: Result) {
        mScanner?.aDecodeSetDecodeMode(mode)
        result.success(null)
    }

    private fun setInverse1DMode(mode: Int, result: Result) {
        mScanner?.aDecodeSetInverse1DMode(mode)
        result.success(null)
    }

    private fun setPowerSaverModeEnabled(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetPowerSaveMode(enabled)
        result.success(null)
    }

    private fun setPowerSaverTimeout(timeout: Int, result: Result) {
        mScanner?.aDecodeSetPowerSaveTimeOut(timeout)
        result.success(null)
    }


    private fun setResultType(resultTypeIndex: Int, result: Result) {
        if (mScanner != null) {
            when (resultTypeIndex) {
                0 -> {
                    mScanner!!.aDecodeSetResultType(ScanConst.RESULT_USERMSG)
                    if (mScanResultReceiver != null) {
                        mContext.unregisterReceiver(mScanResultReceiver)
                        mScanResultReceiver = null
                    }
                    mScanResultReceiver = ScanResultReceiver()
                    val filter = IntentFilter()
                    filter.addAction(ScanConst.INTENT_USERMSG)
                    mContext.registerReceiver(mScanResultReceiver, filter)
                    mDecodeResult = DecodeResult()

                }

                1 -> {
                    mScanner!!.aDecodeSetResultType(ScanConst.RESULT_COPYPASTE)
                    if (mScanResultReceiver != null) {
                        mContext.unregisterReceiver(mScanResultReceiver)
                        mScanResultReceiver = null
                    }
                }

                2 -> {
//                    mScanner?.aDecodeSetResultType(ScanConst.RESULT_KBDMSG)
                }

                3 -> {
//                    mScanner?.aDecodeSetResultType(ScanConst.RESULT_EVENT)
                }

                4 -> {
//                    mScanner?.aDecodeSetResultType(ScanConst.RESULT_CUSTOM_INTENT)
                }

                else -> result.error("Undefined Result Type", null, null)
            }
        }
        result.success(null)
    }


    private fun setAimOn(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetAimOn(enabled)
        result.success(null)
    }

    private fun setIllumOn(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetIllumOn(enabled)
        result.success(null)
    }

    private fun setLaserFocusEnable(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetLaserFocusEnable(enabled)
        result.success(null)
    }

    private fun setLedEnable(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetLedEnable(enabled)
        result.success(null)
    }

    private fun setDefaultAllSettings(result: Result) {
        mScanner?.aDecodeSetDefaultAll()
        result.success(null)
    }

    private fun setAimerEnable(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetAimerEnable(enabled)
        result.success(null)
    }

    private fun setVibratorEnable(isEnabled: Boolean, result: Result) {
        val enabled = if (isEnabled) 1 else 0
        mScanner?.aDecodeSetVibratorEnable(enabled)
        result.success(null)
    }

    private fun setVibratorSuccessInterval(interval: Double, result: Result) {
        val intervalMs = interval.times(1000)
        mScanner?.aDecodeSetVibratorSuccessInterval(intervalMs.toInt())
        result.success(null)
    }

    private fun setVibratorFailInterval(interval: Double, result: Result) {
        val intervalMs = interval.times(1000)
        mScanner?.aDecodeSetVibratorFailInterval(intervalMs.toInt())
        result.success(null)
    }

    // UTILS
    private fun listenClipboard(result: Result) {
        if (listener == null) {
            val clipboardManager = mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            listener = ClipboardManager.OnPrimaryClipChangedListener {
                val copiedValue: String? =
                    clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
                if (copiedValue == null) {
                    Log.d(TAG, "Copied value: null")
                } else {
                    Log.d(TAG, "Copied value: $copiedValue")
                }

                mChannel.invokeMethod(onClipboardPaste, copiedValue)

            }
            clipboardManager.addPrimaryClipChangedListener(listener)
        }
        result.success(null)
    }

    private fun stopListeningClipboard(result: Result) {
        val clipboardManager = mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (listener != null) {
            clipboardManager.removePrimaryClipChangedListener(listener)
            listener = null
        }
        result.success(null)

    }

    private fun clearClipboard(result: Result) {
        val clipboardManager = mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, null))
        result.success(null)
    }

    private fun clearClipboardWithoutListening(result: Result) {
        val clipboardManager = mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (listener != null) {
            clipboardManager.removePrimaryClipChangedListener(listener)
            listener = null
        }


        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, null))
        if (listener == null) {
            val clipboardManager = mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            listener = ClipboardManager.OnPrimaryClipChangedListener {
                val copiedValue: String? =
                    clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
                if (copiedValue == null) {
                    Log.d(TAG, "Copied value: null")
                } else {
                    Log.d(TAG, "Copied value: $copiedValue")
                }

                mChannel.invokeMethod(onClipboardPaste, copiedValue)

            }
            clipboardManager.addPrimaryClipChangedListener(listener)
        }

        result.success(null)
    }


    private fun initializeScanner(resultTypeIndex: Int, result: Result) {
        if (mScanner != null) {
            result.success(false)

        } else {
            mScanner = ScanManager()


            when (resultTypeIndex) {
                0 -> {
                    mScanner!!.aDecodeSetResultType(ScanConst.RESULT_USERMSG)
                    mScanResultReceiver = ScanResultReceiver()

                    val filter = IntentFilter()
                    filter.addAction(ScanConst.INTENT_USERMSG)
                    mContext.registerReceiver(mScanResultReceiver, filter)
                    mDecodeResult = DecodeResult()
                }

                1 -> mScanner!!.aDecodeSetResultType(ScanConst.RESULT_COPYPASTE)
                2 -> {
//                    mScanner!!.aDecodeSetResultType(ScanConst.RESULT_KBDMSG)
                }

                3 -> {
//                    mScanner!!.aDecodeSetResultType(ScanConst.RESULT_EVENT)
                }

                4 -> {
//                    mScanner!!.aDecodeSetResultType(ScanConst.RESULT_CUSTOM_INTENT)
//                    mScanResultReceiver = ScanResultReceiver()
//                    val filter = IntentFilter()
//                    filter.addAction(ScanConst.INTENT_EVENT)
//                    filter.addCategory(customIntentCategory)
//                    mContext.registerReceiver(mScanResultReceiver, filter)
//                    mDecodeResult = DecodeResult()
                }

                else -> Log.d(TAG, "Undefined Result Type")
            }


            result.success(true)
        }
    }

    private fun isDevicePointMobile(): Boolean {
        var majorNumber = ""
        try {
            majorNumber = DeviceServer.getIDeviceService().majorNumber.toString()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return majorNumber != ""
    }


}
