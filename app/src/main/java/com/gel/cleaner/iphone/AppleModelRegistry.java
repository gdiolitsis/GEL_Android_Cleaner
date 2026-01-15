import java.util.HashMap;
import java.util.Map;

public final class AppleModelRegistry {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        // ======================
        // 📱 iPHONE
        // ======================
        DB.put("iPhone 8",  new AppleDeviceSpec(
                "Apple A11 Bionic",
                "2 GB",
                "4.7\" Retina HD",
                "12 MP",
                "Bluetooth 5.0",
                "Wi-Fi 5",
                "Touch ID",
                "Lightning",
                "Qi Wireless",
                "iOS 16"
        ));

        DB.put("iPhone X", new AppleDeviceSpec(
                "Apple A11 Bionic",
                "3 GB",
                "5.8\" OLED",
                "12 MP Dual",
                "Bluetooth 5.0",
                "Wi-Fi 5",
                "Face ID",
                "Lightning",
                "Qi Wireless",
                "iOS 16"
        ));

        DB.put("iPhone 11", new AppleDeviceSpec(
                "Apple A13 Bionic",
                "4 GB",
                "6.1\" Liquid Retina",
                "12 MP Dual",
                "Bluetooth 5.0",
                "Wi-Fi 6",
                "Face ID",
                "Lightning",
                "Qi Wireless",
                "iOS 17"
        ));

        DB.put("iPhone 13", new AppleDeviceSpec(
                "Apple A15 Bionic",
                "4 GB",
                "6.1\" OLED",
                "12 MP Dual",
                "Bluetooth 5.0",
                "Wi-Fi 6",
                "Face ID",
                "Lightning",
                "MagSafe",
                "iOS 17"
        ));

        DB.put("iPhone 14", new AppleDeviceSpec(
                "Apple A15 Bionic",
                "6 GB",
                "6.1\" OLED",
                "12 MP Dual",
                "Bluetooth 5.3",
                "Wi-Fi 6",
                "Face ID",
                "Lightning",
                "MagSafe",
                "iOS 17"
        ));

        DB.put("iPhone 15", new AppleDeviceSpec(
                "Apple A16 Bionic",
                "6 GB",
                "6.1\" OLED",
                "48 MP",
                "Bluetooth 5.3",
                "Wi-Fi 6E",
                "Face ID",
                "USB-C",
                "MagSafe",
                "iOS 17"
        ));

        // ======================
        // 📲 iPAD
        // ======================
        DB.put("iPad 9", new AppleDeviceSpec(
                "Apple A13 Bionic",
                "3 GB",
                "10.2\" Retina",
                "8 MP",
                "Bluetooth 4.2",
                "Wi-Fi 5",
                "Touch ID",
                "Lightning",
                "Smart Connector",
                "iPadOS 17"
        ));

        DB.put("iPad Air 5", new AppleDeviceSpec(
                "Apple M1",
                "8 GB",
                "10.9\" Liquid Retina",
                "12 MP",
                "Bluetooth 5.0",
                "Wi-Fi 6",
                "Touch ID",
                "USB-C",
                "Apple Pencil 2",
                "iPadOS 17"
        ));

        DB.put("iPad Pro 11", new AppleDeviceSpec(
                "Apple M2",
                "8–16 GB",
                "11\" Liquid Retina",
                "12 MP + LiDAR",
                "Bluetooth 5.3",
                "Wi-Fi 6E",
                "Face ID",
                "USB-C / Thunderbolt",
                "Apple Pencil 2",
                "iPadOS 17"
        ));
    }

    public static AppleDeviceSpec get(String model) {
        AppleDeviceSpec s = DB.get(model);
        return s != null ? s : AppleDeviceSpec.unknown(model);
    }
}
