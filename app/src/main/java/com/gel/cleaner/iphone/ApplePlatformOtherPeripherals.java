// ApplePlatformOtherPeripherals.java
package com.gel.cleaner.iphone;

public final class ApplePlatformOtherPeripherals {

    private ApplePlatformOtherPeripherals() {}

    public static String get() {
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
        return
            "<font color=\"#FFFFFF\"><b>• " + k + ":</b></font> " +
            "<font color=\"#00FF7F\">" + v + "</font><br>";
    }

    private static String note(String v) {
        return "<br><i><font color=\"#AAAAAA\">" + v + "</font></i>";
    }
}
