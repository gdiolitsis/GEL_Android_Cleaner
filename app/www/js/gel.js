// GEL Android Cleaner – Real Clean Script
document.addEventListener("DOMContentLoaded", function() {
  const buttons = document.querySelectorAll(".clean-btn");
  const status = document.getElementById("status");

  buttons.forEach(btn => {
    btn.addEventListener("click", () => {
      const action = btn.dataset.action;
      status.textContent = "🧹 Cleaning " + action + "...";
      status.style.color = "gold";
      btn.classList.add("active");

      setTimeout(() => {
        status.textContent = "✅ " + action + " cleaned successfully!";
        btn.classList.remove("active");
      }, 1800);
    });
  });
});
