// app/www/js/lang.js
(function () {
  const STR = {
    en: {
      "GEL Android Cleaner": "GEL Android Cleaner",
      "Device is ready • Dark-Gold Edition v4.0": "Device is ready • Dark-Gold Edition v4.0",
      "Full Access": "Full Access",
      "CPU Info": "CPU Info",
      "CPU Live": "CPU Live",
      "Clean RAM": "Clean RAM",
      "Safe Clean": "Safe Clean",
      "Aggressive": "Aggressive",
      "Media Junk": "Media Junk",
      "Browser Cache": "Browser Cache",
      "Temp": "Temp",
      "Battery Boost": "Battery Boost",
      "Kill Apps": "Kill Apps",
      "Donate — Keep this app free": "Donate — Keep this app free",
      "Log": "Log",
    },
    el: {
      "GEL Android Cleaner": "GEL Android Cleaner",
      "Device is ready • Dark-Gold Edition v4.0": "Συσκευή έτοιμη • Dark-Gold Edition v4.0",
      "Full Access": "Πλήρης Πρόσβαση",
      "CPU Info": "Πληροφορίες CPU",
      "CPU Live": "CPU Live",
      "Clean RAM": "Καθαρισμός RAM",
      "Safe Clean": "Ασφαλής Καθαρισμός",
      "Aggressive": "Επιθετικό",
      "Media Junk": "Άχρηστα Πολυμέσα",
      "Browser Cache": "Cache Περιηγητή",
      "Temp": "Προσωρινά",
      "Battery Boost": "Ενίσχυση Μπαταρίας",
      "Kill Apps": "Κλείσιμο Εφαρμογών",
      "Donate — Keep this app free": "Donate — Κράτα την εφαρμογή δωρεάν",
      "Log": "Καταγραφή",
    }
  };

  function detectLang() {
    const saved = localStorage.getItem("gel_lang");
    if (saved) return saved;
    const n = (navigator.language || "en").toLowerCase();
    return n.startsWith("el") ? "el" : "en";
  }

  function textNodes(root) {
    const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, null, false);
    const nodes = [];
    let n;
    while ((n = walker.nextNode())) {
      const t = (n.nodeValue || "").trim();
      if (!t) continue;
      // αγνοούμε input values / scripts
      if (!n.parentElement) continue;
      if (n.parentElement.tagName === "SCRIPT" || n.parentElement.tagName === "STYLE") continue;
      nodes.push(n);
    }
    return nodes;
  }

  function apply(lang) {
    const map = STR[lang] || STR.en;

    // 1) data-i18n="key"
    document.querySelectorAll("[data-i18n]").forEach(el => {
      const key = el.getAttribute("data-i18n");
      if (map[key]) el.textContent = map[key];
    });

    // 2) direct text replacement (fallback) – ταιριάζει τα αγγλικά ορατά strings
    const enMap = STR.en;
    const nodes = textNodes(document.body);
    nodes.forEach(node => {
      const txt = node.nodeValue.trim();
      if (enMap[txt] && map[txt] && node.nodeValue.trim() !== map[txt]) {
        node.nodeValue = node.nodeValue.replace(txt, map[txt]);
      }
    });

    localStorage.setItem("gel_lang", lang);
    document.documentElement.setAttribute("lang", lang);
  }

  function setLang(lang) {
    apply(lang);
  }

  function init() {
    const lang = detectLang();
    apply(lang);
    // bind σε οποιοδήποτε στοιχείο έχει data-lang="el|en"
    document.addEventListener("click", (e) => {
      const t = e.target.closest("[data-lang]");
      if (t) {
        const l = t.getAttribute("data-lang");
        if (l === "el" || l === "en") setLang(l);
      }
    });
    window.GEL_I18N = { setLang, getLang: () => localStorage.getItem("gel_lang") || detectLang() };
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
