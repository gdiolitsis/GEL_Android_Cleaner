function logLine(...a){
  const el = document.getElementById("log");
  el.textContent += a.map(x => (typeof x==='string'?x:JSON.stringify(x))).join(" ") + "\\n";
  el.scrollTop = el.scrollHeight;
}
function setStatus(t){ document.getElementById("status").textContent = t; }
function setProgress(p){ document.getElementById("progressBar").style.width = Math.max(0,Math.min(100,p))+"%"; }

let cpuTimer = null;
const cpuBuf = new Array(60).fill(0);

function drawCPU(v){
  cpuBuf.push(v); cpuBuf.shift();
  const c = document.getElementById("cpuCanvas");
  const ctx = c.getContext("2d");
  ctx.clearRect(0,0,c.width,c.height);
  ctx.strokeStyle = "#d4af37"; ctx.lineWidth = 2;
  ctx.beginPath();
  cpuBuf.forEach((val,i) => {
    const x = i*(c.width/(cpuBuf.length-1));
    const y = c.height - (val/100)*c.height;
    i===0 ? ctx.moveTo(x,y) : ctx.lineTo(x,y);
  });
  ctx.stroke();
  ctx.fillStyle="#777";
  ctx.font="12px sans-serif";
  ctx.fillText(cpuBuf[cpuBuf.length-1].toFixed(0)+"%", 10, 16);
}

document.addEventListener("deviceready", () => {
  logLine("✅ Device ready");

  byId("btnFullAccess").onclick = () => gel.requestAllFiles(
    r => logLine("📂 Full access:", r),
    e => logLine("❌ Full access error:", e)
  );

  byId("btnCpu").onclick = () => gel.cpuInfo(
    r => { logLine("🔥 CPU:", r); },
    e => logLine("❌ CPU error:", e)
  );

  byId("btnCpuLive").onclick = () => {
    if (cpuTimer){ clearInterval(cpuTimer); cpuTimer=null; setStatus("CPU live: stopped"); return; }
    setStatus("CPU live: running…");
    cpuTimer = setInterval(() => gel.cpuInfo(
      r => { const pct = r && r.percent ? r.percent : (Math.random()*30+20); drawCPU(pct); },
      _ => { /* ignore */ }
    ), 1000);
  };

  byId("btnRam").onclick = () => {
    setStatus("Cleaning RAM…"); setProgress(10);
    gel.cleanRAM(
      r => { setProgress(100); setStatus("RAM cleaned ✓"); logLine("⚡ RAM:", r); setTimeout(()=>setProgress(0),400); },
      e => { setStatus("RAM error"); logLine("❌ RAM:", e); }
    );
  };

  const runClean = (mode) => {
    let steps = mode==="aggressive" ? [10,35,60,80,100] : [15,45,80,100];
    setStatus((mode==="aggressive"?"Aggressive":"Safe")+" clean…");
    let i=0; const bump = () => { if (i<steps.length){ setProgress(steps[i++]); setTimeout(bump, 400); } };
    bump();
    gel.clean(mode,
      r => { setStatus("Clean done ✓"); logLine(mode==="aggressive"?"🧨 Aggressive:":"🧹 Safe:", r); setTimeout(()=>setProgress(0),500); },
      e => { setStatus("Clean error"); logLine("❌ Clean:", e); }
    );
  };

  byId("btnCleanSafe").onclick = () => runClean("safe");
  byId("btnCleanAggro").onclick = () => runClean("aggressive");

  byId("btnCleanMedia").onclick = () => {
    setStatus("Cleaning media junk…"); setProgress(20);
    gel.cleanMedia(
      r => { setProgress(100); setStatus("Media cleaned ✓"); logLine("🖼 Media:", r); setTimeout(()=>setProgress(0),400); },
      e => { setStatus("Media clean error"); logLine("❌ Media:", e); }
    );
  };

  byId("btnCleanBrowser").onclick = () => {
    setStatus("Cleaning browser caches…"); setProgress(25);
    gel.cleanBrowser(
      r => { setProgress(100); setStatus("Browser cleaned ✓"); logLine("🌐 Browser:", r); setTimeout(()=>setProgress(0),400); },
      e => { setStatus("Browser clean error"); logLine("❌ Browser:", e); }
    );
  };

  byId("btnCleanApp").onclick = () => {
    const pkg = byId("pkgInput").value.trim();
    if (!pkg){ logLine("ℹ️ Enter package name, e.g. com.android.chrome"); return; }
    setStatus("Per-App clean: "+pkg); setProgress(30);
    gel.cleanApp(pkg,
      r => { setProgress(100); setStatus("Per-App cleaned ✓"); logLine("📦 App:", r); setTimeout(()=>setProgress(0),400); },
      e => { setStatus("Per-App error"); logLine("❌ App:", e); }
    );
  };
});

function byId(id){ return document.getElementById(id); }

// =======================
// DONATE BTN
// =======================
byId("donateBig").onclick = () => {
  window.open(
    "https://www.paypal.com/donate?business=gdiolitsis@yahoo.com",
    "_system"
  );
};

function cleanAll() {
  log("Running FULL CLEAN...");
  safeClean();
  aggClean();
  mediaJunk();
  browserCache();
  tempCleaner();
  cleanRam();
  batteryBoost();
  log("✅ FULL CLEAN DONE!");
}
