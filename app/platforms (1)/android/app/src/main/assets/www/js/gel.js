
(function () {
  function exec(action, ok, err) {
    if (!window.cordova || !cordova.exec) {
      console.warn("Cordova not ready — mock exec:", action);
      if (ok) ok("mock");
      return;
    }
    cordova.exec(ok, err, "GELCleaner", action, []);
  }

  // ---------- ACCESSIBILITY HELP MODAL ----------
  function makeModal() {
    if (document.getElementById("gel-help-modal")) return;

    const html = `
      <div id="gel-help-modal">
        <div class="gel-help-box">
          <h2>Enable Accessibility</h2>
          <p>To clean cache automatically, please enable Accessibility:</p>
          <ol>
            <li>Press <b>Open Settings</b></li>
            <li>Find <b>GEL Cleaner</b></li>
            <li>Turn it <b>ON</b></li>
          </ol>
          <button id="gel-open-acc">Open Settings</button>
          <button id="gel-close">Close</button>
        </div>
      </div>
    `;

    const div = document.createElement("div");
    div.innerHTML = html;
    document.body.appendChild(div);

    document.getElementById("gel-open-acc").onclick = () => {
      window.GELCleaner.openAccessibilitySettings();
    };
    document.getElementById("gel-close").onclick = () => {
      document.getElementById("gel-help-modal").remove();
    };
  }

  function showHelpOnce() {
    if (!localStorage.getItem("gel_help_shown")) {
      localStorage.setItem("gel_help_shown", "1");
      makeModal();
    }
  }

  // ---------- PUBLIC API ----------
  window.GELCleaner = {
    clearAppCache: function (ok, err) {
      showHelpOnce();
      exec("clearAllCache", ok, err);
    },

    boostRAM: function (ok, err) {
      alert("RAM boost is suggestion-only in normal mode.");
      if (ok) ok("RAM suggestion");
    },

    clearTemp: function (ok, err) {
      alert("Temp cleanup will run in next build step.");
      if (ok) ok("Temp");
    },

    removeJunk: function (ok, err) {
      alert("Junk scan is preview-only for now.");
      if (ok) ok("Junk");
    },

    optimizeBattery: function (ok, err) {
      alert("Battery tips shown.");
      if (ok) ok("Battery");
    },

    killBackground: function (ok, err) {
      alert("Background kill is limited on modern Android.");
      if (ok) ok("Kill");
    },

    openAccessibilitySettings: function (ok, err) {
      exec("openAccessibilitySettings", ok, err);
    }
  };
})();