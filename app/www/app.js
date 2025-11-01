function log(...a){
  const el = document.getElementById("log");
  el.textContent += a.map(x => (typeof x==='string'?x:JSON.stringify(x))).join(" ") + "\n";
}

document.addEventListener("deviceready", () => {
  log("✅ Device ready");

  byId("btnFullAccess").onclick = () => gel.requestAllFiles(
    r => log("📂 Full access:", r),
    e => log("❌ Full access error:", e)
  );

  byId("btnCpu").onclick = () => gel.cpuInfo(
    r => log("🔥 CPU:", r),
    e => log("❌ CPU error:", e)
  );

  byId("btnRam").onclick = () => gel.cleanRAM(
    r => log("⚡ RAM cleaned:", r),
    e => log("❌ RAM error:", e)
  );

  byId("btnCleanSafe").onclick = () => gel.clean("safe",
    r => log("🧹 Safe clean:", r),
    e => log("❌ Safe clean error:", e)
  );

  byId("btnCleanAggro").onclick = () => gel.clean("aggressive",
    r => log("🧨 Aggressive clean:", r),
    e => log("❌ Aggressive clean error:", e)
  );
});

function byId(id){ return document.getElementById(id); }
