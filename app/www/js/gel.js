/* =========================================================
   GEL Android Cleaner — Dark-Gold Edition v3.2 (Demo Mode)
   Author: GDiolitsis Engine Lab (GEL)
   ========================================================= */

// 🔹 Εμφάνιση animated μηνύματος στο κέντρο της οθόνης
function showMessage(msg) {
  const overlay = document.createElement('div');
  overlay.textContent = msg;
  overlay.style.position = 'fixed';
  overlay.style.top = '50%';
  overlay.style.left = '50%';
  overlay.style.transform = 'translate(-50%, -50%) scale(0.9)';
  overlay.style.background = 'rgba(20, 20, 20, 0.95)';
  overlay.style.color = '#ffd700';
  overlay.style.padding = '18px 35px';
  overlay.style.border = '1px solid #d4af37';
  overlay.style.borderRadius = '12px';
  overlay.style.boxShadow = '0 0 20px #d4af37';
  overlay.style.fontSize = '16px';
  overlay.style.zIndex = '9999';
  overlay.style.opacity = '0';
  overlay.style.transition = 'all 0.3s ease';
  document.body.appendChild(overlay);

  setTimeout(() => overlay.style.opacity = '1', 50);
  setTimeout(() => {
    overlay.style.opacity = '0';
    overlay.style.transform = 'translate(-50%, -50%) scale(0.8)';
    setTimeout(() => overlay.remove(), 300);
  }, 1800);
}

// 🔹 Mock actions — εμφανίζουν ειδοποιήσεις και logs
function cleanCache() {
  console.log("🧠 Cleaning cache...");
  showMessage("🧠 Cache Cleaned Successfully!");
}

function boostRAM() {
  console.log("⚡ Boosting RAM...");
  showMessage("⚡ RAM Boost Completed!");
}

function removeJunk() {
  console.log("🗑️ Removing junk files...");
  showMessage("🗑️ Junk Files Removed!");
}

function clearTemp() {
  console.log("🔥 Clearing temporary files...");
  showMessage("🔥 Temp Folder Cleared!");
}

function optimizeBattery() {
  console.log("🔋 Optimizing battery usage...");
  showMessage("🔋 Battery Optimized!");
}

function killProcesses() {
  console.log("🚀 Killing background processes...");
  showMessage("🚀 Background Processes Stopped!");
}

// 🔹 Μικρό εφέ κατά το άνοιγμα της εφαρμογής
document.addEventListener('DOMContentLoaded', () => {
  console.log("✅ GEL Android Cleaner v3.2 initialized successfully.");
  setTimeout(() => showMessage("✨ GEL Cleaner Ready"), 600);
});
