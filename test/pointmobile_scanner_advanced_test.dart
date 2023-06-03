import 'package:flutter_test/flutter_test.dart';
import 'package:pointmobile_scanner_advanced/pointmobile_scanner_advanced.dart';
import 'package:pointmobile_scanner_advanced/pointmobile_scanner_advanced_platform_interface.dart';
import 'package:pointmobile_scanner_advanced/pointmobile_scanner_advanced_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPointmobileScannerAdvancedPlatform
    with MockPlatformInterfaceMixin
    implements PointmobileScannerAdvancedPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PointmobileScannerAdvancedPlatform initialPlatform = PointmobileScannerAdvancedPlatform.instance;

  test('$MethodChannelPointmobileScannerAdvanced is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPointmobileScannerAdvanced>());
  });

  test('getPlatformVersion', () async {
    PointmobileScannerAdvanced pointmobileScannerAdvancedPlugin = PointmobileScannerAdvanced();
    MockPointmobileScannerAdvancedPlatform fakePlatform = MockPointmobileScannerAdvancedPlatform();
    PointmobileScannerAdvancedPlatform.instance = fakePlatform;

    expect(await pointmobileScannerAdvancedPlugin.getPlatformVersion(), '42');
  });
}
