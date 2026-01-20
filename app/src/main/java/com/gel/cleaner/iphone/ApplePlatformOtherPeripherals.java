// ApplePlatformOtherPeripherals.java
package com.gel.cleaner.iphone;

public final class ApplePlatformOtherPeripherals {

    private ApplePlatformOtherPeripherals() {}

    public static String getOtherPeripherals() {
        return
            line("IR / Proximity Sensor", "Yes (system-level)") +
            line("Hall Sensor", "Yes (used by cover & magnets)") +
            line("FM Radio", "No (hardware absent)") +
            line("IR Blaster", "No") +
            line("TV Tuner", "No") +
            line("Barcode Scanner", "Via camera") +
            line("Hardware Keyboard", "External (Bluetooth)") +
            line("Wireless Charging", "Model dependent") +
            note("Some peripherals exist internally but are not exposed to apps.");
    }

    private static String line(String k, String v) {
        return "• " + k + " : " + v + "<br>";
    }

    private static String note(String v) {
        return "<br><i>" + v + "</i>";
    }
}
