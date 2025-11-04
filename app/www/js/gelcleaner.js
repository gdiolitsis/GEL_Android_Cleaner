var GELCleaner = {

  clearAppCache: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "clearAppCache", []),

  boostRAM: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "boostRAM", []),

  clearTemp: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "clearTemp", []),

  removeJunk: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "removeJunk", []),

  optimizeBattery: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "optimizeBattery", []),

  killBackground: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "killBackground", []),

  stats: (ok, err) =>
    cordova.exec(ok, err, "GELCleaner", "stats", []),
};

module.exports = GELCleaner;
