import Foundation
import CoreBluetooth
import YCProductSDK

@objc public class iOSBleManager: NSObject {
    public static let shared = iOSBleManager()

    private var found: [CBPeripheral] = []
    private var scanCallback: (([String], [String]) -> Void)? = nil

    @objc public func scan(completion: @escaping ([String], [String]) -> Void) {
        scanCallback = completion
        YCProduct.filterProductID = []
        YCProduct.scanningDevice { devices, error in
            self.found = devices
            let names = devices.map { $0.name ?? "Unknown" }
            let ids = devices.map { $0.identifier.uuidString }
            completion(names, ids)
        }
    }

    @objc public func connect(index: Int, completion: @escaping (Bool) -> Void) {
        guard index < found.count else {
            completion(false)
            return
        }
        YCProduct.connectDevice(found[index]) { state, _ in
            completion(state == .connected)
        }
    }

    @objc public func disconnect() {
        YCProduct.disconnectDevice(nil, completion: nil)
    }
}