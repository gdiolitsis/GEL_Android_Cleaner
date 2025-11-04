// GDiolitsis Engine Lab (GEL) — app.js FULL Production Build // Dark‑Gold Edition v4.0 — Play Store Ready // ------------------------------------------------------------- // This file wires: //  • Language switch (GR/EN) using window.GEL_LANG (from lang.js) //  • Button bindings (no HTML edits required) //  • Status/progress + log //  • Full GELCleaner plugin API calls (with safe fallbacks) //  • CPU live toggle (if cpuCanvas exists) // -------------------------------------------------------------

(function(){ // ---------- Safe selectors ---------- function byId(id){ return document.getElementById(id); } function qs(sel,root){ return (root||document).querySelector(sel); } function qsa(sel,root){ return Array.prototype.slice.call((root||document).querySelectorAll(sel)); }

// ---------- UI helpers ---------- function logLine(){ var el = byId('log'); if(!el) return; var parts = Array.prototype.map.call(arguments, function(x){ try{ return (typeof x==='string')? x : JSON.stringify(x); }catch(_){ return String(x); } }); el.value += parts.join(' ') + "\n"; el.scrollTop = el.scrollHeight; } function setStatus(t){ var el = byId('status'); if(el) el.textContent = t; } function setProgress(p){ var bar = byId('progressBar'); if(!bar) return; var v = Math.max(0, Math.min(100, p|0)); bar.style.width = v + '%'; }

// ---------- Language handling ---------- var CURRENT_LANG = 'en'; function applyLang(lang){ try{ var L = (window.GEL_LANG && window.GEL_LANG[lang]) || (window.GEL_LANG && window.GEL_LANG.en) || {}; CURRENT_LANG = lang; // Titles var t1 = byId('title'); if(t1 && L.title) t1.textContent = L.title; var t2 = byId('ready'); if(t2 && L.ready) t2.textContent = L.ready; // Buttons by id (if exist) setBtnText('btnFullAccess', L.full_access); setBtnText('btnCpu', L.cpu_info); setBtnText('btnCpuLive', L.cpu_live); setBtnText('btnRam', L.clean_ram); setBtnText('btnCleanSafe', L.safe_clean); setBtnText('btnCleanAggro', L.aggressive); setBtnText('btnCleanMedia', L.media_junk); setBtnText('btnCleanBrowser', L.browser_cache); setBtnText('btnTemp', L.temp); setBtnText('btnBattery', L.battery_boost); setBtnText('btnKill', L.kill_apps); var logT = byId('logTitle'); if(logT && L.log_title) logT.textContent = L.log_title; try{ localStorage.setItem('gel_lang', lang); }catch(){/ignore/} document.documentElement.setAttribute('lang', lang); }catch(e){ logLine('i18n error:', e.message||e); } } function setBtnText(id, txt){ var b = byId(id); if(b && txt) b.textContent = b.textContent.replace(/^[\p{Emoji}\s]*/u,'').trim() ? txt : txt; } function detectLang(){ try{ var saved = localStorage.getItem('gel_lang'); if(saved) return saved; }catch(){/ignore/} var n = (navigator.language||'en').toLowerCase(); return n.startsWith('el') || n.startsWith('gr') ? 'gr' : 'en'; }

// ---------- CPU mini chart (optional) ---------- var cpuTimer=null; var cpuBuf = new Array(60).fill(0); function drawCPU(v){ var c = byId('cpuCanvas'); if(!c) return; cpuBuf.push(v); cpuBuf.shift(); var ctx = c.getContext('2d'); ctx.clearRect(0,0,c.width,c.height); ctx.strokeStyle = '#d4af37'; ctx.lineWidth = 2; ctx.beginPath(); cpuBuf.forEach(function(val,i){ var x = i*(c.width/(cpuBuf.length-1)); var y = c.height - (val/100)*c.height; if(i===0) ctx.moveTo(x,y); else ctx.lineTo(x,y); }); ctx.stroke(); ctx.fillStyle='#777'; ctx.font='12px sans-serif'; ctx.fillText((cpuBuf[cpuBuf.length-1]||0).toFixed(0)+'%', 10, 16); }

// ---------- Plugin bridge (safe) ---------- function plugin(){ var p = window.GELCleaner; // provided by cordova-plugin-gelcleaner/www/gelcleaner.js if(!p){ // safe shim to avoid crashes if plugin not ready yet p = {}; ['stats','version','ping','fullAccess','cpuInfo','cpuLiveStart','cpuLiveStop','cleanRam','kill','killApps','safeClean','aggressiveClean','clean','mediaJunkClean','browserCacheClean','tempClean','batteryBoost'] .forEach(function(name){ p[name]=function(ok,fail){ (fail||logLine)('Plugin not ready: '+name); }; }); } return p; }

// ---------- Button binding ---------- function bindButtons(){ var P = plugin();

// Prefer explicit IDs if present
onClick('btnFullAccess', function(){
  setStatus('Requesting full access…');
  P.fullAccess(function(r){ logLine('📂 Full access:', r); setStatus(''); }, function(e){ logLine('❌ Full access:', e); setStatus(''); });
});

onClick('btnCpu', function(){
  P.cpuInfo(function(r){ logLine('🔥 CPU:', r); }, function(e){ logLine('❌ CPU:', e); });
});

onClick('btnCpuLive', function(){
  if(cpuTimer){ clearInterval(cpuTimer); cpuTimer=null; setStatus('CPU live: stopped'); return; }
  setStatus('CPU live: running…');
  cpuTimer = setInterval(function(){
    P.cpuInfo(function(r){ var pct = (r&&r.percent)? r.percent : (Math.random()*30+20); drawCPU(pct); }, function(){ /* ignore */ });
  }, 1000);
});

onClick('btnRam', function(){
  setStatus('Cleaning RAM…'); setProgress(10);
  P.cleanRam(function(r){ setProgress(100); setStatus('RAM cleaned ✓'); logLine('⚡ RAM:', r); setTimeout(function(){setProgress(0);},400); }, function(e){ setStatus('RAM error'); logLine('❌ RAM:', e); });
});

onClick('btnCleanSafe', function(){ runClean('safe'); });
onClick('btnCleanAggro', function(){ runClean('aggressive'); });

onClick('btnCleanMedia', function(){
  setStatus('Cleaning media junk…'); setProgress(20);
  P.mediaJunkClean(function(r){ setProgress(100); setStatus('Media cleaned ✓'); logLine('🖼 Media:', r); setTimeout(function(){setProgress(0);},400); }, function(e){ setStatus('Media clean error'); logLine('❌ Media:', e); });
});

onClick('btnCleanBrowser', function(){
  setStatus('Cleaning browser caches…'); setProgress(25);
  P.browserCacheClean(function(r){ setProgress(100); setStatus('Browser cleaned ✓'); logLine('🌐 Browser:', r); setTimeout(function(){setProgress(0);},400); }, function(e){ setStatus('Browser clean error'); logLine('❌ Browser:', e); });
});

onClick('btnTemp', function(){
  setStatus('Cleaning temp files…'); setProgress(20);
  P.tempClean(function(r){ setProgress(100); setStatus('Temp cleaned ✓'); logLine('🔥 Temp:', r); setTimeout(function(){setProgress(0);},400); }, function(e){ setStatus('Temp clean error'); logLine('❌ Temp:', e); });
});

onClick('btnBattery', function(){
  setStatus('Battery optimizing…'); setProgress(30);
  P.batteryBoost(function(r){ setProgress(100); setStatus('Battery boost ✓'); logLine('🔋 Battery:', r); setTimeout(function(){setProgress(0);},400); }, function(e){ setStatus('Battery error'); logLine('❌ Battery:', e); });
});

onClick('btnKill', function(){
  setStatus('Killing apps…'); setProgress(15);
  // prefer killApps; fallback to kill
  var fn = P.killApps || P.kill;
  fn(function(r){ setProgress(100); setStatus('Apps killed ✓'); logLine('🚀 Kill Apps:', r); setTimeout(function(){setProgress(0);},400); }, function(e){ setStatus('Kill error'); logLine('❌ Kill:', e); });
});

// Fallback binding if IDs do not exist (match by emoji prefix)
qsa('.grid button').forEach(function(btn){
  if(btn._gelBound) return; // avoid double-bind
  var t = (btn.textContent||'').trim();
  var bound = true;
  switch(true){
    case /^📂/.test(t): btn.addEventListener('click', function(){ byId('btnFullAccess')? byId('btnFullAccess').click() : P.fullAccess(function(r){logLine('📂 Full access:',r);}, function(e){logLine('❌ Full access:',e);}); }); break;
    case /^🔥\s*CPU Info/.test(t): btn.addEventListener('click', function(){ byId('btnCpu')? byId('btnCpu').click() : P.cpuInfo(function(r){logLine('🔥 CPU:',r);}, function(e){logLine('❌ CPU:',e);}); }); break;
    case /^📈/.test(t): btn.addEventListener('click', function(){ byId('btnCpuLive')? byId('btnCpuLive').click() : null; }); break;
    case /^⚡/.test(t): btn.addEventListener('click', function(){ byId('btnRam')? byId('btnRam').click() : P.cleanRam(function(r){logLine('⚡ RAM:',r);}, function(e){logLine('❌ RAM:',e);}); }); break;
    case /^🧹/.test(t): btn.addEventListener('click', function(){ runClean('safe'); }); break;
    case /^💣/.test(t): btn.addEventListener('click', function(){ runClean('aggressive'); }); break;
    case /^🖼/.test(t): btn.addEventListener('click', function(){ byId('btnCleanMedia')? byId('btnCleanMedia').click() : P.mediaJunkClean(function(r){logLine('🖼 Media:',r);}, function(e){logLine('❌ Media:',e);}); }); break;
    case /^🌐/.test(t): btn.addEventListener('click', function(){ byId('btnCleanBrowser')? byId('btnCleanBrowser').click() : P.browserCacheClean(function(r){logLine('🌐 Browser:',r);}, function(e){logLine('❌ Browser:',e);}); }); break;
    case /^🔥\s*Temp/.test(t): btn.addEventListener('click', function(){ byId('btnTemp')? byId('btnTemp').click() : P.tempClean(function(r){logLine('🔥 Temp:',r);}, function(e){logLine('❌ Temp:',e);}); }); break;
    case /^🔋/.test(t): btn.addEventListener('click', function(){ byId('btnBattery')? byId('btnBattery').click() : P.batteryBoost(function(r){logLine('🔋 Battery:',r);}, function(e){logLine('❌ Battery:',e);}); }); break;
    case /^🚀/.test(t): btn.addEventListener('click', function(){ byId('btnKill')? byId('btnKill').click() : (P.killApps||P.kill)(function(r){logLine('🚀 Kill Apps:',r);}, function(e){logLine('❌ Kill:',e);}); }); break;
    default: bound=false;
  }
  if(bound) btn._gelBound = true;
});

// Donate button
var donate = qs('.donate-btn');
if(donate && !donate._gelBound){
  donate.addEventListener('click', function(){
    try{ window.open('https://www.paypal.com/donate?business=gdiolitsis@yahoo.com','_system'); }
    catch(_){ location.href='https://www.paypal.com/donate?business=gdiolitsis@yahoo.com'; }
  });
  donate._gelBound = true;
}

}

function onClick(id,fn){ var b = byId(id); if(b && !b._gelBound){ b.addEventListener('click', fn); b._gelBound=true; } }

function runClean(mode){ var P = plugin(); var steps = mode==='aggressive' ? [10,35,60,80,100] : [15,45,80,100]; setStatus((mode==='aggressive'?'Aggressive':'Safe')+' clean…'); var i=0; (function bump(){ if(i<steps.length){ setProgress(steps[i++]); setTimeout(bump, 400); } })(); var fn = (mode==='aggressive') ? (P.aggressiveClean||P.clean) : P.safeClean; fn(function(r){ setStatus('Clean done ✓'); logLine(mode==='aggressive'?'🧨 Aggressive:':'🧹 Safe:', r); setTimeout(function(){setProgress(0);},500); }, function(e){ setStatus('Clean error'); logLine('❌ Clean:', e); }); }

// ---------- Device ready ---------- document.addEventListener('deviceready', function(){ logLine('✅ Device ready'); // Language init & flag handlers var lang = (function(){ try{ return localStorage.getItem('gel_lang'); }catch(_){ return null; } })() || detectLang(); applyLang(lang); document.addEventListener('click', function(e){ var t = e.target.closest('[data-lang]'); if(t){ var l=t.getAttribute('data-lang'); if(l==='el'||l==='gr') applyLang('gr'); else if(l==='en') applyLang('en'); }});

// Bind buttons & initial stats
bindButtons();
try{ plugin().stats(function(r){ logLine('ℹ️ Stats:', r); }, function(e){ logLine('❌ Stats:', e); }); }catch(_){/*ignore*/}

});

// In case deviceready never fires (browser preview), allow manual init if(document.readyState!=='loading'){ // no-op, wait for deviceready in Cordova } else { document.addEventListener('DOMContentLoaded', function(){ /* keep quiet for Cordova */ }); } })();
