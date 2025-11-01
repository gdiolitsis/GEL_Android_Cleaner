document.addEventListener("deviceready", () => {
    console.log("✅ Device ready — GEL");

    const btn = document.getElementById("cleanBtn");
    if (!btn) {
        console.log("⚠️ cleanBtn not found");
        return;
    }

    btn.addEventListener("click", () => {
        if (!window.gelcleaner) {
            console.log("❌ GELCleaner plugin not loaded");
            return;
        }

        gelcleaner.clean(
            () => console.log("✅ Native clean OK"),
            (err) => console.log("❌ Native clean ERR:", err)
        );
    });
});
