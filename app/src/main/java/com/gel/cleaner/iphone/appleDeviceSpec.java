public class AppleDeviceSpec {

    public String soc;
    public String cpu;
    public String ram;
    public String screen;
    public String camera;
    public String bluetooth;
    public String wifi;
    public String biometrics;
    public String port;
    public String charging;
    public String os;

    public AppleDeviceSpec(
            String soc, String cpu, String ram, String screen,
            String camera, String bluetooth, String wifi,
            String biometrics, String port, String charging, String os) {

        this.soc = soc;
        this.cpu = cpu;
        this.ram = ram;
        this.screen = screen;
        this.camera = camera;
        this.bluetooth = bluetooth;
        this.wifi = wifi;
        this.biometrics = biometrics;
        this.port = port;
        this.charging = charging;
        this.os = os;
    }

    public static AppleDeviceSpec unknown() {
        return new AppleDeviceSpec(
            "Unknown", "Unknown", "Unknown", "Unknown",
            "Unknown", "Unknown", "Unknown",
            "Unknown", "Unknown", "Unknown", "Unknown"
        );
    }
}
