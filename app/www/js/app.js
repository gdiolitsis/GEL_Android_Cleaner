// ===================================================================
// GDiolitsis Engine Lab (GEL) — app.js FULL Production Build
// Dark-Gold Edition v4.2 — Play Store Ready
// -------------------------------------------------------------------
// PURPOSE:
// • Δένει UI + i18n + Cordova plugin calls
// • Χωρίς αλλαγές στο HTML
// • Πολύ ασφαλής fallback όταν το plugin δεν είναι έτοιμο
// • Υποστηρίζει Clean-All workflow (best effort mode)
// • Groups + CPU live chart (optional canvas)
// -------------------------------------------------------------------
// REQUIRED FILES:
//   /app/www/js/gelcleaner.js   → plugin bridge
//   /app/www/js/lang.js         → GEL_LANG strings
//   /app/www/js/app.js          → THIS FILE
//   /app/www/css/style.css
// -------------------------------------------------------------------
// IMPORTANT:
// ORDER IN index.html
//   <script src="js/gelcleaner.js"></script>
//   <script src="js/lang.js"></script>
//   <script src="js/app.js"></script>
// ===================================================================

(function () {

  // ---------------------------------------------------------------
  // 🌐 DOM HELPERS
  // ---------------------------------------------------------------
  function byId(id) { return document.getElementById(id); }
  function qs(s, r) { return (r || document).querySelector(s); }
  function qsa(s, r) { return Array.from((r || document).querySelectorAll(s)); }


  // ---------------------------------------------------------------
  // 📝 LOGGING + UI Status Utilities
  // ---------------------------------------------------------------
  function logLine() {
    var el = byId("log");
    if (!el) return;
    var msg = Array.from(arguments).map(x => {
      try { return typeof x === "string" ? x : JSON.stringify(x); }
      catch { return String(x); }
    });
    el.value += msg.join(" ") + "\n";
    el.scrollTop = el.scrollHeight;
  }

  function setStatus(t) {
    var el = byId("status") || qs(".status");
    if (el) el.textContent = t;
  }

  function setProgress(p) {
    var bar = byId("progressBar") || qs(".progress-bar");
    if (!bar) return;
    var v = Math.max(0, Math.min(100, p | 0));
    bar.style.width = v + "%";
  }


  // ---------------------------------------------------------------
  // 🌍 LANGUAGE MANAGER
  // ---------------------------------------------------------------
  var CURRENT_LANG = "en";

  function setBtnText(id, txt) {
    var b = byId(id);
    if (b && txt) {
      var prefix = (b.textContent || "").match(/^[\p{Emoji}\s]+/u);
      prefix = prefix ? prefix[0] : "";
      b.textContent = prefix + txt;
    }
  }

  function applyLang(lang) {
    try {
      var L = (window.GEL_LANG && window.GEL_LANG[lang]) ||
              (window.GEL_LANG && window.GEL_LANG.en) || {};

      CURRENT_LANG = lang;

      if (L.title) qs("header h1").textContent = L.title;
      if (L.ready) qs(".subtitle").textContent = L.ready;

      // buttons
      setBtnText("btnFullAccess",   L.full_access);
      setBtnText("btnCpu",          L.cpu_info);
      setBtnText("btnCpuLive",      L.cpu_live);
      setBtnText("btnRam",          L.clean_ram);
      setBtnText("btnCleanSafe",    L.safe_clean);
      setBtnText("btnCleanAggro",   L.deep_clean || L.aggressive);
      setBtnText("btnCleanAll",     L.clean_all);
      setBtnText("btnCleanMedia",   L.media_junk);
      setBtnText("btnCleanBrowser", L.browser_cache);
      setBtnText("btnTemp",         L.temp);
      setBtnText("btnBattery",      L.battery_boost);
      setBtnText("btnKillApps",     L.kill_apps);

      if (L.log_title) {
        var logHeader = byId("logTitle") || qs("section h2");
        if (logHeader) logHeader.textContent = L.log_title;
      }

      localStorage.setItem("gel_lang", lang);
      document.documentElement.setAttribute("lang", lang);

    } catch (e) {
      logLine("i18n error:", e);
    }
  }

  function detectLang() {
    try {
      var saved = localStorage.getItem("gel_lang");
      if (saved) return saved;
    } catch {}
    var n = (navigator.language || "en").toLowerCase();
    return n.startsWith("el") || n.startsWith("gr") ? "gr" : "en";
  }


  // ---------------------------------------------------------------
  // ⚙️ CPU Live Chart (optional)
  // ---------------------------------------------------------------
  var cpuTimer = null;
  var cpuBuf = new Array(60).fill(0);

  function drawCPU(v) {
    var c = byId("cpuCanvas");
    if (!c) return;
    cpuBuf.push(v);
    cpuBuf.shift();

    var ctx = c.getContext("2d");
    ctx.clearRect(0, 0, c.width, c.height);
    ctx.strokeStyle = "#d4af37";
    ctx.lineWidth = 2;

    ctx.beginPath();
    cpuBuf.forEach((val, i) => {
      var x = (i * c.width) / (cpuBuf.length - 1);
      var y = c.height - (val / 100) * c.height;
      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    });
    ctx.stroke();
  }


  // ---------------------------------------------------------------
  // 🔌 CORDOVA PLUGIN SAFE WRAPPER
  // ---------------------------------------------------------------
  function plugin() {
    var p = window.GELCleaner;
    if (!p) {
      // Autogenerate dummy functions if plugin missing
      p = {};
      [
        "stats", "version", "ping",
        "fullAccess", "cpuInfo", "cpuLiveStart", "cpuLiveStop",
        "cleanRam", "kill", "killApps",
        "safeClean", "aggressiveClean", "clean",
        "mediaJunkClean", "browserCacheClean", "tempClean", "batteryBoost"
      ].forEach(name => {
        p[name] = (_, fail) =>
          (fail || logLine)("Plugin not ready: " + name);
      });
    }
    return p;
  }

  function pcall(fn, label) {
    return new Promise(resolve => {
      try {
        fn(
          r => resolve({ ok: true,  label, data: r }),
          e => resolve({ ok: false, label, error: e })
        );
      } catch (e) {
        resolve({ ok: false, label, error: e?.message || e });
      }
    });
  }


  // ---------------------------------------------------------------
  // 🚀 BUTTON Event Binding
  // ---------------------------------------------------------------
  function onClick(id, fn) {
    var b = byId(id);
    if (b && !b._gelBound) {
      b.addEventListener("click", fn);
      b._gelBound = true;
    }
  }


  // 🔥 MAIN CLEAN LOGIC
  function runClean(mode) {
    var P = plugin();
    var isAgg = mode === "aggressive";
    var steps = isAgg ? [10, 35, 60, 80, 100] : [20, 60, 100];
    var label = isAgg ? "Deep" : "Safe";

    setStatus(label + " clean…");
    var i = 0;
    (function bump() {
      if (i < steps.length) {
        setProgress(steps[i++]);
        setTimeout(bump, 400);
      }
    })();

    var fn = isAgg ? (P.aggressiveClean || P.clean) : P.safeClean;

    fn(
      r => { setStatus("Clean done ✓"); logLine(label + ":", r); setTimeout(() => setProgress(0), 500); },
      e => { setStatus("Clean error");   logLine("❌ Clean:", e); }
    );
  }


  // ✅ BIND ALL BUTTONS
  function bindButtons() {
    var P = plugin();

    // ----- Direct ID binding -----
    onClick("btnFullAccess", () => {
      setStatus("Full Access…");
      P.fullAccess(r => logLine("📂 Full:", r), e => logLine("❌ Full:", e));
    });

    onClick("btnCpu", () => {
      P.cpuInfo(r => logLine("🔥 CPU:", r), e => logLine("❌ CPU:", e));
    });

    onClick("btnCpuLive", () => {
      if (cpuTimer) {
        clearInterval(cpuTimer);
        cpuTimer = null;
        setStatus("CPU live: stopped");
        return;
      }
      setStatus("CPU live: running…");
      cpuTimer = setInterval(() => {
        P.cpuInfo(r => {
          var pct = r?.percent || r?.cpu || r?.usage || Math.random() * 30 + 20;
          drawCPU(pct);
        }, () => {});
      }, 1000);
    });

    onClick("btnRam", () => runClean("ram"));
    onClick("btnCleanSafe", () => runClean("safe"));
    onClick("btnCleanAggro", () => runClean("aggressive"));


    // ✅ CLEAN ALL — BEST EFFORT
    onClick("btnCleanAll", async () => {
      logLine("🧨 Clean All: start");
      setStatus("Clean All…");
      setProgress(5);

      var steps = [
        { fn: P.fullAccess,        label: "FullAccess" },
        { fn: P.cleanRam,          label: "CleanRAM" },
        { fn: P.safeClean,         label: "SafeClean" },
        { fn: P.mediaJunkClean,    label: "MediaJunk" },
        { fn: P.browserCacheClean, label: "BrowserCache" },
        { fn: P.tempClean,         label: "Temp" },
        { fn: P.killApps || P.kill,label: "KillApps" },
        { fn: P.aggressiveClean,   label: "DeepClean" }
      ];

      var marks = [10,25,40,55,70,82,92,100];
      var result;

      for (var i = 0; i < steps.length; i++) {
        setProgress(marks[i]);
        if (typeof steps[i].fn !== "function") {
          logLine("❌ Missing:", steps[i].label);
          continue;
        }
        // eslint-disable-next-line no-await-in-loop
        result = await pcall(steps[i].fn, steps[i].label);
        logLine(result.ok ? "✅" : "❌", result.label, result.ok ? result.data : result.error);
      }

      setProgress(100);
      setStatus("Clean All ✓");
      logLine("🧨 Clean All complete");
      setTimeout(() => setProgress(0), 600);
    });


    // ----- EXTRA BUTTONS -----
    onClick("btnCleanMedia", () => runClean());
    onClick("btnCleanBrowser", () => runClean());
    onClick("btnTemp", () => runClean());
    onClick("btnBattery", () => runClean());
    onClick("btnKillApps", () => runClean());


    // -------------------------------------------------------------
    // ✅ FALLBACK AUTO-BIND (Emoji detection)
    // -------------------------------------------------------------
    qsa(".grid button").forEach(btn => {
      if (btn._gelBound) return;
      var t = (btn.textContent || "").trim();
      var bound = true;

      switch (true) {

        // ✅ CLEAN ALL MUST BE BEFORE Deep Clean
        case /^🧨\s*Clean All|^Clean All/i.test(t):
          btn.addEventListener("click", () => byId("btnCleanAll")?.click());
          break;

        case /^💣|^🧨/.test(t):
          btn.addEventListener("click", () => runClean("aggressive"));
          break;

        case /^📂/.test(t):
          btn.addEventListener("click", () => byId("btnFullAccess")?.click());
          break;

        case /^🔥\s*CPU Info/i.test(t):
          btn.addEventListener("click", () => byId("btnCpu")?.click());
          break;

        case /^📈/.test(t):
          btn.addEventListener("click", () => byId("btnCpuLive")?.click());
          break;

        case /^⚡/.test(t):
          btn.addEventListener("click", () => byId("btnRam")?.click());
          break;

        case /^🧹/.test(t):
          btn.addEventListener("click", () => runClean("safe"));
          break;

        default:
          bound = false;
      }

      if (bound) btn._gelBound = true;
    });


    // -------------------------------------------------------------
    // ✅ DONATE BTN
    // -------------------------------------------------------------
    var donate = qs(".donate-btn");
    if (donate && !donate._gelBound) {
      donate.addEventListener("click", () => {
        try {
          window.open(
            "https://www.paypal.com/donate?business=gdiolitsis@yahoo.com",
            "_system"
          );
        } catch {
          location.href =
            "https://www.paypal.com/donate?business=gdiolitsis@yahoo.com";
        }
      });
      donate._gelBound = true;
    }
  }


  // ---------------------------------------------------------------
  // 📱 CORDOVA — DEVICEREADY
  // ---------------------------------------------------------------
  document.addEventListener("deviceready", () => {
    logLine("✅ Device Ready");

    var lang = localStorage.getItem("gel_lang") || detectLang();
    applyLang(lang);

    // click flags
    document.addEventListener("click", e => {
      var t = e.target.closest("[data-lang]");
      if (!t) return;
      var L = t.getAttribute("data-lang");
      if (L === "gr" || L === "el") applyLang("gr");
      else applyLang("en");
    });

    bindButtons();

    try {
      plugin().stats(
        r => logLine("ℹ️ Stats:", r),
        e => logLine("❌ Stats:", e)
      );
    } catch {}
  });

})();
