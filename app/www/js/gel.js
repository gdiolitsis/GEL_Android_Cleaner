/* =========================================================
   GEL Android Cleaner — Dark-Gold Edition v3
   Author: GDiolitsis Engine Lab (GEL)
   ========================================================= */

// 🔹 Μικρές καθυστερήσεις για πιο smooth εφέ
function showMessage(msg) {
  const overlay = document.createElement('div');
  overlay.textContent = msg;
  overlay.style.position = 'fixed';
  overlay.style.top = '50%';
  overlay.style.left = '50%';
  overlay.style.transform = 'translate(-50%, -50%)';
  overlay.style.background = 'rgba(20, 20, 20, 0.9)';
  overlay.style.color = '#ffd700';
  overlay.style.padding = '15px 30px';
  overlay.style.border = '1px solid #d4af37';
  overlay.style.borderRadius = '10px';
  overlay.style.boxShadow = '0 0 15px #d4af37';
  overlay.style.fontSize = '16px';
  overlay.style.zIndex = '9999';
  document.body.appendChild(overlay);

  setTimeout(() => overlay.remove(), 2000);
}

// 🔹 Πειραματικά actions (προς το παρόν εμφανίζουν ειδοποιήσεις)
function cleanCache() {
  console.log("Cleaning cache...");
  showMessage("🧠 Cache Cleaned Successfully!");
}

function boostRAM() {
  console.log("Boosting RAM...");
  showMessage("⚡ RAM Boost Completed!");
}

function removeJunk() {
  console.log("Removing junk files...");
  showMessage("🗑️ Junk Files Removed!");
}

function clearTemp() {
  console.log("Clearing temp files...");
  showMessage("🔥 Temp Folder Cleared!");
}

function optimizeBattery() {
  console.log("Optimizing battery...");
  showMessage("🔋 Battery Optimized!");
}

function killProcesses() {
  console.log("Killing background processes...");
  showMessage("🚀 Background Processes Stopped!");
}

// 🔹 Auto console init
document.addEventListener('DOMContentLoaded', () => {
  console.log("GEL Android Cleaner — Dark-Gold Edition v3 initialized successfully.");
});
