window.gel = {
  clean: function(mode, ok, err){
    // mode: "safe" | "aggressive"
    cordova.exec(ok, err, "GELCleaner", "clean", [mode]);
  },
  requestAllFiles: function(ok, err){
    cordova.exec(ok, err, "GELCleaner", "requestAllFiles", []);
  },
  cleanRAM: function(ok, err){
    cordova.exec(ok, err, "GELCleaner", "cleanRAM", []);
  },
  cpuInfo: function(ok, err){
    cordova.exec(ok, err, "GELCleaner", "cpuInfo", []);
  }
};
