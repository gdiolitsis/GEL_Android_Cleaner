private long lastIdle = 0;
private long lastTotal = 0;

private String readCpuLoad() {
    try {
        BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
        String line = br.readLine();   // cpu  3357 0 4313 136239 0 0 0 0 0 0
        br.close();

        if (line == null || !line.startsWith("cpu")) return "N/A";

        String[] parts = line.trim().split("\\s+");

        long user = Long.parseLong(parts[1]);
        long nice = Long.parseLong(parts[2]);
        long system = Long.parseLong(parts[3]);
        long idle = Long.parseLong(parts[4]);

        long total = user + nice + system + idle;

        long diffIdle = idle - lastIdle;
        long diffTotal = total - lastTotal;

        lastIdle = idle;
        lastTotal = total;

        if (diffTotal == 0) return "0%";

        int usage = (int) ((diffTotal - diffIdle) * 100L / diffTotal);

        if (usage < 0) usage = 0;
        if (usage > 100) usage = 100;

        return usage + "%";

    } catch (Exception e) {
        return "N/A";
    }
}
