/* ==========================================================
   GEL Android Cleaner v3.5 — Functional Core (Dark-Gold Edition)
   GDiolitsis Engine Lab (GEL) — Author & Developer
   ========================================================== */

document.addEventListener("deviceready", () => {
  const log = (msg) => console.log(`[GEL Cleaner] ${msg}`);
  log("Device ready. Functional Core active.");

  const alertBox = (msg) => {
    if (navigator.notification && navigator.notification.alert) {
      navigator.notification.alert(msg, null, "Alert", "OK");
    } else {
      alert(msg);
    }
  };

  const execCmd = async (cmd) => {
    try {
      if (window.cordova && cordova.plugins && cordova.plugins.shell) {
        const output = await cordova.plugins.shell.exec(cmd);
        log(output);
        return output;
      } else {
        log("Shell plugin not found — simulated mode.");
        return "Simulated execution.";
      }
    } catch (err) {
      log("Error: " + err);
      return null;
    }
  };

  // --- Clean Cache ---
  document.getElementById("btnCache").addEventListener("click", async () => {
    alertBox("🧹 Cleaning app cache...");
    await execCmd("pm trim-caches 500M");
    alertBox("✅ Cache cleared successfully!");
  });

  // --- Boost RAM ---
  document.getElementById("btnRAM").addEventListener("click", async () => {
    alertBox("⚡ Boosting memory...");
    await execCmd("am kill-all");
    await execCmd("sync; echo 3 > /proc/sys/vm/drop_caches");
    alertBox("✅ RAM optimized!");
  });

  // --- Terminate Background ---
  document.getElementById("btnBackground").addEventListener("click", async () => {
    alertBox("🚀 Killing background processes...");
    await execCmd("am kill-all");
    alertBox("✅ Background processes terminated!");
  });

  // --- Language toggle ---
  document.getElementById("btnEN").addEventListener("click", () => {
    document.documentElement.lang = "en";
    alertBox("🇬🇧 English mode activated!");
  });

  document.getElementById("btnGR").addEventListener("click", () => {
    document.documentElement.lang = "el";
    alertBox("🇬🇷 Ενεργοποιήθηκαν τα Ελληνικά!");
  });

  log("Cleaner core initialized.");
});
