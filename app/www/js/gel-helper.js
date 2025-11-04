// GDiolitsis Engine Lab (GEL)
// gel-helper.js — Lightweight Accessibility Helper
// ------------------------------------------------ +
// • ΔΕΝ αντικαθιστά το gelcleaner.js
// • ΔΕΝ επηρεάζει build / signing
// • Μόνο UI + help + openAccessibilitySettings() wrapper
// ------------------------------------------------ +

(function () {

  // Show modal only once
  function shouldShow() {
    try { return !localStorage.getItem("gel_help_shown"); }
    catch (_) { return true; }
  }
  function markShown() {
    try { localStorage.setItem("gel_help_shown", "1"); }
    catch (_) {}
  }

  // Create modal
  function makeModal() {
    if (document.getElementById("gel-help-modal")) return;

    const html = `
      <div id="gel-help-modal" style="
        position: fixed;
        inset: 0;
        background: rgba(0,0,0,.55);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 99999;
      ">
        <div style="
          background:#111;
          color:#eee;
          padding:20px;
          border-radius:14px;
          width:88%;
          max-width:420px;
          border:2px solid #d4af37;
          box-shadow:0 0 20px #000;
        ">
          <h2 style="margin-top:0;color:#d4af37;text-align:center">
            Enable Accessibility
          </h2>
          <p>To allow cleaning automation, enable Accessibility permissions:</p>
          <ol>
            <li>Tap <b>Open Settings</b></li>
            <li>Find <b>GEL Cleaner</b></li>
            <li>Switch it <b>ON</b></li>
          </ol>

          <button id="gel-open-acc" style="
            width:100%;margin:12px 0;
            padding:10px;
            background:#d4af37;
            border:none;border-radius:8px;
            font-size:16px;color:#000;
          ">Open Settings</button>

          <button id="gel-close" style="
            width:100%;
            padding:10px;
            background:#333;
            border:none;border-radius:8px;
            color:#eee;
          ">Close</button>
        </div>
      </div>
    `;

    const box = document.createElement("div");
    box.innerHTML = html;
    document.body.appendChild(box);

    // Open system settings
    document.getElementById("gel-open-acc").onclick = () => {
      try {
        if (window.GELCleaner && typeof window.GELCleaner.openAccessibilitySettings === "function") {
          window.GELCleaner.openAccessibilitySettings();
        } else {
          alert("Open Settings unavailable.");
        }
      } catch (_) {}
    };

    // Close modal
    document.getElementById("gel-close").onclick = () => {
      document.getElementById("gel-help-modal").remove();
    };
  }

  // Public trigger
  function showAccessibilityHelp() {
    makeModal();
    markShown();
  }

  // Automatic single-time hint
  function autoBoot() {
    if (shouldShow()) {
      // delay 1s so UI loads fully
      setTimeout(() => {
        showAccessibilityHelp();
      }, 1000);
    }
  }

  // Device ready → auto
  document.addEventListener("deviceready", autoBoot);

  // Export clean namespace
  window.GELHelper = {
    show: showAccessibilityHelp
  };

})();
