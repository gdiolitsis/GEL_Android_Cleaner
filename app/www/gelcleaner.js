window.gel = {
  clean: function(mode, ok, err){ cordova.exec(ok, err, "GELCleaner", "clean", [mode]); },
  requestAllFiles: function(ok, err){ cordova.exec(ok, err, "GELCleaner", "requestAllFiles", []); },
  cleanRAM: function(ok, err){ cordova.exec(ok, err, "GELCleaner", "cleanRAM", []); },
  cpuInfo: function(ok, err){ cordova.exec(ok, err, "GELCleaner", "cpuInfo", []); },
  cleanMedia: function(ok, err){ cordova.exec(ok, err, "GELCleaner", "cleanMedia", []); },
  cleanBrowser: function(ok, err){ cordova.exec(ok, err, "GELCleaner", "cleanBrowser", []); },
  cleanApp: function(pkg, ok, err){ cordova.exec(ok, err, "GELCleaner", "cleanApp", [pkg]); }
};
