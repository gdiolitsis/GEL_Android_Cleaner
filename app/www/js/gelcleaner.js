var GELCleaner = {
  fullAccess: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "fullAccess", []),

  cpuInfo: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "cpuInfo", []),

  cpuLiveStart: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "cpuLiveStart", []),

  cpuLiveStop: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "cpuLiveStop", []),

  cleanRam: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "cleanRam", []),

  safeClean: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "safeClean", []),

  aggressiveClean: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "aggressiveClean", []),

  clean: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "clean", []),

  mediaJunkClean: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "mediaJunkClean", []),

  browserCacheClean: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "browserCacheClean", []),

  tempClean: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "tempClean", []),

  batteryBoost: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "batteryBoost", []),

  kill: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "kill", []),

  killApps: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "killApps", []),

  stats: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "stats", []),
};

module.exports = GELCleaner;
