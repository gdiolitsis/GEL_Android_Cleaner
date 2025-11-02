// ======================================================================
// GDiolitsis Engine Lab (GEL)
// app.js — Stable Classic WebView Edition
// ======================================================================

document.addEventListener("DOMContentLoaded", () => {
  console.log("✅ GEL app.js loaded");

  // Register UI buttons
  bindButtons();
});

// ======================================================================
// BUTTON EVENTS
// ======================================================================
function bindButtons() {
  const buttons = document.querySelectorAll("[data-action]");

  buttons.forEach(btn => {
    btn.addEventListener("click", () => {
      const action = btn.getAttribute("data-action");
      handleAction(action);
    });
  });
}

// ======================================================================
// ACTION HANDLER
// ======================================================================
function handleAction(action) {
  switch (action) {
    case "clear-cache":
      onClearCache();
      break;

    case "open-folder":
      onOpenFolder();
      break;

    case "compress-data":
      onCompressData();
      break;

    case "scan-junk":
      onScanJunk();
      break;

    case "optimize":
      onOptimize();
      break;

    default:
      console.warn("⚠️ Unknown action →", action);
  }
}

// ======================================================================
// FEATURE ACTIONS
// ======================================================================
function onClearCache() {
  toast("Clearing cache…");
}

function onOpenFolder() {
  toast("Opening folder…");
}

function onCompressData() {
  toast("Compressing…");
}

function onScanJunk() {
  toast("Scanning junk…");
}

function onOptimize() {
  toast("Optimizing…");
}

// ======================================================================
// Toast
// ======================================================================
function toast(msg) {
  console.log("[Toast] " + msg);
  const box = document.getElementById("toast");
  if (!box) return;

  box.innerText = msg;
  box.classList.add("show");

  setTimeout(() => {
    box.classList.remove("show");
  }, 2000);
}

// ======================================================================
// END
// ======================================================================
